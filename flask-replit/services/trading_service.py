import uuid
from datetime import datetime
from typing import Optional, Dict, Any, List
from models import (
    User, Portfolio, PortfolioHolding, Order, OrderType, OrderSide, OrderStatus,
    users, portfolios, orders, order_history
)


class TradingService:
    def __init__(self):
        # Use service manager to get shared instances
        from services.service_manager import service_manager
        self.stock_data_service = service_manager.get_stock_data_service()
        self.portfolio_service = service_manager.get_portfolio_service()

    def create_user(self, username: str, email: str, initial_cash: float = 100000.0) -> User:
        """Create a new user with initial cash balance"""
        user_id = str(uuid.uuid4())
        user = User(
            user_id=user_id,
            username=username,
            email=email,
            initial_cash=initial_cash,
            available_balance=initial_cash
        )
        users[user_id] = user
        portfolios[user_id] = Portfolio(user_id=user_id)
        return user

    def get_user(self, user_id: str) -> Optional[User]:
        """Get user by ID"""
        return users.get(user_id)

    def place_buy_order(self, user_id: str, ticker: str, quantity: int,
                        order_type: OrderType, price: Optional[float] = None) -> Dict[str, Any]:
        """Place a buy order"""
        try:
            user = users.get(user_id)
            if not user:
                return {"success": False, "message": "User not found"}

            # Validate stock exists
            current_price = self.stock_data_service.get_current_price(ticker)
            if current_price is None:
                return {"success": False, "message": "Stock not found"}

            # Calculate required funds
            if order_type == OrderType.MARKET:
                required_funds = current_price * quantity
            else:  # LIMIT order
                if price is None:
                    return {"success": False, "message": "Price required for limit orders"}
                required_funds = price * quantity

            # Check sufficient funds
            if user.available_balance < required_funds:
                return {"success": False, "message": "Insufficient funds"}

            # Create order
            order_id = str(uuid.uuid4())
            order = Order(
                order_id=order_id,
                user_id=user_id,
                ticker=ticker,
                side=OrderSide.BUY,
                order_type=order_type,
                quantity=quantity,
                price=price,
                status=OrderStatus.PENDING,
                created_at=datetime.now()
            )

            orders[order_id] = order

            # Reserve funds
            user.available_balance -= required_funds

            # Execute market order immediately
            if order_type == OrderType.MARKET:
                self._execute_order(order, current_price)

            return {"success": True, "order_id": order_id, "message": "Order placed successfully"}

        except Exception as e:
            return {"success": False, "message": f"Error placing order: {str(e)}"}

    def place_sell_order(self, user_id: str, ticker: str, quantity: int,
                         order_type: OrderType, price: Optional[float] = None) -> Dict[str, Any]:
        """Place a sell order"""
        try:
            user = users.get(user_id)
            if not user:
                return {"success": False, "message": "User not found"}

            portfolio = portfolios.get(user_id)
            if not portfolio or ticker not in portfolio.holdings:
                return {"success": False, "message": "Stock not owned"}

            holding = portfolio.holdings[ticker]
            if holding.quantity < quantity:
                return {"success": False, "message": "Insufficient stock quantity"}

            # Validate stock exists
            current_price = self.stock_data_service.get_current_price(ticker)
            if current_price is None:
                return {"success": False, "message": "Stock not found"}

            # Create order
            order_id = str(uuid.uuid4())
            order = Order(
                order_id=order_id,
                user_id=user_id,
                ticker=ticker,
                side=OrderSide.SELL,
                order_type=order_type,
                quantity=quantity,
                price=price,
                status=OrderStatus.PENDING,
                created_at=datetime.now()
            )

            orders[order_id] = order

            # Execute market order immediately
            if order_type == OrderType.MARKET:
                self._execute_order(order, current_price)

            return {"success": True, "order_id": order_id, "message": "Order placed successfully"}

        except Exception as e:
            return {"success": False, "message": f"Error placing order: {str(e)}"}

    def _execute_order(self, order: Order, execution_price: float):
        """Execute an order at the given price"""
        try:
            user = users[order.user_id]
            portfolio = portfolios[order.user_id]

            if order.side == OrderSide.BUY:
                # Update portfolio
                if order.ticker in portfolio.holdings:
                    holding = portfolio.holdings[order.ticker]
                    new_total_quantity = holding.quantity + order.quantity
                    new_total_cost = holding.total_cost + (execution_price * order.quantity)
                    holding.quantity = new_total_quantity
                    holding.average_buy_price = new_total_cost / new_total_quantity
                    holding.total_cost = new_total_cost
                else:
                    portfolio.holdings[order.ticker] = PortfolioHolding(
                        ticker=order.ticker,
                        quantity=order.quantity,
                        average_buy_price=execution_price,
                        total_cost=execution_price * order.quantity
                    )

                # For market orders, adjust balance (already reserved for limit orders)
                if order.order_type == OrderType.MARKET:
                    total_cost = execution_price * order.quantity
                else:
                    # Refund difference for limit orders
                    if order.price is not None:
                        reserved_amount = order.price * order.quantity
                        actual_cost = execution_price * order.quantity
                        user.available_balance += (reserved_amount - actual_cost)

            else:  # SELL
                holding = portfolio.holdings[order.ticker]
                holding.quantity -= order.quantity

                # Remove holding if quantity becomes zero
                if holding.quantity == 0:
                    del portfolio.holdings[order.ticker]
                elif holding.quantity < 0:
                    # This shouldn't happen with proper validation
                    raise ValueError("Negative stock quantity")

                # Add proceeds to balance
                proceeds = execution_price * order.quantity
                user.available_balance += proceeds

            # Update order status
            order.status = OrderStatus.FILLED
            order.filled_at = datetime.now()
            order.filled_price = execution_price
            order.filled_quantity = order.quantity

            # Move to history
            order_history.append(order)
            if order.order_id in orders:
                del orders[order.order_id]

        except Exception as e:
            order.status = OrderStatus.REJECTED
            # Refund reserved funds for buy orders
            if order.side == OrderSide.BUY and order.order_type == OrderType.LIMIT and order.price is not None:
                user = users[order.user_id]
                user.available_balance += order.price * order.quantity
            raise e

    def get_pending_orders(self, user_id: str) -> List[Order]:
        """Get all pending orders for a user"""
        return [order for order in orders.values()
                if order.user_id == user_id and order.status == OrderStatus.PENDING]

    def get_order_history(self, user_id: str) -> List[Order]:
        """Get order history for a user"""
        return [order for order in order_history if order.user_id == user_id]

    def cancel_order(self, user_id: str, order_id: str) -> Dict[str, Any]:
        """Cancel a pending order"""
        try:
            order = orders.get(order_id)
            if not order or order.user_id != user_id:
                return {"success": False, "message": "Order not found"}

            if order.status != OrderStatus.PENDING:
                return {"success": False, "message": "Order cannot be cancelled"}

            # Refund reserved funds for buy orders
            if order.side == OrderSide.BUY:
                user = users[user_id]
                if order.order_type == OrderType.LIMIT and order.price is not None:
                    refund_amount = order.price * order.quantity
                else:
                    refund_amount = 0
                user.available_balance += refund_amount

            order.status = OrderStatus.CANCELLED
            order_history.append(order)
            del orders[order_id]

            return {"success": True, "message": "Order cancelled successfully"}

        except Exception as e:
            return {"success": False, "message": f"Error cancelling order: {str(e)}"}
