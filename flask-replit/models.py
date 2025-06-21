from datetime import datetime
from enum import Enum
from dataclasses import dataclass, field
from typing import Dict, List, Optional

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

@dataclass
class User:
    user_id: str
    username: str
    email: str
    initial_cash: float
    available_balance: float
    created_at: datetime = field(default_factory=datetime.now)

@dataclass
class Portfolio:
    user_id: str
    holdings: Dict[str, 'PortfolioHolding'] = field(default_factory=dict)

@dataclass
class PortfolioHolding:
    ticker: str
    quantity: int
    average_buy_price: float
    total_cost: float

@dataclass
class Order:
    order_id: str
    user_id: str
    ticker: str
    side: OrderSide
    order_type: OrderType
    quantity: int
    price: Optional[float]  # None for market orders
    status: OrderStatus
    created_at: datetime
    filled_at: Optional[datetime] = None
    filled_price: Optional[float] = None
    filled_quantity: int = 0

@dataclass
class StockPrice:
    ticker: str
    name: str
    market_type: str
    trade_date: datetime
    open_price: float
    high_price: float
    low_price: float
    close_price: float
    volume: int

# In-memory storage
users: Dict[str, User] = {}
portfolios: Dict[str, Portfolio] = {}
orders: Dict[str, Order] = {}
order_history: List[Order] = []
stock_prices: Dict[str, List[StockPrice]] = {}
