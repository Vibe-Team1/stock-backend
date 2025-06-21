import os
import logging
from sqlalchemy import create_engine, text, MetaData, Table, Column, Integer, String, Date, Numeric, BigInteger, \
    DateTime
from sqlalchemy.orm import sessionmaker
from sqlalchemy.exc import SQLAlchemyError
from datetime import datetime


class DatabaseManager:
    def __init__(self):
        # Support both MySQL and SQLite with environment variable configuration
        self.db_type = os.environ.get('DB_TYPE', 'sqlite')  # mysql or sqlite

        if self.db_type == 'mysql':
            self.db_host = os.environ.get('DB_HOST', 'localhost')
            self.db_port = int(os.environ.get('DB_PORT', 3306))
            self.db_user = os.environ.get('DB_USER', 'root')
            self.db_password = os.environ.get('DB_PASSWORD', '')
            self.db_name = os.environ.get('DB_NAME', 'trading_db')
            self.connection_string = f"mysql+pymysql://{self.db_user}:{self.db_password}@{self.db_host}:{self.db_port}/{self.db_name}"
        else:
            # Default to SQLite for development
            self.db_path = os.environ.get('DB_PATH', 'trading_data.db')
            self.connection_string = f"sqlite:///{self.db_path}"

        self.engine = None
        self.SessionLocal = None
        self.metadata = MetaData()
        self._initialize_database()

    def _initialize_database(self):
        """Initialize database connection and create tables"""
        try:
            if self.db_type == 'mysql':
                self._initialize_mysql()
            else:
                self._initialize_sqlite()

            self.SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=self.engine)
            self._create_tables()
            logging.info(f"Database connection established successfully using {self.db_type}")

        except Exception as e:
            logging.error(f"Database initialization failed: {e}")
            self.engine = None
            self.SessionLocal = None

    def _initialize_mysql(self):
        """Initialize MySQL database"""
        try:
            # First connect without database to create it if needed
            base_connection_string = f"mysql+pymysql://{self.db_user}:{self.db_password}@{self.db_host}:{self.db_port}"
            base_engine = create_engine(base_connection_string)

            with base_engine.connect() as conn:
                conn.execute(text(f"CREATE DATABASE IF NOT EXISTS {self.db_name}"))
                conn.commit()

            # Now connect to the specific database
            self.engine = create_engine(self.connection_string, pool_pre_ping=True)

        except Exception as e:
            logging.error(f"MySQL initialization failed: {e}")
            # Fallback to SQLite
            self._initialize_sqlite()

    def _initialize_sqlite(self):
        """Initialize SQLite database"""
        self.engine = create_engine(self.connection_string)

    def _create_tables(self):
        """Create database tables"""
        if not self.engine:
            return

        # Define stocks table
        self.stocks_table = Table(
            'stocks',
            self.metadata,
            Column('id', Integer, primary_key=True, autoincrement=True),
            Column('ticker', String(10), nullable=False, index=True),
            Column('name', String(255), nullable=False),
            Column('market_type', String(50), nullable=False),
            Column('trade_date', Date, nullable=False, index=True),
            Column('open_price', Numeric(10, 2), nullable=False),
            Column('high_price', Numeric(10, 2), nullable=False),
            Column('low_price', Numeric(10, 2), nullable=False),
            Column('close_price', Numeric(10, 2), nullable=False),
            Column('volume', BigInteger, nullable=False),
            Column('created_at', DateTime, default=datetime.now),
        )

        # Create unique constraint for ticker + trade_date
        if self.db_type == 'mysql':
            # MySQL syntax
            create_index_sql = """
            CREATE UNIQUE INDEX IF NOT EXISTS idx_ticker_date 
            ON stocks (ticker, trade_date)
            """
        else:
            # SQLite syntax
            create_index_sql = """
            CREATE UNIQUE INDEX IF NOT EXISTS idx_ticker_date 
            ON stocks (ticker, trade_date)
            """

        try:
            # Create all tables
            self.metadata.create_all(self.engine)

            # Create unique index
            with self.engine.connect() as conn:
                conn.execute(text(create_index_sql))
                conn.commit()

            logging.info("Database tables created successfully")

        except Exception as e:
            logging.error(f"Error creating tables: {e}")

    def get_session(self):
        """Get database session"""
        if self.SessionLocal:
            return self.SessionLocal()
        return None

    def is_available(self):
        """Check if database is available"""
        return self.engine is not None and self.SessionLocal is not None

    def execute_query(self, query, params=None):
        """Execute a raw SQL query"""
        if not self.engine:
            return None

        try:
            with self.engine.connect() as conn:
                if params:
                    result = conn.execute(text(query), params)
                else:
                    result = conn.execute(text(query))
                conn.commit()
                return result
        except SQLAlchemyError as e:
            logging.error(f"Query execution failed: {e}")
            return None

    def get_table_info(self, table_name):
        """Get information about a table"""
        if not self.engine:
            return None

        try:
            with self.engine.connect() as conn:
                if self.db_type == 'mysql':
                    result = conn.execute(text(f"DESCRIBE {table_name}"))
                else:
                    result = conn.execute(text(f"PRAGMA table_info({table_name})"))
                return result.fetchall()
        except Exception as e:
            logging.error(f"Error getting table info: {e}")
            return None


# Global database manager instance
db_manager = DatabaseManager()