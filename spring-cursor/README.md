# SpringCursor - Home Trading System Backend

A Java Spring Boot-based backend API for a Home Trading System (HTS) that provides stock search, historical chart data, and trading functionality.

## Features

- **Stock Search**: Search stocks by name or ticker with partial matching
- **Historical Charts**: Retrieve candlestick chart data with configurable intervals
- **Trading**: Buy and sell stocks with account-based transactions
- **CSV Data Source**: Uses Korean stock market CSV data files

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.2.2 (latest stable)
- **Build Tool**: Gradle 8.5
- **CSV Processing**: OpenCSV 5.9
- **JSON Processing**: Jackson

## Project Structure

```
src/main/java/com/hts/
├── SpringCursorApplication.java          # Main application class
├── controller/                           # REST API controllers
│   ├── StockController.java             # Stock search endpoints
│   ├── ChartController.java             # Chart data endpoints
│   └── TradeController.java             # Trading endpoints
├── service/                             # Business logic layer
│   ├── StockService.java               # Stock-related services
│   ├── ChartService.java               # Chart-related services
│   └── TradeService.java               # Trading services
├── repository/                          # Data access layer
│   ├── StockRepository.java            # Stock data interface
│   ├── ChartRepository.java            # Chart data interface
│   ├── TradeRepository.java            # Trade data interface
│   └── impl/                           # Repository implementations
│       ├── StockRepositoryImpl.java    # CSV-based stock data
│       ├── ChartRepositoryImpl.java    # CSV-based chart data
│       └── TradeRepositoryImpl.java    # In-memory trade storage
├── domain/                             # Domain models
│   ├── Stock.java                     # Stock entity
│   ├── ChartData.java                 # Chart data entity
│   ├── Trade.java                     # Trade entity
│   └── ChartInterval.java             # Chart interval enum
├── dto/                               # Data Transfer Objects
│   ├── StockSearchResponse.java       # Stock search response
│   ├── ChartDataResponse.java         # Chart data response
│   ├── TradeRequest.java              # Trade request
│   ├── TradeResponse.java             # Trade response
│   └── ErrorResponse.java             # Error response
└── exception/                         # Exception handling
    └── GlobalExceptionHandler.java    # Global exception handler
```

## API Endpoints

### Stock Search
- **GET** `/api/stocks/search?q={keyword}`
  - Search stocks by name or ticker
  - Case-insensitive partial matching
  - Returns: `StockSearchResponse[]`

### Historical Charts
- **GET** `/api/chart/{ticker}?from={start}&to={end}&interval={1m|5m|10m}`
  - Retrieve candlestick data for a specific ticker
  - Date format: ISO 8601 (e.g., `2025-06-17T13:21:00`)
  - Intervals: `1m`, `5m`, `10m`
  - Returns: `ChartDataResponse[]`

### Trading
- **POST** `/api/trade/buy`
  - Request body: `TradeRequest`
  - Returns: `TradeResponse`

- **POST** `/api/trade/sell`
  - Request body: `TradeRequest`
  - Returns: `TradeResponse`

## Data Sources

The application uses two CSV files located in the project root:

1. **국내주식기본조회v1.csv**: Stock basic information
   - Columns: 종목코드, 종목명, 현재가, PER, PBR, etc.

2. **국내주식분봉차트v1.csv**: Minute-level candlestick data
   - Columns: 종목명, 종목코드, 영업일자, 체결시간, 현재가, 시가, 고가, 저가, 체결거래량, etc.

## Getting Started

### Prerequisites
- Java 21 or higher
- Gradle 8.5 or higher

### Running the Application

1. **Clone and navigate to the project directory**:
   ```bash
   cd ~/stock-backend/spring-cursor
   ```

2. **Build the project**:
   ```bash
   ./gradlew build
   ```

3. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```

4. **Access the API**:
   - Base URL: `http://localhost:8080`
   - Health check: `http://localhost:8080/actuator/health`

### Example API Calls

#### Search Stocks
```bash
curl "http://localhost:8080/api/stocks/search?q=삼성"
```

#### Get Chart Data
```bash
curl "http://localhost:8080/api/chart/005930?from=2025-06-17T13:21:00&to=2025-06-17T15:30:00&interval=1m"
```

#### Buy Stock
```bash
curl -X POST "http://localhost:8080/api/trade/buy" \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "ACC001",
    "ticker": "005930",
    "price": 59500,
    "quantity": 10,
    "timestamp": "2025-06-17T15:30:00"
  }'
```

#### Sell Stock
```bash
curl -X POST "http://localhost:8080/api/trade/sell" \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "ACC001",
    "ticker": "005930",
    "price": 60000,
    "quantity": 5,
    "timestamp": "2025-06-17T15:35:00"
  }'
```

## Configuration

The application can be configured via `application.properties`:

- **Server port**: `server.port=8080`
- **CSV file paths**: Configured in repository implementations
- **CORS**: Enabled for all origins
- **Logging**: DEBUG level for application packages

## Architecture

The application follows a layered architecture:

1. **Controller Layer**: Handles HTTP requests and responses
2. **Service Layer**: Contains business logic
3. **Repository Layer**: Data access abstraction
4. **Domain Layer**: Core business entities
5. **DTO Layer**: Data transfer objects for API communication

## Future Enhancements

The architecture is designed to easily support:

- **Database Integration**: Replace CSV repositories with JPA repositories
- **Authentication/Authorization**: Add Spring Security
- **WebSocket Support**: Real-time price updates
- **Caching**: Redis for performance optimization
- **Microservices**: Split into separate services

## Error Handling

All API endpoints return consistent JSON error responses:

```json
{
  "timestamp": "2025-06-17T15:30:00",
  "message": "Error description",
  "details": "Detailed error information"
}
```

## Development

### Building
```bash
./gradlew build
```

### Testing
```bash
./gradlew test
```

### Running Tests with Coverage
```bash
./gradlew test jacocoTestReport
```

## License

This project is for educational and development purposes. 