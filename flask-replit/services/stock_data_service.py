import csv
import random
import logging
from datetime import datetime, timedelta
from typing import Optional, List, Dict, Any
from sqlalchemy import text
from models import StockPrice, stock_prices
from database import db_manager


class StockDataService:
    def __init__(self):
        self.current_prices = {}
        self.use_database = db_manager.is_available()
        logging.info(f"Stock data service initialized with database: {self.use_database}")

    def load_historical_data(self):
        """Load historical stock data from database or generate sample data"""
        try:
            stock_prices.clear()
            self.current_prices.clear()

            if self.use_database:
                self._load_from_database()
            else:
                self._load_from_csv_fallback()

            logging.info(
                f"Loaded historical data for {len(stock_prices)} stocks with current prices: {self.current_prices}")

        except Exception as e:
            logging.error(f"Error loading historical data: {e}")
            self._generate_fallback_data()

    def _load_from_database(self):
        """Load stock data from database"""
        session = db_manager.get_session()
        if not session:
            self._load_from_csv_fallback()
            return

        try:
            # Check if database has data
            count_result = session.execute(text("SELECT COUNT(*) FROM stocks")).fetchone()
            count = count_result[0] if count_result else 0

            if count == 0:
                logging.info("No data in database, generating sample data...")
                session.close()
                self._populate_database_with_sample_data()
                session = db_manager.get_session()

            # Load all stock data
            query = text("""
                SELECT ticker, name, market_type, trade_date, 
                       open_price, high_price, low_price, close_price, volume
                FROM stocks 
                ORDER BY ticker, trade_date
            """)

            results = session.execute(query).fetchall()

            for row in results:
                ticker = row[0]
                if ticker not in stock_prices:
                    stock_prices[ticker] = []

                # Handle date conversion
                trade_date = row[3]
                if isinstance(trade_date, str):
                    trade_date = datetime.strptime(trade_date, '%Y-%m-%d')
                elif hasattr(trade_date, 'date'):
                    trade_date = datetime.combine(trade_date, datetime.min.time())

                stock_price = StockPrice(
                    ticker=ticker,
                    name=row[1],
                    market_type=row[2],
                    trade_date=trade_date,
                    open_price=float(row[4]),
                    high_price=float(row[5]),
                    low_price=float(row[6]),
                    close_price=float(row[7]),
                    volume=int(row[8])
                )
                stock_prices[ticker].append(stock_price)

            # Update current prices (latest price for each ticker)
            for ticker in stock_prices:
                if stock_prices[ticker]:
                    stock_prices[ticker].sort(key=lambda x: x.trade_date)
                    self.current_prices[ticker] = stock_prices[ticker][-1].close_price

        except Exception as e:
            logging.error(f"Error loading from database: {e}")
            self._load_from_csv_fallback()
        finally:
            if session:
                session.close()

    def _populate_database_with_sample_data(self):
        """Generate and save sample stock data to database"""
        session = db_manager.get_session()
        if not session:
            return

        sample_stocks = [
            {'ticker': 'AAPL', 'name': 'Apple Inc.', 'base_price': 150.0},
            {'ticker': 'GOOGL', 'name': 'Alphabet Inc.', 'base_price': 2500.0},
            {'ticker': 'MSFT', 'name': 'Microsoft Corporation', 'base_price': 300.0},
            {'ticker': 'TSLA', 'name': 'Tesla Inc.', 'base_price': 800.0},
            {'ticker': 'AMZN', 'name': 'Amazon.com Inc.', 'base_price': 3200.0},
            {'ticker': 'META', 'name': 'Meta Platforms Inc.', 'base_price': 250.0},
            {'ticker': 'NVDA', 'name': 'NVIDIA Corporation', 'base_price': 400.0},
            {'ticker': 'NFLX', 'name': 'Netflix Inc.', 'base_price': 450.0}
        ]

        try:
            base_date = datetime.now() - timedelta(days=30)

            for stock in sample_stocks:
                current_price = stock['base_price']

                for i in range(30):
                    trade_date = base_date + timedelta(days=i)

                    # Skip weekends
                    if trade_date.weekday() >= 5:
                        continue

                    # Generate realistic price movement
                    daily_change = random.uniform(-0.05, 0.05)
                    open_price = current_price
                    close_price = current_price * (1 + daily_change)
                    high_price = max(open_price, close_price) * random.uniform(1.0, 1.03)
                    low_price = min(open_price, close_price) * random.uniform(0.97, 1.0)
                    volume = random.randint(1000000, 10000000)

                    # Insert into database
                    insert_sql = """
                        INSERT OR REPLACE INTO stocks 
                        (ticker, name, market_type, trade_date, open_price, high_price, low_price, close_price, volume)
                        VALUES (:ticker, :name, :market_type, :trade_date, :open_price, :high_price, :low_price, :close_price, :volume)
                    """

                    if db_manager.db_type == 'mysql':
                        insert_sql = insert_sql.replace('INSERT OR REPLACE', 'REPLACE')

                    session.execute(text(insert_sql), {
                        'ticker': stock['ticker'],
                        'name': stock['name'],
                        'market_type': 'NASDAQ',
                        'trade_date': trade_date.strftime('%Y-%m-%d'),
                        'open_price': round(open_price, 2),
                        'high_price': round(high_price, 2),
                        'low_price': round(low_price, 2),
                        'close_price': round(close_price, 2),
                        'volume': volume
                    })

                    current_price = close_price

            session.commit()
            logging.info("Sample data populated in database successfully")

        except Exception as e:
            logging.error(f"Error populating database: {e}")
            session.rollback()
        finally:
            session.close()

    def _load_from_csv_fallback(self):
        """Load stock data from CSV file as fallback"""
        try:
            with open('data/stock_prices.csv', 'r') as file:
                reader = csv.DictReader(file)
                for row in reader:
                    ticker = row['ticker']
                    if ticker not in stock_prices:
                        stock_prices[ticker] = []

                    stock_price = StockPrice(
                        ticker=ticker,
                        name=row['name'],
                        market_type=row['market_type'],
                        trade_date=datetime.strptime(row['trade_date'], '%Y-%m-%d'),
                        open_price=float(row['open']),
                        high_price=float(row['high']),
                        low_price=float(row['low']),
                        close_price=float(row['close']),
                        volume=int(row['volume'])
                    )
                    stock_prices[ticker].append(stock_price)

            # Update current prices
            for ticker in stock_prices:
                stock_prices[ticker].sort(key=lambda x: x.trade_date)
                if stock_prices[ticker]:
                    self.current_prices[ticker] = stock_prices[ticker][-1].close_price

        except FileNotFoundError:
            self._generate_fallback_data()

    def _generate_fallback_data(self):
        """Generate minimal fallback data in memory"""
        sample_stocks = [
            {'ticker': 'AAPL', 'name': 'Apple Inc.', 'price': 150.0},
            {'ticker': 'GOOGL', 'name': 'Alphabet Inc.', 'price': 2500.0},
            {'ticker': 'MSFT', 'name': 'Microsoft Corporation', 'price': 300.0},
            {'ticker': 'TSLA', 'name': 'Tesla Inc.', 'price': 800.0}
        ]

        for stock in sample_stocks:
            ticker = stock['ticker']
            stock_prices[ticker] = []
            price = stock['price']

            stock_price = StockPrice(
                ticker=ticker,
                name=stock['name'],
                market_type='NASDAQ',
                trade_date=datetime.now(),
                open_price=price,
                high_price=price * 1.02,
                low_price=price * 0.98,
                close_price=price,
                volume=1000000
            )
            stock_prices[ticker].append(stock_price)
            self.current_prices[ticker] = price

    def add_stock_data(self, ticker: str, name: str, market_type: str, trade_date: datetime,
                       open_price: float, high_price: float, low_price: float,
                       close_price: float, volume: int) -> Dict[str, Any]:
        """Add new stock data to database"""
        if not self.use_database:
            return {"success": False, "message": "Database not available"}

        session = db_manager.get_session()
        if not session:
            return {"success": False, "message": "Database session not available"}

        try:
            # Insert into database
            insert_sql = """
                INSERT OR REPLACE INTO stocks 
                (ticker, name, market_type, trade_date, open_price, high_price, low_price, close_price, volume)
                VALUES (:ticker, :name, :market_type, :trade_date, :open_price, :high_price, :low_price, :close_price, :volume)
            """

            if db_manager.db_type == 'mysql':
                insert_sql = insert_sql.replace('INSERT OR REPLACE', 'REPLACE')

            session.execute(text(insert_sql), {
                'ticker': ticker,
                'name': name,
                'market_type': market_type,
                'trade_date': trade_date.strftime('%Y-%m-%d'),
                'open_price': round(open_price, 2),
                'high_price': round(high_price, 2),
                'low_price': round(low_price, 2),
                'close_price': round(close_price, 2),
                'volume': volume
            })

            session.commit()

            # Update in-memory data
            if ticker not in stock_prices:
                stock_prices[ticker] = []

            new_stock_price = StockPrice(
                ticker=ticker,
                name=name,
                market_type=market_type,
                trade_date=trade_date,
                open_price=open_price,
                high_price=high_price,
                low_price=low_price,
                close_price=close_price,
                volume=volume
            )

            # Insert in chronological order
            inserted = False
            for i, existing_price in enumerate(stock_prices[ticker]):
                if trade_date <= existing_price.trade_date:
                    stock_prices[ticker].insert(i, new_stock_price)
                    inserted = True
                    break

            if not inserted:
                stock_prices[ticker].append(new_stock_price)

            # Update current price if this is the latest data
            stock_prices[ticker].sort(key=lambda x: x.trade_date)
            if stock_prices[ticker] and stock_prices[ticker][-1].trade_date == trade_date:
                self.current_prices[ticker] = close_price

            return {"success": True, "message": "Stock data added successfully"}

        except Exception as e:
            session.rollback()
            return {"success": False, "message": f"Error adding stock data: {str(e)}"}
        finally:
            session.close()

    def get_current_price(self, ticker: str) -> Optional[float]:
        """Get current price for a stock"""
        return self.current_prices.get(ticker)

    def get_price_at_timestamp(self, ticker: str, timestamp: datetime) -> Optional[float]:
        """Get stock price at a specific timestamp"""
        if ticker not in stock_prices:
            return None

        stock_data = stock_prices[ticker]
        closest_price = None
        min_diff = float('inf')

        for price_data in stock_data:
            diff = abs((price_data.trade_date - timestamp).total_seconds())
            if diff < min_diff:
                min_diff = diff
                closest_price = price_data.close_price

        return closest_price

    def get_stock_info(self, ticker: str) -> Optional[Dict]:
        """Get basic stock information"""
        if ticker not in stock_prices or not stock_prices[ticker]:
            return None

        latest = stock_prices[ticker][-1]
        return {
            'ticker': ticker,
            'name': latest.name,
            'market_type': latest.market_type,
            'current_price': self.current_prices.get(ticker),
            'last_updated': latest.trade_date.isoformat()
        }

    def get_available_stocks(self) -> List[Dict]:
        """Get list of all available stocks"""
        stocks = []
        for ticker in stock_prices:
            info = self.get_stock_info(ticker)
            if info:
                stocks.append(info)
        return stocks

    def search_stocks(self, query: str) -> List[Dict]:
        """Search stocks by ticker or name"""
        query = query.upper()
        results = []

        for ticker in stock_prices:
            stock_info = self.get_stock_info(ticker)
            if stock_info and (query in ticker or query in stock_info['name'].upper()):
                results.append(stock_info)

        return results

    def get_stock_history(self, ticker: str, start_date: Optional[datetime] = None,
                          end_date: Optional[datetime] = None) -> List[Dict]:
        """Get stock price history for a ticker within date range"""
        if ticker not in stock_prices:
            return []

        history = []
        for price_data in stock_prices[ticker]:
            # Filter by date range if provided
            if start_date and price_data.trade_date.date() < start_date.date():
                continue
            if end_date and price_data.trade_date.date() > end_date.date():
                continue

            history.append({
                'ticker': price_data.ticker,
                'name': price_data.name,
                'market_type': price_data.market_type,
                'trade_date': price_data.trade_date.isoformat(),
                'open_price': price_data.open_price,
                'high_price': price_data.high_price,
                'low_price': price_data.low_price,
                'close_price': price_data.close_price,
                'volume': price_data.volume
            })

        return history

    def get_database_stats(self) -> Dict[str, Any]:
        """Get statistics about the stock database"""
        if not self.use_database:
            return {"database_available": False}

        session = db_manager.get_session()
        if not session:
            return {"database_available": False}

        try:
            # Get total records
            total_result = session.execute(text("SELECT COUNT(*) FROM stocks")).fetchone()
            total_records = total_result[0] if total_result else 0

            # Get unique tickers
            tickers_result = session.execute(text("SELECT COUNT(DISTINCT ticker) FROM stocks")).fetchone()
            unique_tickers = tickers_result[0] if tickers_result else 0

            # Get date range
            date_range_result = session.execute(text("SELECT MIN(trade_date), MAX(trade_date) FROM stocks")).fetchone()
            min_date = date_range_result[0] if date_range_result else None
            max_date = date_range_result[1] if date_range_result else None

            return {
                "database_available": True,
                "database_type": db_manager.db_type,
                "total_records": total_records,
                "unique_tickers": unique_tickers,
                "date_range": {
                    "start": min_date.isoformat() if min_date else None,
                    "end": max_date.isoformat() if max_date else None
                }
            }

        except Exception as e:
            logging.error(f"Error getting database stats: {e}")
            return {"database_available": False, "error": str(e)}
        finally:
            session.close()