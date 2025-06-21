from app import db
from models.user import User
from models.portfolio import PortfolioItem
from models.order import Order, OrderType, OrderSide, OrderStatus
from services.stock_service import StockService
from datetime import datetime
from typing import Dict, Optional, Tuple

class TradingService:
    
    @staticmethod
    def place_buy_order(user_id: int, ticker: str, quantity: int, order_type: str, limit_price: Optional[float] = None) -> Dict:
        """Place a buy order"""
        user = User.query.get(user_id)
        if not user:
            raise ValueError("User not found")
        
        stock_name = StockService.get_stock_name(ticker)
        if not stock_name:
            raise ValueError("Invalid ticker")
        
        # Validate order parameters
        if quantity <= 0:
            raise ValueError("Quantity must be positive")
        
        if order_type == OrderType.LIMIT.value and not limit_price:
            raise ValueError("Limit price required for LIMIT orders")
        
        if order_type == OrderType.LIMIT.value and limit_price <= 0:
            raise ValueError("Limit price must be positive")
        
        # Create order
        order = Order(
            user_id=user_id,
            ticker=ticker,
            stock_name=stock_name,
            order_type=OrderType(order_type),
            side=OrderSide.BUY,
            quantity=quantity,
            limit_price=limit_price
        )
        
        db.session.add(order)
        
        # Try to execute immediately for MARKET orders
        if order_type == OrderType.MARKET.value:
            success, message = TradingService._execute_buy_order(order, user)
            if not success:
                order.reject()
                db.session.commit()
                raise ValueError(message)
        else:
            # For LIMIT orders, check if current price meets limit
            current_price = StockService.get_current_price(ticker)
            if current_price and current_price <= limit_price:
                success, message = TradingService._execute_buy_order(order, user)
                if not success:
                    order.reject()
                    db.session.commit()
                    raise ValueError(message)
        
        db.session.commit()
        return order.to_dict()
    
    @staticmethod
    def place_sell_order(user_id: int, ticker: str, quantity: int, order_type: str, limit_price: Optional[float] = None) -> Dict:
        """Place a sell order"""
        user = User.query.get(user_id)
        if not user:
            raise ValueError("User not found")
        
        stock_name = StockService.get_stock_name(ticker)
        if not stock_name:
            raise ValueError("Invalid ticker")
        
        # Check if user has enough shares
        portfolio_item = PortfolioItem.query.filter_by(user_id=user_id, ticker=ticker).first()
        if not portfolio_item or portfolio_item.quantity < quantity:
            raise ValueError("Insufficient shares")
        
        # Validate order parameters
        if quantity <= 0:
            raise ValueError("Quantity must be positive")
        
        if order_type == OrderType.LIMIT.value and not limit_price:
            raise ValueError("Limit price required for LIMIT orders")
        
        if order_type == OrderType.LIMIT.value and limit_price <= 0:
            raise ValueError("Limit price must be positive")
        
        # Create order
        order = Order(
            user_id=user_id,
            ticker=ticker,
            stock_name=stock_name,
            order_type=OrderType(order_type),
            side=OrderSide.SELL,
            quantity=quantity,
            limit_price=limit_price
        )
        
        db.session.add(order)
        
        # Try to execute immediately for MARKET orders
        if order_type == OrderType.MARKET.value:
            success, message = TradingService._execute_sell_order(order, user)
            if not success:
                order.reject()
                db.session.commit()
                raise ValueError(message)
        else:
            # For LIMIT orders, check if current price meets limit
            current_price = StockService.get_current_price(ticker)
            if current_price and current_price >= limit_price:
                success, message = TradingService._execute_sell_order(order, user)
                if not success:
                    order.reject()
                    db.session.commit()
                    raise ValueError(message)
        
        db.session.commit()
        return order.to_dict()
    
    @staticmethod
    def _execute_buy_order(order: Order, user: User) -> Tuple[bool, str]:
        """Execute a buy order"""
        current_price = StockService.get_current_price(order.ticker)
        if not current_price:
            return False, "Unable to get current price"
        
        # Check if limit price is met (for LIMIT orders)
        if order.order_type == OrderType.LIMIT and current_price > order.limit_price:
            return False, "Current price exceeds limit price"
        
        total_cost = current_price * order.quantity
        
        # Check if user has enough balance
        if user.available_balance < total_cost:
            return False, "Insufficient balance"
        
        # Execute the order
        order.execute(current_price)
        
        # Update user balance
        user.update_balance(-total_cost)
        
        # Update portfolio
        portfolio_item = PortfolioItem.query.filter_by(user_id=user.id, ticker=order.ticker).first()
        if portfolio_item:
            portfolio_item.add_shares(order.quantity, current_price)
        else:
            portfolio_item = PortfolioItem(
                user_id=user.id,
                ticker=order.ticker,
                stock_name=order.stock_name,
                quantity=order.quantity,
                average_buy_price=current_price
            )
            db.session.add(portfolio_item)
        
        return True, "Order executed successfully"
    
    @staticmethod
    def _execute_sell_order(order: Order, user: User) -> Tuple[bool, str]:
        """Execute a sell order"""
        current_price = StockService.get_current_price(order.ticker)
        if not current_price:
            return False, "Unable to get current price"
        
        # Check if limit price is met (for LIMIT orders)
        if order.order_type == OrderType.LIMIT and current_price < order.limit_price:
            return False, "Current price below limit price"
        
        # Check if user has enough shares
        portfolio_item = PortfolioItem.query.filter_by(user_id=user.id, ticker=order.ticker).first()
        if not portfolio_item or portfolio_item.quantity < order.quantity:
            return False, "Insufficient shares"
        
        total_proceeds = current_price * order.quantity
        
        # Execute the order
        order.execute(current_price)
        
        # Update user balance
        user.update_balance(total_proceeds)
        
        # Update portfolio
        portfolio_item.remove_shares(order.quantity)
        
        return True, "Order executed successfully"
    
    @staticmethod
    def process_pending_orders():
        """Process all pending orders (for LIMIT orders that haven't been executed yet)"""
        pending_orders = Order.query.filter_by(status=OrderStatus.PENDING).all()
        
        for order in pending_orders:
            user = User.query.get(order.user_id)
            if not user:
                continue
            
            current_price = StockService.get_current_price(order.ticker)
            if not current_price:
                continue
            
            # Check if LIMIT order conditions are met
            if order.order_type == OrderType.LIMIT:
                if order.side == OrderSide.BUY and current_price <= order.limit_price:
                    success, _ = TradingService._execute_buy_order(order, user)
                elif order.side == OrderSide.SELL and current_price >= order.limit_price:
                    success, _ = TradingService._execute_sell_order(order, user)
        
        db.session.commit() 