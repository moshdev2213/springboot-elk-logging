# Spring Boot Microservices with ELK Stack Demo

This project demonstrates a microservices architecture using Spring Boot with centralized logging using the ELK (Elasticsearch, Logstash, Kibana) stack. It includes two microservices (Order Service and Inventory Service) that communicate via Kafka events.


<img width="4320" height="3360" alt="Image" src="https://github.com/user-attachments/assets/8468654e-2bbb-4527-8d80-11ed7e3b87f5" />

---
## Environment-Specific Logging

This project now includes **three distinct logging environments** following industry best practices:

- **🔧 Development (dev)**: Verbose logging for debugging and development
- **🧪 Staging**: Balanced logging for testing and monitoring
- **🚀 Production (prod)**: Minimal logging for performance and security

Each environment has its own:

- Spring Boot configuration
- Docker Compose setup
- Logging levels and formats

## Services Overview

### 1. Order Service (Port 8080)

- Manages customer orders
- Publishes order events to Kafka
- RESTful API for CRUD operations
- Database: `order_db`

### 2. Inventory Service (Port 8081)

- Manages product inventory
- Consumes order events from Kafka
- Updates stock levels automatically
- Database: `inventory_db`

### 3. Infrastructure Services

- **PostgreSQL**: Two separate databases for each service
- **Kafka**: Event streaming platform for inter-service communication
- **Elasticsearch**: Log storage and search engine
- **Logstash**: Log processing and transformation
- **Kibana**: Log visualization and analysis
- **Filebeat**: Log collection and forwarding

## Prerequisites

- Docker and Docker Compose
- Java 17 or higher
- Maven 3.6 or higher

## 🚀 Quick Start

### Option 1: Start with Default Environment

```bash
# Start all services (PostgreSQL, Kafka, ELK stack)
docker-compose up -d

# Build and run the Spring Boot services
cd order &&  mvn clean package -DskipTests && java -jar target/order-0.0.1-SNAPSHOT.jar
cd ../inventory mvn clean package -DskipTests && java -jar target/inventory-0.0.1-SNAPSHOT.jar
```

### Option 2: Environment-Specific Setup

#### Development Environment

```bash
# Start development infrastructure
docker compose -f docker-compose.yml -f docker-compose-dev.yml up -d

# Run services with dev profile
cd order && mvn clean package -DskipTests && java "-Dspring.profiles.active=dev" -jar "target\order-0.0.1-SNAPSHOT.jar"
cd ../inventory && mvn clean package -DskipTests && java "-Dspring.profiles.active=dev" -jar "target\inventory-0.0.1-SNAPSHOT.jar"
```

#### Staging Environment

```bash
# Start staging infrastructure
 docker compose -f docker-compose.yml -f docker-compose-staging.yml up -d

# Run services with staging profile
cd order && mvn clean package -DskipTests && java "-Dspring.profiles.active=staging" -jar "target\order-0.0.1-SNAPSHOT.jar"
cd ../inventory && mvn clean package -DskipTests && java "-Dspring.profiles.active=staging" -jar "target\inventory-0.0.1-SNAPSHOT.jar"
```

#### Production Environment

```bash
# Start production infrastructure
 docker compose -f docker-compose.yml -f docker-compose-production.yml up -d

# Run services with production profile
cd order && mvn clean package -DskipTests && java "-Dspring.profiles.active=prod" -jar "target\order-0.0.1-SNAPSHOT.jar"
cd ../inventory && mvn clean package -DskipTests && java "-Dspring.profiles.active=prod" -jar "target\inventory-0.0.1-SNAPSHOT.jar"
```
<!-- 
### 3. Access Services

#### Default Environment

- **Order Service**: http://localhost:8080
- **Inventory Service**: http://localhost:8081
- **PG Admin**: http://localhost:5050 (admin@local.local / admin123)

#### Development Environment

