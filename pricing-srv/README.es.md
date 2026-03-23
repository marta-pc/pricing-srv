# Pricing Service

Servicio de gestión de precios implementado con Spring Boot, Java 21 y arquitectura hexagonal.
Permite calcular el precio aplicable de un producto para una marca en una fecha determinada, aplicando la prioridad cuando varias filas de precio se solapan.

## Objetivo

Exponer un endpoint REST que reciba:

- `applicationDate`
- `productId`
- `brandId`

Y devuelva:

- `productId`
- `brandId`
- `priceList`
- `startDate`
- `endDate`
- `price`
- `currency`

## Stack Tecnologico

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- H2 en memoria
- JUnit 5
- Lombok

## Arquitectura

El proyecto sigue una arquitectura hexagonal con tres capas principales:

- `domain`
  Contiene el modelo de negocio, las reglas de negocio y los puertos.
- `application`
  Contiene la implementacion del caso de uso y la logica de orquestacion.
- `infrastructure`
  Contiene los entrypoints HTTP, los adaptadores de persistencia, la inicializacion SQL y los detalles tecnicos.

Estructura del proyecto:

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

## Vista General Del Diseno

Flujo de una peticion:

1. `PriceController` recibe la peticion HTTP.
2. Se crea un `PriceQuery` a partir de los parametros de entrada.
3. `GetApplicablePriceService` ejecuta el caso de uso.
4. `LoadPricesPort` recupera los precios candidatos desde persistencia.
5. `ApplicablePriceSelector` aplica la regla de negocio:
   filtra por fecha y selecciona la fila con mayor prioridad.
6. La respuesta se transforma a un DTO REST y se devuelve al cliente.

Clases principales:

- Controlador:
  [PriceController.java](src/main/java/com/company/pricing_srv/infrastructure/in/rest/controller/PriceController.java)
- Caso de uso:
  [GetApplicablePriceService.java](src/main/java/com/company/pricing_srv/application/usecase/GetApplicablePriceService.java)
- Regla de seleccion en dominio:
  [ApplicablePriceSelector.java](src/main/java/com/company/pricing_srv/domain/service/ApplicablePriceSelector.java)
- Adaptador de persistencia:
  [PriceRepositoryAdapter.java](src/main/java/com/company/pricing_srv/infrastructure/out/persistence/adapter/PriceRepositoryAdapter.java)

## Regla De Negocio

La regla de seleccion es:

- Una fila de precio aplica cuando `applicationDate` esta entre `startDate` y `endDate`
- `startDate` y `endDate` se tratan como inclusivos
- Si mas de una fila aplica, se devuelve la de mayor `priority`

## Dataset

La aplicacion arranca con un dataset en memoria cargado desde:

- [schema.sql](src/main/resources/schema.sql)
- [data.sql](src/main/resources/data.sql)

La muestra cargada contiene 4 filas de precio para:

- `brandId = 1`
- `productId = 35455`

## API REST

Endpoint:

```http
GET /api/v1/prices
```

Ejemplo:

```bash
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"
```

Respuesta de ejemplo:

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

Si no existe un precio aplicable, el servicio devuelve `404 Not Found`.

## Como Ejecutarlo

Ejecutar tests:

```bash
./mvnw test
```

Levantar la aplicacion:

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

## Prueba Manual Del Endpoint

Una vez levantada la aplicacion, el endpoint tambien se puede probar manualmente desde PowerShell con `Invoke-RestMethod`.

Ejemplo:

```powershell
Invoke-RestMethod "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"
```

Resultado esperado:

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

## Casos De Verificacion

Los siguientes escenarios estan cubiertos y deben devolver:

| Application Date       | Product ID | Brand ID | Expected Price List | Expected Price |
|------------------------|------------|----------|---------------------|----------------|
| 2020-06-14T10:00:00    | 35455      | 1        | 1                   | 35.50          |
| 2020-06-14T16:00:00    | 35455      | 1        | 2                   | 25.45          |
| 2020-06-14T21:00:00    | 35455      | 1        | 1                   | 35.50          |
| 2020-06-15T10:00:00    | 35455      | 1        | 3                   | 30.50          |
| 2020-06-16T21:00:00    | 35455      | 1        | 4                   | 38.95          |

Tambien hay un script de apoyo para validacion manual:

```powershell
./scripts/check-sample-cases.ps1
```

## Estrategia De Testing

El proyecto incluye tres niveles de tests:

- Tests de dominio
  Verifican la logica de seleccion basada en streams de forma aislada
- Tests de aplicacion
  Verifican el comportamiento del caso de uso sin HTTP ni base de datos
- Tests de integracion REST
  Verifican el endpoint usando el dataset de H2 en memoria

Clases de test relevantes:

- [ApplicablePriceSelectorTest.java](src/test/java/com/company/pricing_srv/domain/service/ApplicablePriceSelectorTest.java)
- [GetApplicablePriceServiceTest.java](src/test/java/com/company/pricing_srv/application/usecase/GetApplicablePriceServiceTest.java)
- [PriceControllerTest.java](src/test/java/com/company/pricing_srv/infrastructure/in/rest/controller/PriceControllerTest.java)

## Validacion Y Gestion De Errores

- La validacion de parametros se realiza a nivel de controlador
- Cuando no existe precio aplicable, se devuelve `404 Not Found`
- Cuando el formato de los parametros es invalido, Spring MVC devuelve `400 Bad Request`

## Supuestos

- Los intervalos de precio son inclusivos en ambos extremos
- La prioridad es la regla principal de resolucion de conflictos
- El selector usa `startDate` como comparador secundario para mantener un comportamiento determinista

## Notas

- Se han usado nombres neutros en el codigo y en la documentacion
- Lombok se utiliza solo donde reduce boilerplate sin ocultar demasiado el modelo
- La regla principal de negocio se mantiene en dominio en lugar de incrustarla por completo en SQL
