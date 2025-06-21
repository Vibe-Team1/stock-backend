from app import db
from datetime import datetime

class User(db.Model):
    __tablename__ = 'users'
    
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)
    initial_balance = db.Column(db.Float, nullable=False, default=10000000.0)  # 10M KRW default
    available_balance = db.Column(db.Float, nullable=False, default=10000000.0)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    # Relationships
    portfolio_items = db.relationship('PortfolioItem', backref='user', lazy=True, cascade='all, delete-orphan')
    orders = db.relationship('Order', backref='user', lazy=True, cascade='all, delete-orphan')
    
    def to_dict(self):
        return {
            'id': self.id,
            'username': self.username,
            'email': self.email,
            'initial_balance': self.initial_balance,
            'available_balance': self.available_balance,
            'total_portfolio_value': self.calculate_total_portfolio_value(),
            'created_at': self.created_at.isoformat(),
            'updated_at': self.updated_at.isoformat()
        }
    
    def calculate_total_portfolio_value(self):
        """Calculate total portfolio value including cash and stocks"""
        portfolio_value = sum(item.current_value for item in self.portfolio_items)
        return portfolio_value + self.available_balance
    
    def update_balance(self, amount):
        """Update available balance"""
        self.available_balance += amount
        self.updated_at = datetime.utcnow()
        if self.available_balance < 0:
            raise ValueError("Insufficient balance") 