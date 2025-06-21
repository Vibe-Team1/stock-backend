from app import db
from datetime import datetime
from enum import Enum

class OrderType(Enum):
    MARKET = "MARKET"
    LIMIT = "LIMIT"

class OrderSide(Enum):
    BUY = "BUY"
    SELL = "SELL"

class OrderStatus(Enum):
    PENDING = "PENDING"
    FILLED = "FILLED"
    CANCELLED = "CANCELLED"
    REJECTED = "REJECTED"

class Order(db.Model):
    __tablename__ = 'orders'
    
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    ticker = db.Column(db.String(20), nullable=False)
    stock_name = db.Column(db.String(100), nullable=False)
    order_type = db.Column(db.Enum(OrderType), nullable=False)
    side = db.Column(db.Enum(OrderSide), nullable=False)
    quantity = db.Column(db.Integer, nullable=False)
    limit_price = db.Column(db.Float, nullable=True)  # Only for LIMIT orders
    executed_price = db.Column(db.Float, nullable=True)
    status = db.Column(db.Enum(OrderStatus), default=OrderStatus.PENDING)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    executed_at = db.Column(db.DateTime, nullable=True)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'ticker': self.ticker,
            'stock_name': self.stock_name,
            'order_type': self.order_type.value,
            'side': self.side.value,
            'quantity': self.quantity,
            'limit_price': self.limit_price,
            'executed_price': self.executed_price,
            'status': self.status.value,
            'total_value': self.executed_price * self.quantity if self.executed_price else None,
            'created_at': self.created_at.isoformat(),
            'executed_at': self.executed_at.isoformat() if self.executed_at else None,
            'updated_at': self.updated_at.isoformat()
        }
    
    def execute(self, price):
        """Execute the order at the given price"""
        self.executed_price = price
        self.status = OrderStatus.FILLED
        self.executed_at = datetime.utcnow()
        self.updated_at = datetime.utcnow()
    
    def cancel(self):
        """Cancel the order"""
        self.status = OrderStatus.CANCELLED
        self.updated_at = datetime.utcnow()
    
    def reject(self):
        """Reject the order"""
        self.status = OrderStatus.REJECTED
        self.updated_at = datetime.utcnow() 