# BrainRidge Banking API

A RESTful API for banking operations built with Spring Boot. This API provides functionality for managing accounts and transactions in a banking system.

## Features

- **Account Management**
  - Create and manage bank accounts
  - Update account information
  - Check account balances
  - Delete accounts

- **Transaction Operations**
  - Transfer funds between accounts
  - Deposit funds
  - Withdraw funds
  - View transaction history

- **Validation**
  - Email validation for accounts
  - Insufficient funds checks
  - Domain-specific business rule validation

## Technology Stack

- Java 17
- Spring Boot 3.1.0
- JUnit 5 for testing
- Maven for dependency management

## Getting Started

### Prerequisites

- JDK 17 or later
- Maven 3.6+ or use the included Maven wrapper

### Installation

1. Clone the repository
   ```bash
   git clone https://github.com/your-username/brainridge-banking-api.git
   cd brainridge-banking-api
   ```

2. Build the project
   ```bash
   ./mvnw clean install
   ```

3. Run the application
   ```bash
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8080`

## API Endpoints

### Account Endpoints

| Method | URL                        | Description                         |
|--------|----------------------------|-------------------------------------|
| POST   | /api/accounts              | Create a new account                |
| GET    | /api/accounts              | Get all accounts                    |
| GET    | /api/accounts/{id}         | Get account by ID                   |
| PUT    | /api/accounts/{id}         | Update account information          |
| DELETE | /api/accounts/{id}         | Delete an account                   |
| GET    | /api/accounts/{id}/balance | Get the balance of an account       |

### Transaction Endpoints

| Method | URL                             | Description                |
|--------|----------------------------------|----------------------------|
| POST   | /api/transactions/transfer      | Transfer funds             |
| POST   | /api/transactions/deposit       | Deposit funds              |
| POST   | /api/transactions/withdraw      | Withdraw funds             |
| GET    | /api/transactions/history/{id}  | Get transaction history    |

## Request/Response Examples

### Create Account

**Request:**
```json
POST /api/accounts
{
  "accountName": "John Doe",
  "accountEmail": "john.doe@gmail.com",
  "initialBalance": 1000.00
}
```

**Response:**
```json
201 Created
{
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "accountName": "John Doe",
  "accountEmail": "john.doe@gmail.com",
  "accountBalance": 1000.00,
  "createdAt": "2023-07-15T10:30:45.123"
}
```

### Transfer Funds

**Request:**
```json
POST /api/transactions/transfer
{
  "fromAccountId": "550e8400-e29b-41d4-a716-446655440000",
  "toAccountId": "550e8400-e29b-41d4-a716-446655440001",
  "amount": 200.00
}
```

**Response:**
```json
201 Created
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "fromAccountId": "550e8400-e29b-41d4-a716-446655440000",
  "toAccountId": "550e8400-e29b-41d4-a716-446655440001",
  "amount": 200.00,
  "timestamp": "2023-07-15T10:35:45.123",
  "type": "TRANSFER"
}
```

## Error Handling

The API uses standard HTTP status codes and includes informative messages in the response body:

```json
{
  "timestamp": "2023-07-15T10:40:45.123",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Account not found with id: 550e8400-e29b-41d4-a716-446655440003"
}
```

Common errors:
- 400 Bad Request: Invalid input parameters or insufficient funds
- 404 Not Found: Resource (account, transaction) not found
- 409 Conflict: Duplicate email address
- 500 Internal Server Error: Unexpected server error

## Running Tests

Run unit tests with:
```bash
./mvnw test
```

Run a specific test class:
```bash
./mvnw test -Dtest=AccountServiceImplTest
```

## Project Structure

```
com.brainridge_banking.api
├── controller        # REST controllers
├── dto               # Data Transfer Objects 
│   ├── request       # Request DTOs
│   └── response      # Response DTOs
├── exception         # Custom exceptions and handler
├── model             # Domain models
├── repository        # Data access layer
├── service           # Business logic
└── util              # Utility classes
```

## Future Enhancements

- Persistent database integration
- User authentication and authorization
- Transaction pagination and filtering
- Account statement generation
- Interest calculation for savings accounts

## License

This project is licensed under the MIT License - see the LICENSE file for details.
