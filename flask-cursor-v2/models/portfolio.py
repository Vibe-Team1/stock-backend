from app import db
from datetime import datetime

class PortfolioItem(db.Model):
    __tablename__ = 'portfolio_items'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    ticker = db.Column(db.String(20), nullable=False)
    stock_name = db.Column(db.String(100), nullable=False)
    quantity = db.Column(db.Integer, nullable=False, default=0)
    average_buy_price = db.Column(db.Float, nullable=False, default=0.0)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    # Composite unique constraint
    __table_args__ = (db.UniqueConstraint('user_id', 'ticker', name='_user_ticker_uc'),)
    
    @property
    def current_value(self):
        """Calculate current value based on latest price"""
        from services.stock_service import StockService
        current_price = StockService.get_current_price(self.ticker)
        return self.quantity * current_price if current_price else 0
    
    @property
    def total_invested(self):
        """Calculate total amount invested in this stock"""
        return self.quantity * self.average_buy_price
    
    @property
    def profit_loss(self):
        """Calculate profit/loss for this stock"""
        return self.current_value - self.total_invested
    
    def to_dict(self):
        from services.stock_service import StockService
        current_price = StockService.get_current_price(self.ticker)
        
        return {
            'id': self.id,
            'ticker': self.ticker,
            'stock_name': self.stock_name,
            'quantity': self.quantity,
            'average_buy_price': self.average_buy_price,
            'current_price': current_price,
            'current_value': self.current_value,
            'total_invested': self.total_invested,
            'profit_loss': self.profit_loss,
            'profit_loss_percentage': (self.profit_loss / self.total_invested * 100) if self.total_invested > 0 else 0,
            'created_at': self.created_at.isoformat(),
            'updated_at': self.updated_at.isoformat()
        }
    
    def add_shares(self, quantity, price):
        """Add shares to portfolio and recalculate average price"""
        if quantity <= 0:
            raise ValueError("Quantity must be positive")
        
        total_quantity = self.quantity + quantity
        total_cost = (self.quantity * self.average_buy_price) + (quantity * price)
        
        self.quantity = total_quantity
        self.average_buy_price = total_cost / total_quantity if total_quantity > 0 else 0
        self.updated_at = datetime.utcnow()
    
    def remove_shares(self, quantity):
        """Remove shares from portfolio"""
        if quantity <= 0:
            raise ValueError("Quantity must be positive")
        
        if quantity > self.quantity:
            raise ValueError("Insufficient shares")
        
        self.quantity -= quantity
        self.updated_at = datetime.utcnow()
        
        # If no shares left, delete the portfolio item
        if self.quantity == 0:
            db.session.delete(self) 