- **Order Service**: http://localhost:8080
- **Inventory Service**: http://localhost:8081
- **PG Admin**: http://localhost:5050 (admin@dev.local / admin123)

#### Staging Environment

- **Order Service**: http://localhost:8080
- **Inventory Service**: http://localhost:8081
- **PG Admin**: http://localhost:5051 (admin@staging.local / admin123)

#### Production Environment

- **Order Service**: http://localhost:8080
- **Inventory Service**: http://localhost:8081
- **PG Admin**: http://localhost:5052 (admin@prod.local / admin123) -->

## API Endpoints

### Order Service

```
GET    /api/orders              - Get all orders
GET    /api/orders/{id}         - Get order by ID
GET    /api/orders/customer/{email} - Get orders by customer email
POST   /api/orders              - Create new order
PUT    /api/orders/{id}/status  - Update order status
DELETE /api/orders/{id}         - Delete order
```

### Inventory Service

```
GET    /api/products            - Get all products
GET    /api/products/{id}       - Get product by ID
POST   /api/products            - Create new product
PUT    /api/products/{id}       - Update product
DELETE /api/products/{id}       - Delete product
POST   /api/products/{id}/stock - Update stock quantity
```

## Testing the System

### 1. Create Products (Inventory Service)

```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "A test product",
    "price": 29.99,
    "stockQuantity": 100,
    "category": "Test"
  }'
```

### 2. Create Order (Order Service)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "orderItems": [
      {
        "productId": 1,
        "quantity": 2
      }
    ]
  }'
```

### 3. Check Stock Update (Inventory Service)

```bash
curl http://localhost:8081/api/products/1
```

## Logging and Monitoring

### 1. View Logs in Kibana

1. Open Kibana at the appropriate port for your environment
2. Go to **Discover** section
3. Create index pattern based on your environment:
   - **Development**: `microservices-logs-dev-*`
   - **Staging**: `microservices-logs-staging-*`
   - **Production**: `microservices-logs-prod-*`
4. View and search logs from both services

### 2. Log Structure

Logs include:

- Service name and operation
- Request/response details
- Kafka event publishing/consumption
- Database operations
- Error details with stack traces

### 3. Environment-Specific Logging

#### Development

- **Log Level**: DEBUG, TRACE, INFO, WARN, ERROR
- **SQL Logging**: Enabled with parameter binding
- **Console Output**: Colored, detailed formatting
- **Performance**: Detailed timing and metrics

#### Staging

- **Log Level**: INFO, WARN, ERROR
- **SQL Logging**: Disabled for performance
- **Console Output**: Standard formatting
- **Performance**: Balanced timing

#### Production

- **Log Level**: ERROR, WARN only
- **SQL Logging**: Completely disabled
- **Console Output**: Minimal formatting
- **Performance**: Optimized for speed

### 4. Environment-Specific Infrastructure

Each environment has its own infrastructure configuration with different ports:

#### Development

- **PostgreSQL**: Port 5432
- **PG Admin**: Port 5050

#### Staging

- **PostgreSQL**: Port 5433
- **PG Admin**: Port 5051

#### Production

- **PostgreSQL**: Port 5434
- **PG Admin**: Port 5052

**Note**: Each environment connects to its own database instance, ensuring complete isolation between environments.

### 5. Sample Log Queries

```kibana
# View all logs for specific environment
index:microservices-logs-dev-*
index:microservices-logs-staging-*
index:microservices-logs-prod-*

# Filter by service
fields.service:order-service
fields.service:inventory-service

# Filter by log level
level:ERROR
level:WARN
level:INFO
level:DEBUG

