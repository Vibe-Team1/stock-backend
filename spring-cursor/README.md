# HTS (Home Trading System) Backend

A Spring Boot-based backend application for a home trading system with support for stock data, chart data, and trading operations.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.2**
- **PostgreSQL** (for database persistence)
- **Spring Data JPA** (for database operations)
- **OpenCSV** (for CSV file processing)

## Architecture

### Repository Structure

The application uses a polymorphic repository design for chart data:

- **CSV-based repositories**: Located in `src/main/java/com/hts/repository/csv/`
  - `CsvStockRepository` - Handles stock data from CSV files
  - `CsvChartRepository` - Handles chart data from CSV files
  - `CsvTradeRepository` - Handles trade data in memory

- **Database repositories**: Located in `src/main/java/com/hts/repository/`
  - `DbChartRepository` - JPA-based chart data repository
  - `AccountRepository` - Account management
  - `UserRepository` - User management

- **Future implementations**:
  - `HttpChartRepository` - For external HTTP API integration
  - `WebSocketChartRepository` - For real-time WebSocket data

### ChartRepository Polymorphic Design

The `ChartRepository` interface supports multiple data source types:
- Relational database (PostgreSQL)
- External HTTP API
- External WebSocket-based data
- CSV file

The appropriate implementation is injected based on the `app.chart.repository.type` configuration property.

## Features

### 1. Stock List API
- **Endpoint**: `GET /api/stocks`
- **Parameters**: 
  - `page` (default: 0) - Page number
  - `size` (default: 20, max: 100) - Page size
- **Response**: Paginated list of stocks with name, ticker, tradeDate, tradeTime, currentPrice

### 2. Account and Balance Management
- **Account Entity**: One-to-one relationship with User
- **Balance Operations**: 
  - Deduct balance when buying stocks
  - Add balance when selling stocks
- **Fields**: id (UUID), user (reference), balance (BigDecimal), timestamps

### 3. Trading Operations
- **Buy Stock**: `POST /api/trade/buy`
- **Sell Stock**: `POST /api/trade/sell`
- **Balance Integration**: Automatic balance updates on trades

### 4. Chart Data
- **Endpoint**: `GET /api/chart/{ticker}`
- **Parameters**: from, to (datetime), interval
- **Multiple Data Sources**: CSV, Database, HTTP API, WebSocket

### 5. Exception Handling
- **Global Exception Handler**: `@RestControllerAdvice`
- **Consistent Error Responses**: JSON format with status, errorCode, message
- **Custom Exceptions**: 
  - `InsufficientBalanceException`
  - `StockNotFoundException`

## Configuration

### Environment-Specific Properties

The application uses Spring Boot's profile-based configuration:

#### Local Development
- **File**: `src/main/resources/application-local.properties` (gitignored)
- **Template**: `src/main/resources/application-local.properties.template`
- **Usage**: Copy template and update with your local database credentials

#### Development Environment
- **File**: `src/main/resources/application-dev.properties` (gitignored)
- **Usage**: Configure for development server environment

#### Production Environment
- **File**: `src/main/resources/application-prod.properties` (gitignored)
- **Usage**: Uses environment variables for sensitive data

### Database Configuration Examples

#### Local Development
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hts_db
spring.datasource.username=postgres
spring.datasource.password=password
```

#### Production (Environment Variables)
```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
```

### Chart Repository Type
```properties
app.chart.repository.type=csv  # Options: csv, db, http, websocket
```

## API Endpoints

### Stocks
- `GET /api/stocks` - Get paginated stock list
- `GET /api/stocks/search?q={keyword}` - Search stocks

### Trading
- `POST /api/trade/buy` - Buy stock
- `POST /api/trade/sell` - Sell stock

### Charts
- `GET /api/chart/{ticker}?from={datetime}&to={datetime}&interval={interval}` - Get chart data

## Dependencies

### Core Dependencies
- `spring-boot-starter-web` - Web application support
- `spring-boot-starter-data-jpa` - JPA and database support
- `spring-boot-starter-validation` - Validation support
- `postgresql` - PostgreSQL driver

### CSV Processing
- `opencsv` - CSV file reading and parsing

## Development

### Prerequisites
- Java 17
- PostgreSQL database
- Gradle

### Setup
1. **Database Setup**:
   - Create PostgreSQL database named `hts_db`
   - Update database credentials in `application-local.properties`

2. **Configuration Setup**:
   ```bash
   # Copy the template file
   cp src/main/resources/application-local.properties.template src/main/resources/application-local.properties
   
   # Edit with your actual database credentials
   nano src/main/resources/application-local.properties
   ```

3. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```

### Running with Different Profiles
```bash
# Local development (default)
./gradlew bootRun

# Development environment
./gradlew bootRun --args='--spring.profiles.active=dev'

# Production environment
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### Building
```bash
./gradlew build
```

### Testing
```bash
./gradlew test
```

## Security Notes

- **Never commit sensitive data** like database passwords to version control
- **Use environment variables** in production for sensitive configurations
- **Template files** are provided for reference but contain no real credentials
- **All environment-specific properties files** are gitignored

## Future Enhancements

The application is designed to be easily extendable for:
- Real-time data via WebSocket
- Authentication and authorization
- Full database integration
- External API integrations
- Advanced trading features

## Coding Standards

- Constructor-based dependency injection (no `@Autowired` annotations)
- Layered architecture: DTO, Entity, Repository, Service, Controller
- Consistent JSON responses
- Comprehensive exception handling
- JPA annotations for database entities 