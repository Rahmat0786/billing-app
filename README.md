# Spring Cloud Microservices Billing Application

A production-ready billing application built with Spring Boot 3.2 and Spring Cloud, following a microservices architecture pattern with service discovery, API gateway, JWT authentication, and inter-service communication via Feign clients.

---

## Architecture Diagram

```
                        ┌─────────────────────────────────────────┐
                        │           Client (Browser/App)          │
                        └──────────────────┬──────────────────────┘
                                           │ HTTP :8080
                        ┌──────────────────▼──────────────────────┐
                        │              API Gateway                │
                        │   (JWT Validation, Request Routing)     │
                        │            Port: 8080                   │
                        └────┬──────────────┬──────────────┬──────┘
                             │              │              │
                   /api/auth/**    /api/products/**  /api/billing/**
                             │              │              │
              ┌──────────────▼──┐  ┌────────▼────────┐  ┌─▼─────────────────┐
              │  Auth Service   │  │ Product Service  │  │  Billing Service  │
              │   Port: 8081    │  │   Port: 8082     │  │    Port: 8083     │
              │  H2 DB (authdb) │  │ H2 DB(productdb) │  │ H2 DB(billingdb)  │
              └─────────────────┘  └─────────────────┘  └───────┬───────────┘
                                                                  │ Feign Client
                                                        ┌─────────▼─────────┐
                                                        │  Product Service  │
                                                        └───────────────────┘

              ┌─────────────────────────────────────────────────────────────┐
              │             Eureka Server (Service Discovery)               │
              │                       Port: 8761                            │
              └─────────────────────────────────────────────────────────────┘

              ┌─────────────────────────────────────────────────────────────┐
              │               Config Server (Central Config)                │
              │                       Port: 8888                            │
              └─────────────────────────────────────────────────────────────┘
```

---

## Services Summary

| Service         | Port | Description                                               |
|-----------------|------|-----------------------------------------------------------|
| eureka-server   | 8761 | Netflix Eureka Service Discovery & Registry               |
| config-server   | 8888 | Spring Cloud Config Server for centralized configuration  |
| api-gateway     | 8080 | Spring Cloud Gateway with JWT authentication filter       |
| auth-service    | 8081 | User registration, login, JWT token generation            |
| product-service | 8082 | Product CRUD, stock management, category filtering        |
| billing-service | 8083 | Order creation, invoice generation, billing management    |

---

## Prerequisites

- **Java 17** (JDK 17+)
- **Maven 3.8+**
- **Docker** and **Docker Compose** (for containerized deployment)
- **Git**

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Rahmat0786/billing-app.git
cd billing-app
```

### 2. Build All Services

```bash
mvn clean package -DskipTests
```

### 3. Run with Docker Compose

```bash
docker-compose up --build
```

### 4. Run Locally (without Docker)

Start services in this order:

```bash
# Terminal 1 - Eureka Server
cd eureka-server && mvn spring-boot:run

# Terminal 2 - Config Server
cd config-server && mvn spring-boot:run

# Terminal 3 - API Gateway
cd api-gateway && mvn spring-boot:run

# Terminal 4 - Auth Service
cd auth-service && mvn spring-boot:run

# Terminal 5 - Product Service
cd product-service && mvn spring-boot:run

# Terminal 6 - Billing Service
cd billing-service && mvn spring-boot:run
```

---

## API Documentation

All requests go through the API Gateway at `http://localhost:8080`.  
Endpoints under `/api/products/**` and `/api/billing/**` require a `Bearer` JWT token.

### Authentication API

#### Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Sample Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "ROLE_USER",
  "message": "User registered successfully"
}
```

#### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Sample Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "ROLE_USER",
  "message": "Login successful"
}
```

#### Validate Token

```bash
curl -X GET http://localhost:8080/api/auth/validate \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

### Product API

> **Note:** Replace `<YOUR_TOKEN>` with the JWT token from login/register.

#### Get All Products

```bash
curl -X GET http://localhost:8080/api/products/ \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

**Sample Response:**
```json
[
  {
    "id": 1,
    "name": "Laptop Pro",
    "description": "High-performance laptop for professionals",
    "price": 999.99,
    "quantity": 50,
    "category": "Electronics",
    "createdBy": 1,
    "createdAt": "2024-01-15T10:00:00",
    "updatedAt": "2024-01-15T10:00:00"
  }
]
```

#### Get Product by ID

```bash
curl -X GET http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

#### Create a Product

```bash
curl -X POST http://localhost:8080/api/products/ \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Product",
    "description": "Product description",
    "price": 49.99,
    "quantity": 100,
    "category": "Electronics"
  }'
```

#### Update a Product

```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Laptop Pro",
    "description": "Updated description",
    "price": 1099.99,
    "quantity": 45,
    "category": "Electronics"
  }'
