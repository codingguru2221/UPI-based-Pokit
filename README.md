# UPI-based Pokit - Parent-Controlled Pocket Money Application

## Project Overview

In India, most parents give their children monthly pocket money for daily expenses such as food, travel, and small purchases. However, children below 18 years of age usually cannot use UPI directly because a bank account and ATM card are mandatory. This creates a gap where parents want to encourage digital payments and financial learning, but also want control, safety, and visibility over their children's spending.

To solve this problem, we propose a *Parent-Controlled, UPI-Powered Pocket Money Application* where a parent links their own UPI account and creates *sub-accounts for their children. The parent assigns a fixed monthly pocket money amount, which is distributed across predefined spending categories such as food, travel, online shopping, and others. Children can make UPI payments only at approved and limited places, while every transaction is recorded and visible to the parent.

## Key Features

1. **Parent-Controlled Accounts**: Parents can create and manage sub-accounts for their children
2. **Category-Based Spending Limits**: Monthly pocket money is distributed across predefined categories
3. **Flexible Category Management**: Children can reallocate limits between categories during emergencies
4. **Real-Time Parent Approval**: Critical transactions trigger real-time notifications for parent approval
5. **Seamless Transition**: Accounts can be converted to normal UPI accounts when the child turns 18
6. **Transparent Dashboard**: Both parents and children have dashboards for monitoring spending

## Technology Stack

- **Backend**: Java with Spring Boot
- **Database**: H2 (in-memory for development)
- **API**: RESTful APIs
- **Build Tool**: Maven

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/pokit/upipocket/
│   │       ├── Application.java          # Main application class
│   │       ├── controller/               # REST controllers
│   │       ├── model/                    # Entity classes
│   │       ├── repository/               # JPA repositories
│   │       ├── service/                  # Business logic
│   │       ├── config/                   # Configuration classes
│   │       └── DataInitializer.java      # Sample data initializer
│   └── resources/
│       └── application.properties        # Application configuration
└── test/                                 # Test classes (to be added)
```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Building the Project

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

Or alternatively:

```bash
java -jar target/upi-pocket-money-1.0-SNAPSHOT.jar
```

### Accessing the Application

- **API Base URL**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console (for development)

## API Endpoints

### User Management
- `POST /api/users` - Create a new user
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users/parent/{parentId}/children` - Get all children of a parent
- `PUT /api/users` - Update user information
- `DELETE /api/users/{id}` - Delete a user
- `GET /api/users/parents` - Get all parents

### Account Management
- `POST /api/accounts` - Create a new account
- `GET /api/accounts/{id}` - Get account by ID
- `GET /api/accounts/upi/{upiId}` - Get account by UPI ID
- `GET /api/accounts/user/{userId}` - Get all accounts of a user
- `PUT /api/accounts` - Update account information
- `DELETE /api/accounts/{id}` - Delete an account
- `POST /api/accounts/{accountId}/allocate` - Allocate monthly pocket money
- `POST /api/accounts/{accountId}/transfer` - Transfer between categories
- `GET /api/accounts/{accountId}/balance` - Get available balance
- `GET /api/accounts/{accountId}/categories` - Get category limits

### Transaction Management
- `POST /api/transactions` - Create a new transaction
- `GET /api/transactions/{id}` - Get transaction by ID
- `GET /api/transactions/account/{accountId}` - Get all transactions of an account
- `GET /api/transactions/account/{accountId}/pending` - Get pending transactions of an account
- `PUT /api/transactions/{transactionId}/status` - Update transaction status
- `DELETE /api/transactions/{id}` - Delete a transaction
- `POST /api/transactions/process-payment` - Process a payment

## Sample Data

On startup, the application initializes with sample data:
- A parent user (John Doe)
- A child user (Jane Doe)
- An account for the child with ₹1000 monthly pocket money
- Category limits:
  - Food: ₹400
  - Travel: ₹300
  - Online Shopping: ₹200
  - Entertainment: ₹100

## Banking Simulation Features

The application simulates banking operations including:
1. **Account Management**: Creating and managing pocket money accounts
2. **Category Limit Enforcement**: Ensuring spending stays within predefined limits
3. **Flexible Transfers**: Allowing reallocation of funds between categories
4. **Transaction Processing**: Handling UPI-like transactions with appropriate checks
5. **Parental Controls**: Requiring approval for certain transactions
6. **Balance Tracking**: Maintaining accurate account balances

## Future Enhancements

1. **Frontend Dashboard**: Web-based UI for parents and children
2. **Mobile Application**: Native mobile apps for iOS and Android
3. **UPI Integration**: Actual integration with UPI payment systems
4. **Notification System**: Email/SMS notifications for transactions and approvals
5. **Analytics Dashboard**: Spending insights and financial education tools
6. **Security Enhancements**: Multi-factor authentication and encryption

## License

This project is licensed under the MIT License - see the LICENSE file for details.