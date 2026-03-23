# Pricing Service

Pricing Service implemented with Spring Boot, Java 21, H2, and a hexagonal architecture.

This service calculates the applicable price for a product and a brand on a given date. When multiple price rows overlap, the one with the highest priority is selected.

## Goal

Expose a REST endpoint that receives:

- `applicationDate`
- `productId`
- `brandId`

And returns:

- `productId`
- `brandId`
- `priceList`
- `startDate`
- `endDate`
- `price`
- `currency`

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- H2 in-memory database
- JUnit 5
- Lombok

## Architecture

The project follows a hexagonal architecture with three main layers:

- `domain`
  Contains the business model, business rules, and ports.
- `application`
  Contains the use case implementation and orchestration logic.
- `infrastructure`
  Contains HTTP entrypoints, persistence adapters, SQL initialization, and technical details.

Project structure:

```text
src/main/java/com/company/pricing
|-- application
|   `-- usecase
|-- domain
|   |-- exception
|   |-- model
|   |-- port
|   |   |-- in
|   |   `-- out
|   `-- service
`-- infrastructure
    |-- in
    |   `-- rest
    `-- out
        `-- persistence
```

## Design Overview

Request flow:

1. `PriceController` receives the HTTP request.
2. A `PriceQuery` is created from the request parameters.
3. `GetApplicablePriceService` executes the use case.
4. `LoadPricesPort` retrieves candidate prices from persistence.
5. `ApplicablePriceSelector` applies the business rule:
   filter by date and select the highest priority row.
6. The response is mapped to a REST DTO and returned.

Main classes:

- Controller:
  [PriceController.java](src/main/java/com/company/pricing_srv/infrastructure/in/rest/controller/PriceController.java)
- Use case:
  [GetApplicablePriceService.java](src/main/java/com/company/pricing_srv/application/usecase/GetApplicablePriceService.java)
- Domain selection rule:
  [ApplicablePriceSelector.java](src/main/java/com/company/pricing_srv/domain/service/ApplicablePriceSelector.java)
- Persistence adapter:
  [PriceRepositoryAdapter.java](src/main/java/com/company/pricing_srv/infrastructure/out/persistence/adapter/PriceRepositoryAdapter.java)

## Business Rule

The selection rule is:

- A price row is applicable when `applicationDate` is between `startDate` and `endDate`
- `startDate` and `endDate` are treated as inclusive
- If more than one row is applicable, the row with the highest `priority` is returned

## Dataset

The application boots with an in-memory dataset loaded from:

- [schema.sql](src/main/resources/schema.sql)
- [data.sql](src/main/resources/data.sql)

The loaded sample contains 4 price rows for:

- `brandId = 1`
- `productId = 35455`

## REST API

Endpoint:

```http
GET /api/v1/prices
```

Example:

```bash
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"
```

Example response:

```json
{
  "brandId": 1,
  "productId": 35455,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "currency": "EUR"
}
```

If no applicable price exists, the service returns `404 Not Found`.

## How To Run

Run tests:

```bash
./mvnw test
```

Run the application:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

## Manual Endpoint Check

Once the application is running, the endpoint can also be tested manually from PowerShell with `Invoke-RestMethod`.

Example:

```powershell
Invoke-RestMethod "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"
```

Expected result:

```json
{
  "brandId": 1,
  "productId": 35455,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "currency": "EUR"
}
```

## Sample Verification Cases

The following scenarios are covered and expected to return:

| Application Date       | Product ID | Brand ID | Expected Price List | Expected Price |
|------------------------|------------|----------|---------------------|----------------|
| 2020-06-14T10:00:00    | 35455      | 1        | 1                   | 35.50          |
| 2020-06-14T16:00:00    | 35455      | 1        | 2                   | 25.45          |
| 2020-06-14T21:00:00    | 35455      | 1        | 1                   | 35.50          |
| 2020-06-15T10:00:00    | 35455      | 1        | 3                   | 30.50          |
| 2020-06-16T21:00:00    | 35455      | 1        | 4                   | 38.95          |

There is also a helper script for manual verification:

```powershell
./scripts/check-sample-cases.ps1
```

## Testing Strategy

The project includes three levels of tests:

- Domain tests
  Verify the stream-based selection logic in isolation
- Application tests
  Verify the use case behavior without HTTP or database concerns
- REST integration tests
  Verify the endpoint using the H2 in-memory dataset

Relevant test classes:

- [ApplicablePriceSelectorTest.java](src/test/java/com/company/pricing_srv/domain/service/ApplicablePriceSelectorTest.java)
- [GetApplicablePriceServiceTest.java](src/test/java/com/company/pricing_srv/application/usecase/GetApplicablePriceServiceTest.java)
- [PriceControllerTest.java](src/test/java/com/company/pricing_srv/infrastructure/in/rest/controller/PriceControllerTest.java)

## Validation And Error Handling

- Request parameter validation is handled at controller level
- A missing applicable price is translated to `404 Not Found`
- Invalid request parameter formats are handled by Spring MVC and result in `400 Bad Request`

## Assumptions

- Price intervals are inclusive on both boundaries
- Priority is the main conflict resolution rule
- A deterministic secondary comparator by `startDate` is used in the selector implementation

## Notes

- Neutral naming has been used in the code and documentation
- Lombok is used only where it reduces boilerplate without making the model less explicit
- The core business rule is intentionally kept in the domain layer instead of embedding all logic in SQL
