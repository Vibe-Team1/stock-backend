from typing import Dict, List, Any, Optional
from models import Portfolio, PortfolioHolding, User, users, portfolios


class PortfolioService:
    def __init__(self):
        # Use service manager to get shared instance
        from services.service_manager import service_manager
        self.stock_data_service = service_manager.get_stock_data_service()

    def get_portfolio(self, user_id: str) -> Optional[Portfolio]:
        """Get user's portfolio"""
        return portfolios.get(user_id)

    def get_portfolio_value(self, user_id: str) -> Dict[str, Any]:
        """Calculate current portfolio value"""
        portfolio = portfolios.get(user_id)
        user = users.get(user_id)

        if not portfolio or not user:
            return {"total_value": 0, "cash_balance": 0, "stock_value": 0, "holdings": []}

        stock_value = 0
        holdings_detail = []

        for ticker, holding in portfolio.holdings.items():
            current_price = self.stock_data_service.get_current_price(ticker)
            if current_price:
                market_value = current_price * holding.quantity
                stock_value += market_value

                gain_loss = market_value - holding.total_cost
                gain_loss_percentage = (gain_loss / holding.total_cost) * 100 if holding.total_cost > 0 else 0

                holdings_detail.append({
                    "ticker": ticker,
                    "quantity": holding.quantity,
                    "average_buy_price": holding.average_buy_price,
                    "current_price": current_price,
                    "market_value": market_value,
                    "total_cost": holding.total_cost,
                    "gain_loss": gain_loss,
                    "gain_loss_percentage": gain_loss_percentage
                })

        total_value = user.available_balance + stock_value

        return {
            "total_value": total_value,
            "cash_balance": user.available_balance,
            "stock_value": stock_value,
            "initial_cash": user.initial_cash,
            "total_gain_loss": total_value - user.initial_cash,
            "total_gain_loss_percentage": ((
                                                       total_value - user.initial_cash) / user.initial_cash) * 100 if user.initial_cash > 0 else 0,
            "holdings": holdings_detail
        }

    def get_account_summary(self, user_id: str) -> Dict[str, Any]:
        """Get complete account summary"""
        user = users.get(user_id)
        if not user:
            return {}

        portfolio_value = self.get_portfolio_value(user_id)

        return {
            "user_id": user_id,
            "username": user.username,
            "email": user.email,
            "created_at": user.created_at.isoformat(),
            "portfolio": portfolio_value
        }
