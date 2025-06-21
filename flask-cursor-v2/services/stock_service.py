import pandas as pd
import os
from datetime import datetime, timedelta
from typing import Optional, Dict, List

class StockService:
    _stock_data = None
    _price_data = None
    
    @classmethod
    def _load_stock_data(cls):
        """Load stock basic information from CSV"""
        if cls._stock_data is None:
            csv_path = '국내주식기본조회v1.csv'
            if os.path.exists(csv_path):
                df = pd.read_csv(csv_path)
                cls._stock_data = df.set_index('종목코드').to_dict('index')
            else:
                cls._stock_data = {}
        return cls._stock_data
    
    @classmethod
    def _load_price_data(cls):
        """Load stock price data from CSV"""
        if cls._price_data is None:
            csv_path = '국내주식분봉차트v1.csv'
            if os.path.exists(csv_path):
                df = pd.read_csv(csv_path)
                # Convert date and time columns
                df['datetime'] = pd.to_datetime(df['영업일자'].astype(str) + ' ' + df['체결시간'].astype(str), 
                                              format='%Y%m%d %H%M%S')
                cls._price_data = df
            else:
                cls._price_data = pd.DataFrame()
        return cls._price_data
    
    @classmethod
    def get_stock_info(cls, ticker: str) -> Optional[Dict]:
        """Get stock basic information"""
        stock_data = cls._load_stock_data()
        return stock_data.get(ticker)
    
    @classmethod
    def get_current_price(cls, ticker: str) -> Optional[float]:
        """Get current price for a stock (latest available price)"""
        price_data = cls._load_price_data()
        if price_data.empty:
            return None
        
        stock_prices = price_data[price_data['종목코드'] == ticker]
        if stock_prices.empty:
            return None
        
        # Get the latest price
        latest = stock_prices.sort_values('datetime').iloc[-1]
        return float(latest['현재가'])
    
    @classmethod
    def get_price_at_time(cls, ticker: str, timestamp: datetime) -> Optional[float]:
        """Get stock price at a specific timestamp"""
        price_data = cls._load_price_data()
        if price_data.empty:
            return None
        
        stock_prices = price_data[price_data['종목코드'] == ticker]
        if stock_prices.empty:
            return None
        
        # Find the closest price before or at the given timestamp
        stock_prices = stock_prices[stock_prices['datetime'] <= timestamp]
        if stock_prices.empty:
            return None
        
        closest = stock_prices.sort_values('datetime').iloc[-1]
        return float(closest['현재가'])
    
    @classmethod
    def get_price_range(cls, ticker: str, start_time: datetime, end_time: datetime) -> List[Dict]:
        """Get price data for a stock within a time range"""
        price_data = cls._load_price_data()
        if price_data.empty:
            return []
        
        stock_prices = price_data[
            (price_data['종목코드'] == ticker) & 
            (price_data['datetime'] >= start_time) & 
            (price_data['datetime'] <= end_time)
        ]
        
        if stock_prices.empty:
            return []
        
        return stock_prices.sort_values('datetime').to_dict('records')
    
    @classmethod
    def get_all_tickers(cls) -> List[str]:
        """Get list of all available stock tickers"""
        stock_data = cls._load_stock_data()
        return list(stock_data.keys())
    
    @classmethod
    def get_stock_name(cls, ticker: str) -> Optional[str]:
        """Get stock name from ticker"""
        stock_info = cls.get_stock_info(ticker)
        return stock_info.get('종목명') if stock_info else None 