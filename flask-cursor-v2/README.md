# Stock Trading Simulation Platform

A Flask-based backend for a stock trading simulation platform that mimics HTS (Home Trading System) behavior using historical stock price data.

## Features

- **User Account Management**: Balance tracking with initial and available cash
- **Portfolio Management**: Track stock holdings with quantity and average buy price
- **Order Management**: Support for MARKET and LIMIT buy/sell orders
- **Historical Data**: Uses CSV files for stock price data
- **RESTful API**: Clean REST endpoints for all trading operations

## Data Structure

The system uses two CSV files:
- `국내주식기본조회v1.csv`: Basic stock information (ticker, name, market cap, etc.)
- `국내주식분봉차트v1.csv`: Historical price data with timestamps

## Setup

1. **Install Dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

2. **Run the Application**:
   ```bash
   python app.py
   ```

The server will start on `http://localhost:5000`

## API Endpoints

### Account Management

#### GET /api/account
Get user account information
- **Query Parameters**: `user_id` (required)
- **Response**: Account details including balance and portfolio value

#### POST /api/account
Create a new user account
- **Body**: `{"username": "string", "email": "string", "initial_balance": float}`
- **Response**: Created account details

#### GET /api/portfolio
Get user portfolio
- **Query Parameters**: `user_id` (required)
- **Response**: Portfolio items with profit/loss calculations

### Trading

#### POST /api/trade/buy
Place a buy order
- **Body**: 
  ```json
  {
    "user_id": 1,
    "ticker": "005930",
    "quantity": 10,
    "order_type": "MARKET|LIMIT",
    "limit_price": 50000  // Required for LIMIT orders
  }
  ```

#### POST /api/trade/sell
Place a sell order
- **Body**: 
  ```json
  {
    "user_id": 1,
    "ticker": "005930",
    "quantity": 5,
    "order_type": "MARKET|LIMIT",
    "limit_price": 60000  // Required for LIMIT orders
  }
  ```

#### GET /api/trade/orders
Get user's order history
- **Query Parameters**: 
  - `user_id` (required)
  - `status` (optional): PENDING, FILLED, CANCELLED, REJECTED
  - `ticker` (optional): Filter by stock ticker
  - `limit` (optional): Number of orders to return (default: 50)

#### POST /api/trade/orders/{order_id}/cancel
Cancel a pending order

### Stock Information

#### GET /api/stock/{ticker}/price
Get stock price
- **Query Parameters**: `at` (optional): Timestamp for historical price
- **Response**: Current or historical price

#### GET /api/stock/{ticker}/info
Get stock basic information

#### GET /api/stock/{ticker}/prices
Get price data within a time range
- **Query Parameters**: `start_time`, `end_time` (required)

#### GET /api/stock/list
Get list of all available stocks

## Database Models

### User
- `id`: Primary key
- `username`: Unique username
- `email`: Unique email
- `initial_balance`: Starting cash balance
- `available_balance`: Current available cash
- `created_at`, `updated_at`: Timestamps

### PortfolioItem
- `id`: Primary key
- `user_id`: Foreign key to User
- `ticker`: Stock ticker symbol
- `stock_name`: Stock name
- `quantity`: Number of shares owned
- `average_buy_price`: Average purchase price
- `created_at`, `updated_at`: Timestamps

### Order
- `id`: Primary key
- `user_id`: Foreign key to User
- `ticker`: Stock ticker symbol
- `stock_name`: Stock name
- `order_type`: MARKET or LIMIT
- `side`: BUY or SELL
- `quantity`: Number of shares
- `limit_price`: Price limit (for LIMIT orders)
- `executed_price`: Actual execution price
- `status`: PENDING, FILLED, CANCELLED, REJECTED
- `created_at`, `executed_at`, `updated_at`: Timestamps

## Order Types

### MARKET Orders
- Execute immediately at current market price
- Require sufficient balance (for buy) or shares (for sell)

### LIMIT Orders
- Execute only when market price meets the limit condition
- Buy orders: Execute when price ≤ limit price
- Sell orders: Execute when price ≥ limit price
- Can remain pending until conditions are met

## Example Usage

### 1. Create a User Account
```bash
curl -X POST http://localhost:5000/api/account \
  -H "Content-Type: application/json" \
  -d '{"username": "trader1", "email": "trader1@example.com", "initial_balance": 10000000}'
```

### 2. Place a Buy Order
```bash
curl -X POST http://localhost:5000/api/trade/buy \
  -H "Content-Type: application/json" \
  -d '{"user_id": 1, "ticker": "005930", "quantity": 10, "order_type": "MARKET"}'
```

### 3. Check Portfolio
```bash
curl "http://localhost:5000/api/portfolio?user_id=1"
```

### 4. Get Stock Price
```bash
curl "http://localhost:5000/api/stock/005930/price"
```

## Error Handling

The API returns appropriate HTTP status codes:
- `200`: Success
- `201`: Created
- `400`: Bad Request (validation errors)
- `404`: Not Found
- `409`: Conflict (duplicate data)
- `500`: Internal Server Error

Error responses include a descriptive message:
```json
{
  "error": "Insufficient balance"
}
```

## Notes

- The system uses SQLite database by default (can be changed via DATABASE_URL environment variable)
- Stock prices are based on historical data from CSV files
- All monetary values are in Korean Won (KRW)
- The system automatically processes pending LIMIT orders when price conditions are met 