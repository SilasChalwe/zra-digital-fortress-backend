# ZRA Digital Fortress - Spring Boot Backend

A comprehensive tax administration platform backend for the Zambia Revenue Authority (ZRA).

## 🚀 Features

- **User Management**: Registration, authentication, and profile management for individual and business taxpayers
- **Tax Filing**: Income tax, VAT, and company tax filing with automatic calculations
- **Payment Processing**: Multiple payment methods (Mobile Money, Bank Transfer, Cards)
- **AI Integration**: Fraud detection and intelligent chatbot assistance
- **Blockchain Recording**: Immutable transaction records
- **Compliance Tracking**: Automated compliance scoring and gamification
- **Dashboard**: Comprehensive taxpayer dashboard with analytics
- **Security**: JWT authentication, MFA, and role-based access control

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Python 3.8+ (for AI and Blockchain services)

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL with JPA/Hibernate
- **API Documentation**: Swagger/OpenAPI
- **Validation**: Jakarta Validation
- **Build Tool**: Maven

## 📦 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/silaschalwe/zra-digital-fortress-backend.git
cd zra-digital-fortress-backend
```

### 2. Configure Database

Create a PostgreSQL database:

```sql
CREATE DATABASE zra_digital_fortress;
CREATE USER zra_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE zra_digital_fortress TO zra_user;
```

### 3. Configure Application Properties

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/zra_digital_fortress
spring.datasource.username=zra_user
spring.datasource.password=your_password

app.jwt.secret=YourSecretKeyHere
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui/index.html
```

## 🔐 Authentication

### Register Individual Taxpayer

```http
POST /api/v1/auth/register/individual
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-01-01",
  "nrcNumber": "123456/78/1",
  "email": "john.doe@example.com",
  "phoneNumber": "+260971234567",
  "password": "SecurePass123!",
  "physicalAddress": "123 Main St, Lusaka",
  "gender": "MALE",
  "employmentStatus": "EMPLOYED",
  "estimatedAnnualIncome": 120000.00
}
```

### Register Business Taxpayer

```http
POST /api/v1/auth/register/business
Content-Type: application/json

{
  "businessName": "ABC Limited",
  "businessType": "LIMITED_COMPANY",
  "registrationNumber": "REG123456",
  "dateOfIncorporation": "2020-01-01",
  "businessAddress": "456 Commerce St, Lusaka",
  "contactPersonName": "Jane Smith",
  "contactPersonPhone": "+260971234568",
  "contactPersonEmail": "jane@abc.zm",
  "email": "info@abc.zm",
  "phoneNumber": "+260971234569",
  "password": "SecurePass123!",
  "numberOfEmployees": 50,
  "sector": "RETAIL",
  "estimatedAnnualTurnover": 5000000.00,
  "vatRegistered": true
}
```

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "tpinOrEmail": "john.doe@example.com",
  "password": "SecurePass123!"
}
```

Response:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": "uuid",
      "tpin": "123456789A",
      "email": "john.doe@example.com",
      "userType": "INDIVIDUAL"
    }
  }
}
```

## 💰 Tax Filing

### Submit Income Tax Filing

```http
POST /api/v1/tax-filings
Authorization: Bearer {token}
Content-Type: application/json

{
  "taxType": "INCOME_TAX",
  "taxYear": 2024,
  "taxPeriod": 1,
  "employmentIncome": 120000.00,
  "businessIncome": 0.00,
  "rentalIncome": 0.00,
  "investmentIncome": 0.00,
  "otherIncome": 0.00,
  "nappsaContributions": 12000.00,
  "medicalExpenses": 5000.00,
  "educationExpenses": 3000.00,
  "insurancePremiums": 2000.00,
  "otherDeductions": 0.00,
  "saveDraft": false
}
```

### Get All Filings

```http
GET /api/v1/tax-filings
Authorization: Bearer {token}
```

## 💳 Payment Processing

### Process Payment

```http
POST /api/v1/payments
Authorization: Bearer {token}
Content-Type: application/json

{
  "taxFilingId": "filing-uuid",
  "amount": 15000.00,
  "paymentMethod": "MOBILE_MONEY_MTN",
  "phoneNumber": "+260971234567"
}
```

## 📊 Dashboard

### Get Dashboard Data

```http
GET /api/v1/dashboard
Authorization: Bearer {token}
```

Response includes:
- User summary
- Compliance score
- Upcoming obligations
- Recent activities
- Notifications
- Payment summary

## 🤖 Chatbot

### Chat with AI Assistant

```http
POST /api/v1/chatbot/chat
Authorization: Bearer {token}
Content-Type: application/json

{
  "message": "How do I file my income tax?",
  "language": "EN"
}
```

## 🏗️ Project Structure

```
src/main/java/zm/zra/digitalfortress/
├── config/              # Configuration classes
├── controller/          # REST controllers
├── dto/                 # Data Transfer Objects
│   ├── request/        # Request DTOs
│   └── response/       # Response DTOs
├── exception/          # Custom exceptions
├── integration/        # External service integrations
├── model/              # JPA entities
├── repository/         # Spring Data repositories
├── security/           # Security components
├── service/            # Business logic
└── util/               # Utility classes
```

## 🔒 Security Features

- JWT-based authentication
- Password encryption with BCrypt
- Multi-factor authentication (MFA)
- Role-based access control (RBAC)
- Session management
- Audit logging
- CORS configuration

## 🧪 Testing

Run tests:

```bash
mvn test
```

## 📈 Monitoring

Health check endpoint:

```
GET /actuator/health
```

## 🚀 Deployment

### Docker Deployment

```bash
docker build -t zra-digital-fortress-backend .
docker run -p 8080:8080 zra-digital-fortress-backend
```

### Environment Variables

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/zra_digital_fortress
export SPRING_DATASOURCE_USERNAME=zra_user
export SPRING_