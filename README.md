# Pricing Service

REST service built with Spring Boot that queries the applicable price for a product of a commercial chain on a given date, applying priority rules when multiple rates are in effect.

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

## Technologies

- **Java 17**
- **Spring Boot 3.3.6**
- **H2 Database** (in-memory database)
- **Spring Data JPA**
- **Lombok**
- **MapStruct**
- **JUnit 5 + Mockito**
- **Maven**

## Architecture

The project implements **Hexagonal Architecture** (Ports & Adapters):

```
src/main/java/com/example/pricing_service/
├── application/
│   ├── service/
│   │   └── FindApplicablePriceService.java    # Use case implementation
│   └── usecase/
│       └── FindApplicablePriceUseCase.java    # Input port
├── domain/
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
        │   │   ├── PriceRequest.java
        │   │   └── PriceResponse.java
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

## REST API

### Endpoint: Query Applicable Price

```
GET /prices
```

**Input parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `applicationDate` | ISO DateTime | Yes | Application date (e.g., `2020-06-14T10:00:00`) |
| `productId` | Long | Yes | Product identifier |
| `brandId` | Long | Yes | Chain identifier |

**Successful response (200 OK):**

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

**Response codes:**

| Code | Description |
|------|-------------|
| 200 | Price found |
| 400 | Invalid or missing parameters |
| 404 | No applicable price found |

### Usage Examples

```bash
# Test 1: Request at 10:00 on the 14th
curl "http://localhost:8080/prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=1"

# Test 2: Request at 16:00 on the 14th
curl "http://localhost:8080/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"

# Test 3: Request at 21:00 on the 14th
curl "http://localhost:8080/prices?applicationDate=2020-06-14T21:00:00&productId=35455&brandId=1"

# Test 4: Request at 10:00 on the 15th
curl "http://localhost:8080/prices?applicationDate=2020-06-15T10:00:00&productId=35455&brandId=1"

# Test 5: Request at 21:00 on the 16th
curl "http://localhost:8080/prices?applicationDate=2020-06-16T21:00:00&productId=35455&brandId=1"
```

## Test Data

The H2 database is initialized with the following data:

| brand_id | start_date | end_date | price_list | product_id | priority | price | currency |
|----------|------------|----------|------------|------------|----------|-------|----------|
| 1 | 2020-06-14 00:00:00 | 2020-12-31 23:59:59 | 1 | 35455 | 0 | 35.50 | EUR |
| 1 | 2020-06-14 15:00:00 | 2020-06-14 18:30:00 | 2 | 35455 | 1 | 25.45 | EUR |
| 1 | 2020-06-15 00:00:00 | 2020-06-15 11:00:00 | 3 | 35455 | 1 | 30.50 | EUR |
| 1 | 2020-06-15 16:00:00 | 2020-12-31 23:59:59 | 4 | 35455 | 1 | 38.95 | EUR |

## Tests

The project includes **11 tests** (unit and integration):

```
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Integration Tests (PriceControllerIntegrationTest)

Validate the 5 required scenarios:

| Test | Date/Time | Expected Result |
|------|-----------|-----------------|
| Test 1 | 06/14 10:00 | price_list=1, price=35.50€ |
| Test 2 | 06/14 16:00 | price_list=2, price=25.45€ |
| Test 3 | 06/14 21:00 | price_list=1, price=35.50€ |
| Test 4 | 06/15 10:00 | price_list=3, price=30.50€ |
| Test 5 | 06/16 21:00 | price_list=4, price=38.95€ |

### Unit Tests (FindApplicablePriceServiceTest)

- Price return when exists
- Empty return when no prices found
- Correct priority-based selection when multiple prices apply

## H2 Console

Access to the in-memory database:

- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:pricesdb`
- **Username:** `sa`
- **Password:** *(empty)*

## Business Rules

1. Search for all prices matching `productId`, `brandId` and whose date range includes the `applicationDate`
2. If multiple prices apply, select the one with the **highest priority**
3. In case of priority tie, select the one with the **most recent start date**