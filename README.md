# Pricing Service

A Spring Boot REST API that returns the applicable price for a product based on application date, product ID, and brand ID. Built with Hexagonal Architecture.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Business Rules](#business-rules)

## Features

- RESTful API endpoint to query product prices
- Support for overlapping price periods with priority-based selection
- In-memory H2 database with sample data
- Comprehensive unit and integration tests
- Clean hexagonal architecture

## Architecture

This project follows **Hexagonal Architecture** (Ports and Adapters) with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────┐
│                    Infrastructure                       │
│  ┌──────────────────┐              ┌─────────────────┐  │
│  │   REST Controller│              │  JPA Repository │  │
│  │     (Input)      │              │    (Output)     │  │
│  └────────┬─────────┘              └────────┬────────┘  │
│           │                                 │           │
└───────────┼─────────────────────────────────┼───────────┘
            │                                 │
      ┌─────▼─────────────────────────────────▼─────┐
      │            Application Layer                │
      │         (Use Case Orchestration)            │
      └─────────────────┬───────────────────────────┘
                        │
            ┌───────────▼───────────┐
            │     Domain Layer      │
            │  (Business Logic)     │
            │  - Models             │
            │  - Ports (Interfaces) │
            │  - Services           │
            └───────────────────────┘
```

### Layers

- **Domain**: Pure business logic, framework-agnostic
  - Models: `Price`, `PriceQuery`
  - Ports: Input and output interfaces
  - Services: Price selection algorithm

- **Application**: Use case orchestration
  - Implements input ports
  - Coordinates domain services and repositories

- **Infrastructure**: Framework-specific implementations
  - REST API (Spring MVC)
  - Database persistence (JPA/H2)
  - Mappers (MapStruct)

## Tech Stack

- **Java 17**
- **Spring Boot 3.3.6**
- **H2 Database** (in-memory)
- **Lombok** (boilerplate reduction)
- **MapStruct** (object mapping)
- **JUnit 5** (testing)
- **Mockito** (mocking)
- **Maven** (build tool)

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Installation

1. Clone the repository:
```bash
  git clone <repository-url>
cd pricing-service
```

2. Build the project:
```bash
  mvn clean install
```

3. Run the application:
```bash
  mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### H2 Console

Access the H2 database console at: `http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:mem:pricesdb`
- **Username**: `sa`
- **Password**: _(empty)_

## API Documentation

### Get Applicable Price

Returns the applicable price for a product at a specific date and time.

**Endpoint**: `GET /prices`

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `applicationDate` | ISO-8601 DateTime | Yes | Date and time to check price (e.g., `2020-06-14T10:00:00`) |
| `productId` | Long | Yes | Product identifier |
| `brandId` | Long | Yes | Brand identifier |

**Success Response (200 OK)**:
```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "2020-06-14T00:00:00",
  "endDate": "2020-12-31T23:59:59",
  "finalPrice": 35.50,
  "currency": "EUR"
}
```

**Error Responses**:
- `400 Bad Request`: Missing or invalid parameters
- `404 Not Found`: No applicable price found

### Examples

```bash
# Request at 10:00 on June 14th
curl "http://localhost:8080/prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=1"

# Request at 16:00 on June 14th
curl "http://localhost:8080/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"

# Request at 21:00 on June 14th
curl "http://localhost:8080/prices?applicationDate=2020-06-14T21:00:00&productId=35455&brandId=1"

# Request at 10:00 on June 15th
curl "http://localhost:8080/prices?applicationDate=2020-06-15T10:00:00&productId=35455&brandId=1"

# Request at 21:00 on June 16th
curl "http://localhost:8080/prices?applicationDate=2020-06-16T21:00:00&productId=35455&brandId=1"
```

## Testing

The project includes comprehensive test coverage:

### Run All Tests
```bash
  mvn test
```

### Test Categories

1. **Unit Tests** (Domain Layer)
   - `PriceSelectionServiceTest`: Tests the price selection algorithm
   - Business logic validation with various scenarios

2. **Unit Tests** (Application Layer)
   - `FindApplicablePriceServiceTest`: Tests use case orchestration
   - Mock-based testing

3. **Integration Tests**
   - `PriceControllerIntegrationTest`: End-to-end API tests
   - Tests all 5 required scenarios with real HTTP requests

### Test Coverage

- ✅ Price selection by highest priority
- ✅ Price selection by latest start date (when priorities are equal)
- ✅ Date range validation (inclusive)
- ✅ Empty result handling
- ✅ HTTP 200, 400, and 404 responses

## Project Structure

```
src/
├── main/
│   ├── java/com/example/pricingservice/
│   │   ├── application/
│   │   │   └── service/
│   │   │       └── FindApplicablePriceService.java
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Price.java
│   │   │   │   └── PriceQuery.java
│   │   │   ├── port/
│   │   │   │   ├── in/
│   │   │   │   │   └── FindApplicablePriceUseCase.java
│   │   │   │   └── out/
│   │   │   │       └── PriceRepository.java
│   │   │   └── service/
│   │   │       └── PriceSelectionService.java
│   │   └── infrastructure/
│   │       ├── adapter/
│   │       │   ├── in/
│   │       │   │   └── rest/
│   │       │   │       ├── PriceController.java
│   │       │   │       ├── dto/
│   │       │   │       │   ├── PriceRequest.java
│   │       │   │       │   └── PriceResponse.java
│   │       │   │       └── mapper/
│   │       │   │           └── PriceDtoMapper.java
│   │       │   └── out/
│   │       │       └── persistence/
│   │       │           ├── PriceJpaAdapter.java
│   │       │           ├── entity/
│   │       │           │   └── PriceEntity.java
│   │       │           ├── mapper/
│   │       │           │   └── PriceEntityMapper.java
│   │       │           └── repository/
│   │       │               └── PriceJpaRepository.java
│   │       └── config/
│   │           └── BeanConfiguration.java
│   └── resources/
│       ├── application.yml
│       └── data.sql
└── test/
    └── java/com/example/pricingservice/
        ├── application/service/
        │   └── FindApplicablePriceServiceTest.java
        ├── domain/service/
        │   └── PriceSelectionServiceTest.java
        └── infrastructure/adapter/in/rest/
            └── PriceControllerIntegrationTest.java
```

## Business Rules

### Price Selection Algorithm

When multiple prices apply for the same date, the selection follows these rules:

1. **Priority**: Select the price with the highest priority value
2. **Start Date**: If priorities are equal, select the price with the latest start date
3. **Date Range**: The application date must be within `[startDate, endDate]` (inclusive)

### Sample Data

The application is initialized with the following test data:

| Brand | Start Date | End Date | Price List | Product | Priority | Price | Currency |
|-------|------------|----------|------------|---------|----------|-------|----------|
| 1 | 2020-06-14 00:00 | 2020-12-31 23:59 | 1 | 35455 | 0 | 35.50 | EUR |
| 1 | 2020-06-14 15:00 | 2020-06-14 18:30 | 2 | 35455 | 1 | 25.45 | EUR |
| 1 | 2020-06-15 00:00 | 2020-06-15 11:00 | 3 | 35455 | 1 | 30.50 | EUR |
| 1 | 2020-06-15 16:00 | 2020-12-31 23:59 | 4 | 35455 | 1 | 38.95 | EUR |

## License

This project is for educational/assessment purposes.
