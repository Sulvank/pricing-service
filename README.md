# Pricing Service

REST service built with Spring Boot that queries the applicable price for a product of a commercial chain on a given date, applying priority rules when multiple rates are in effect.

---

## 📋 Table of Contents

- [Problem Description](#problem-description)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [Installation and Execution](#installation-and-execution)
- [REST API](#rest-api)
- [Test Data](#test-data)
- [Tests](#tests)
- [Exception Handling](#exception-handling)
- [Business Rules](#business-rules)
- [H2 Console](#h2-console)

---

## Problem Description

In the e-commerce database, there is a `prices` table that stores the final price (PVP) and the applicable rate for a product of a chain within a date range.

When two rates overlap in a date range, the one with the **highest priority** (higher numeric value) is applied.

### prices Table Structure

| Field | Description |
|-------|-------------|
| `brand_id` | Foreign key of the group's chain |
| `start_date` | Price application start date |
| `end_date` | Price application end date |
| `price_list` | Applicable price rate identifier |
| `product_id` | Product identifier |
| `priority` | Price application disambiguator |
| `price` | Final sale price |
| `currency` | Currency ISO code |

---

## Technologies

- **Java 17**
- **Spring Boot 3.3.6**
- **H2 Database** (in-memory database)
- **Spring Data JPA**
- **Lombok**
- **MapStruct**
- **JUnit 5 + Mockito**
- **Maven**

---

## Architecture

The project implements **Hexagonal Architecture** (Ports & Adapters), separating business logic from infrastructure concerns.

### Project Structure
```
src/main/java/com/example/pricing_service/
├── application/
│   ├── service/
│   │   └── FindApplicablePriceService.java    # Use case implementation
│   └── usecase/
│       └── FindApplicablePriceUseCase.java    # Input port
├── domain/
│   ├── exception/
│   │   └── PriceNotFoundException.java        # Domain exception
│   ├── model/
│   │   ├── Price.java                         # Domain entity
│   │   └── PriceQuery.java                    # Query object
│   └── port/out/
│       └── PriceRepository.java               # Output port
└── infrastructure/
    └── adapter/
        ├── in/rest/
        │   ├── PriceController.java           # REST controller
        │   ├── dto/
        │   │   ├── ErrorResponse.java         # Error DTO
        │   │   └── PriceResponse.java         # Response DTO
        │   ├── exception/
        │   │   └── GlobalExceptionHandler.java # Exception handler
        │   └── mapper/
        │       └── PriceDtoMapper.java
        └── out/persistence/
            ├── PriceJpaAdapter.java           # Persistence adapter
            ├── entity/
            │   └── PriceEntity.java
            ├── mapper/
            │   └── PriceEntityMapper.java
            └── repository/
                └── PriceJpaRepository.java
```

### Layers

- **Domain Layer**: Contains business entities, value objects, and domain exceptions
- **Application Layer**: Implements use cases with business logic
- **Infrastructure Layer**: Handles external concerns (REST API, database, exception handling)

---

## Installation and Execution

### Prerequisites

- Java 17+
- Maven 3.6+

### Build the Project
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

### Run Tests
```bash
mvn test
```

**Test Results:**
```
Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## REST API

### Endpoint: Query Applicable Price
```
GET /prices
```

#### Request Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `applicationDate` | ISO DateTime | Yes | Application date (e.g., `2020-06-14T10:00:00`) |
| `productId` | Long | Yes | Product identifier |
| `brandId` | Long | Yes | Chain identifier |

#### Response Codes

| Code | Description |
|------|-------------|
| 200 | Price found successfully |
| 400 | Invalid or missing parameters |
| 404 | No applicable price found for the given criteria |
| 500 | Internal server error |

#### Success Response (200 OK)
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

#### Error Response (404 NOT FOUND)
```json
{
  "code": "PRICE_NOT_FOUND",
  "message": "No applicable price found for productId=123 and brandId=1",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/prices"
}
```

#### Error Response (400 BAD REQUEST)
```json
{
  "code": "MISSING_PARAMETER",
  "message": "Required parameter 'brandId' is missing",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/prices"
}
```

### Usage Examples
```bash
# Test 1: Request at 10:00 on the 14th (returns price 35.50€)
curl "http://localhost:8080/prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=1"

# Test 2: Request at 16:00 on the 14th (returns price 25.45€)
curl "http://localhost:8080/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"

# Test 3: Request at 21:00 on the 14th (returns price 35.50€)
curl "http://localhost:8080/prices?applicationDate=2020-06-14T21:00:00&productId=35455&brandId=1"

# Test 4: Request at 10:00 on the 15th (returns price 30.50€)
curl "http://localhost:8080/prices?applicationDate=2020-06-15T10:00:00&productId=35455&brandId=1"

# Test 5: Request at 21:00 on the 16th (returns price 38.95€)
curl "http://localhost:8080/prices?applicationDate=2020-06-16T21:00:00&productId=35455&brandId=1"

# Example: Missing parameter error
curl "http://localhost:8080/prices?applicationDate=2020-06-14T10:00:00&productId=35455"

# Example: Price not found error
curl "http://localhost:8080/prices?applicationDate=2025-01-01T10:00:00&productId=123&brandId=1"
```

---

## Test Data

The H2 database is initialized with the following data:

| brand_id | start_date | end_date | price_list | product_id | priority | price | currency |
|----------|------------|----------|------------|------------|----------|-------|----------|
| 1 | 2020-06-14 00:00:00 | 2020-12-31 23:59:59 | 1 | 35455 | 0 | 35.50 | EUR |
| 1 | 2020-06-14 15:00:00 | 2020-06-14 18:30:00 | 2 | 35455 | 1 | 25.45 | EUR |
| 1 | 2020-06-15 00:00:00 | 2020-06-15 11:00:00 | 3 | 35455 | 1 | 30.50 | EUR |
| 1 | 2020-06-15 16:00:00 | 2020-12-31 23:59:59 | 4 | 35455 | 1 | 38.95 | EUR |

---

## Tests

The project includes **23 comprehensive tests** covering unit, integration, and domain logic.

### Integration Tests (15 tests)

**PriceControllerIntegrationTest** - Validates the complete flow from REST endpoint to database.

#### Required Scenarios

| Test | Date/Time | Expected Result |
|------|-----------|-----------------|
| Test 1 | 06/14 10:00 | price_list=1, price=35.50€ |
| Test 2 | 06/14 16:00 | price_list=2, price=25.45€ |
| Test 3 | 06/14 21:00 | price_list=1, price=35.50€ |
| Test 4 | 06/15 10:00 | price_list=3, price=30.50€ |
| Test 5 | 06/16 21:00 | price_list=4, price=38.95€ |

#### Error Handling Scenarios

- 404 with structured error body when no price found
- 400 when required parameters are missing (brandId, productId, applicationDate)
- 400 when parameter types are invalid (non-numeric IDs)
- 400 when date format is incorrect
- 400 when applicationDate lacks time component

### Unit Tests (2 tests)

**FindApplicablePriceServiceTest** - Validates business logic in isolation using mocks.

- Returns correct price when found
- Throws PriceNotFoundException with descriptive message when no price found

### Domain Tests (5 tests)

**PriceTest** - Validates domain entity behavior.

- Price applicability at exact start date
- Price applicability at exact end date
- Price not applicable one second after end date
- Price applicable within date range
- Price not applicable outside date range

### Application Context Test (1 test)

**PricingServiceApplicationTests** - Validates Spring Boot configuration.

- Spring Boot application context loads successfully

---

## Exception Handling

The API provides structured error responses for all error scenarios through a global exception handler.

### Error Response Structure
```json
{
  "code": "ERROR_CODE",
  "message": "Human-readable error description",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/prices"
}
```

### Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `PRICE_NOT_FOUND` | 404 | No applicable price found for the given criteria |
| `MISSING_PARAMETER` | 400 | Required request parameter is missing |
| `INVALID_PARAMETER_TYPE` | 400 | Parameter has incorrect type (e.g., non-numeric ID) |
| `INTERNAL_ERROR` | 500 | Unexpected server error |

---

## Business Rules

1. **Query Filtering**: Search for all prices matching `productId`, `brandId` and whose date range includes the `applicationDate`

2. **Priority Selection**: If multiple prices apply, select the one with the **highest priority** (higher numeric value)

3. **Tie-Breaking**: In case of priority tie, select the one with the **most recent start date**

4. **Business Logic Location**: Price selection logic is implemented in the application layer using Java Stream API with `Comparator`, keeping business rules explicit and testable rather than delegated to database queries

---

## H2 Console

Access to the in-memory database for debugging and inspection:

- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:pricesdb`
- **Username:** `sa`
- **Password:** *(empty)*