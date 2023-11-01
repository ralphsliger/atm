# Proyecto Base Implementando Clean Architecture

Proyecto realizado en Spring Boot haciendo uso de Bancolombia Scatfold para construcción de arquitectura hexagonal.

## Correr Proyecto
Clonar el proyecto `git clone `

Abrir con IDE de preferencia ir a capa /applications y ejecutar `MainApplication.java`

## Endpoints

GET /api/accounts
```
[
    {
        "id": 1,
        "accountNumber": "Daffy Duck",
        "balance": 150.0
    },
    {
        "id": 2,
        "accountNumber": "Elmer Fudd",
        "balance": 70.0
    },
    {
        "id": 3,
        "accountNumber": "Wile E. Coyote",
        "balance": 100.0
    }
]
```

POST /api/accounts
```
BODY:
{
    "accountNumber": "Wile E. Coyote",
    "balance": 0
}

RESPONSE:
{
    "id": 3,
    "accountNumber": "Wile E. Coyote",
    "balance": 0
}
```


POST /api/withdrawal
```
Body:
{
    "accountId": 3,
    "amount": 30
}
Response void
```


POST /api/deposit
```
Body:
{
    "accountId":2,
    "amount": 20
}
Response void
```

POST /api/transfer
```
Body:
{
    "sourceAccountId": 4,
    "destinationAccountId": 1,
    "amount": 20
}
Response void
```


GET /api/transactions
```
[
    {
        "id": 1,
        "transactionType": "DEPOSIT",
        "amount": 111.0,
        "finalBalance": 111.0,
        "accountId": 2,
        "description": "Deposit"
    },
    {
        "id": 2,
        "transactionType": "DEPOSIT",
        "amount": 111.0,
        "finalBalance": 222.0,
        "accountId": 2,
        "description": "Deposit"
    },
    {
        "id": 3,
        "transactionType": "DEPOSIT",
        "amount": 111.0,
        "finalBalance": 333.0,
        "accountId": 2,
        "description": "Deposit"
    },
    {
        "id": 4,
        "transactionType": "DEPOSIT",
        "amount": 111.0,
        "finalBalance": 444.0,
        "accountId": 2,
        "description": "Deposit"
    },
    {
        "id": 5,
        "transactionType": "WITHDRAWAL",
        "amount": 22.0,
        "finalBalance": 422.0,
        "accountId": 2,
        "description": "Withdrawal"
    },
    {
        "id": 6,
        "transactionType": "WITHDRAWAL",
        "amount": 22.0,
        "finalBalance": 400.0,
        "accountId": 2,
        "description": "Withdrawal"
    },
    {
        "id": 7,
        "transactionType": "WITHDRAWAL",
        "amount": 22.0,
        "finalBalance": 378.0,
        "accountId": 2,
        "description": "Withdrawal"
    },
    {
        "id": 8,
        "transactionType": "WITHDRAWAL",
        "amount": 22.0,
        "finalBalance": 356.0,
        "accountId": 2,
        "description": "Withdrawal"
    },
    {
        "id": 9,
        "transactionType": "WITHDRAWAL",
        "amount": 22.0,
        "finalBalance": 334.0,
        "accountId": 2,
        "description": "Withdrawal"
    },
    {
        "id": 10,
        "transactionType": "WITHDRAWAL",
        "amount": 22.0,
        "finalBalance": 312.0,
        "accountId": 2,
        "description": "Withdrawal"
    },
    {
        "id": 11,
        "transactionType": "DEPOSIT",
        "amount": 10000.0,
        "finalBalance": 20000.0,
        "accountId": 4,
        "description": "Deposit"
    },
    {
        "id": 12,
        "transactionType": "WITHDRAWAL",
        "amount": 10.0,
        "finalBalance": 19990.0,
        "accountId": 4,
        "description": "Withdrawal"
    },
    {
        "id": 13,
        "transactionType": "WITHDRAWAL",
        "amount": 10.0,
        "finalBalance": 19980.0,
        "accountId": 4,
        "description": "Withdrawal"
    }
]
```


## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**