```

#### Delete a Product

```bash
curl -X DELETE http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

#### Search Products by Name

```bash
curl -X GET "http://localhost:8080/api/products/search?name=laptop" \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

#### Get Products by Category

```bash
curl -X GET http://localhost:8080/api/products/category/Electronics \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

### Billing API

#### Create an Order

```bash
curl -X POST http://localhost:8080/api/billing/orders \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "userEmail": "john@example.com",
    "items": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ]
  }'
```

**Sample Response:**
```json
{
  "id": 1,
  "orderNumber": "ORD-A1B2C3D4",
  "userId": 1,
  "userEmail": "john@example.com",
  "status": "PENDING",
  "totalAmount": 2029.97,
  "items": [
    {
      "id": 1,
      "productId": 1,
      "productName": "Laptop Pro",
      "quantity": 2,
      "unitPrice": 999.99,
      "subtotal": 1999.98
    },
    {
      "id": 2,
      "productId": 2,
      "productName": "Wireless Mouse",
      "quantity": 1,
      "unitPrice": 29.99,
      "subtotal": 29.99
    }
  ],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

#### Get My Orders

```bash
curl -X GET http://localhost:8080/api/billing/orders \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

#### Get Order by ID

```bash
curl -X GET http://localhost:8080/api/billing/orders/1 \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

#### Get All Orders (Admin)

```bash
curl -X GET http://localhost:8080/api/billing/orders/all \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

#### Update Order Status

```bash
curl -X PUT "http://localhost:8080/api/billing/orders/1/status?status=CONFIRMED" \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

Available statuses: `PENDING`, `CONFIRMED`, `CANCELLED`, `DELIVERED`

#### Get Invoice for an Order

```bash
curl -X GET http://localhost:8080/api/billing/invoices/order/1 \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

**Sample Response:**
```json
{
  "id": 1,
  "invoiceNumber": "INV-X7Y8Z9W0",
  "orderNumber": "ORD-A1B2C3D4",
  "userId": 1,
  "totalAmount": 2029.97,
  "taxAmount": 365.39,
  "taxRate": 0.18,
  "grandTotal": 2395.36,
  "status": "ISSUED",
  "issuedAt": "2024-01-15T10:30:00",
  "dueDate": "2024-02-14T10:30:00"
}
```

#### Get My Invoices

```bash
curl -X GET http://localhost:8080/api/billing/invoices \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

---

## H2 Console Access

Each service has an in-memory H2 database accessible via web console during development:

| Service         | H2 Console URL                        | JDBC URL              |
|-----------------|---------------------------------------|-----------------------|
| auth-service    | http://localhost:8081/h2-console      | jdbc:h2:mem:authdb    |
| product-service | http://localhost:8082/h2-console      | jdbc:h2:mem:productdb |
| billing-service | http://localhost:8083/h2-console      | jdbc:h2:mem:billingdb |

**Credentials:** Username: `sa`, Password: *(leave empty)*

---

## Eureka Dashboard

Visit [http://localhost:8761](http://localhost:8761) to view all registered services.

---

## Pre-loaded Product Data

The product-service auto-populates with 5 sample products on startup:

| ID | Name          | Price   | Category    |
|----|---------------|---------|-------------|
| 1  | Laptop Pro    | $999.99 | Electronics |
| 2  | Wireless Mouse| $29.99  | Electronics |
| 3  | Office Chair  | $299.99 | Furniture   |
| 4  | Notebook Set  | $12.99  | Stationery  |
| 5  | Coffee Maker  | $79.99  | Appliances  |

---

## Troubleshooting

### Services not registering with Eureka
- Ensure `eureka-server` starts first and is healthy before other services
- Check that `eureka.client.service-url.defaultZone` points to the correct Eureka URL
- Wait 30–60 seconds for services to register after startup

### JWT Token Invalid / Unauthorized
- Ensure the `app.jwt.secret` in `api-gateway/application.yml` and `auth-service/application.yml` match exactly
- Check token expiration (default: 24 hours / 86400000ms)

### Feign Client Connection Error (billing-service → product-service)
- Ensure `product-service` is running and registered in Eureka before creating orders
- Check Eureka dashboard to confirm `PRODUCT-SERVICE` appears in registered instances

### Port Already in Use
- Kill the process using the port: `lsof -ti:<port> | xargs kill -9`
- Or change the port in the service's `application.yml`

### Docker Build Fails
- Ensure you have run `mvn clean package -DskipTests` before `docker-compose up --build`
- Each service needs its JAR built in the `target/` directory before Docker can copy it

### H2 Console Not Loading
- H2 console is only available when running locally (not in production mode)
- Ensure `spring.h2.console.enabled: true` is set in the service's `application.yml`