# Search for specific operations
message:*order created*
message:*SQL*
message:*Performance Metric*
```

## Project Structure

```
elk_springboot/
├── order/                          # Order Service
│   ├── src/main/java/com/learn/order/
│   │   ├── controller/            # REST controllers
│   │   ├── service/               # Business logic
│   │   ├── entity/                # JPA entities
│   │   ├── repository/            # Data access
│   │   ├── dto/                   # Data transfer objects
│   │   ├── event/                 # Kafka events
│   │   └── config/                # Configuration
│   ├── src/main/resources/
│   │   ├── application.properties # Default config
│   │   ├── application-dev.properties    # Development config
│   │   ├── application-staging.properties # Staging config
│   │   └── application-prod.properties   # Production config
│   └── pom.xml
├── inventory/                      # Inventory Service
│   ├── src/main/java/com/learn/inventory/
│   │   ├── controller/            # REST controllers
│   │   ├── service/               # Business logic
│   │   ├── entity/                # JPA entities
│   │   ├── repository/            # Data access
│   │   ├── kafka/                 # Kafka consumers
│   │   ├── config/                # Configuration
│   │   └── service/LoggingService.java # Environment-aware logging
│   ├── src/main/resources/
│   │   ├── application.properties # Default config
│   │   ├── application-dev.properties    # Development config
│   │   ├── application-staging.properties # Staging config
│   │   └── application-prod.properties   # Production config
│   └── pom.xml
├── docker-compose.yml             # Default infrastructure
├── init-db.sql                   # Database initialization
├── logstash/                     # Logstash configuration
│   ├── pipeline/
│   │   ├── logstash.conf         # Default pipeline
│   └── config/
├── filebeat/                     # Filebeat configuration
│   ├── filebeat.yml              # Default config
├── logs/                         # Application logs
└── README.md                     # This file
```

## Configuration

### Database Configuration

- **Order Service**: `jdbc:postgresql://localhost:5432/order_db`
- **Inventory Service**: `jdbc:postgresql://localhost:5432/inventory_db`
- **Credentials**: postgres/password

### Kafka Configuration

- **Bootstrap Servers**: localhost:9092
- **Topic**: `order-created`
- **Consumer Group**: `inventory-group`

### Logging Configuration

- **Log Files**: `logs/{service-name}-{environment}.log`
- **Log Level**: Environment-specific (see Environment-Specific Logging section)
- **Format**: Structured logging with timestamps and correlation IDs

## Troubleshooting

### Common Issues

1. **Services won't start**: Check if PostgreSQL and Kafka are running
2. **Database connection failed**: Verify database is created and accessible
3. **Kafka connection failed**: Ensure Kafka are running
4. **Logs not appearing in Kibana**: Check Filebeat and Logstash status
5. **Wrong environment configuration**: Ensure correct profile is active

### Useful Commands

```bash
# Check service status for specific environment
docker-compose -f docker-compose-dev.yml ps
docker-compose -f docker-compose-staging.yml ps
docker-compose -f docker-compose-prod.yml ps

# View service logs for specific environment
docker-compose -f docker-compose-dev.yml logs -f
docker-compose -f docker-compose-staging.yml logs -f
docker-compose -f docker-compose-prod.yml logs -f

# Restart specific service
docker-compose -f docker-compose-dev.yml restart [service-name]

# Stop all services for specific environment
docker-compose -f docker-compose-dev.yml down
docker-compose -f docker-compose-staging.yml down
docker-compose -f docker-compose-prod.yml down

# Clean up volumes for specific environment
docker-compose -f docker-compose-dev.yml down -v
docker-compose -f docker-compose-staging.yml down -v
docker-compose -f docker-compose-prod.yml down -v
```

## Learning Objectives

This project demonstrates:

1. **Microservices Architecture**: Service separation and communication
2. **Event-Driven Communication**: Using Kafka for asynchronous messaging
3. **Centralized Logging**: ELK stack integration
4. **Environment-Specific Configuration**: Different logging levels for different environments
5. **Database Design**: Separate databases per service
6. **RESTful APIs**: CRUD operations implementation
7. **Docker Orchestration**: Multi-service deployment
8. **Log Aggregation**: Collecting logs from multiple services
9. **Real-time Monitoring**: Log analysis and visualization

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is for educational purposes.
