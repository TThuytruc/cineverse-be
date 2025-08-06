# Cineverse Backend

A Spring Boot-based REST API backend for a movie discovery and recommendation platform. This project provides movie data, user authentication, ratings, reviews, and AI-powered movie recommendations.

## 🎬 Features

- **Movie Management**: Browse trending movies, search, and get detailed movie information
- **User Authentication**: Firebase-based authentication system
- **User Profiles**: Manage favorites, watchlist, ratings, and reviews
- **AI Integration**: AI-powered movie recommendations and search
- **Cast Information**: Browse cast details and popular actors
- **Email Notifications**: Email service integration
- **RESTful API**: Comprehensive API with Swagger documentation

## 🏗️ Project Structure

```
cineverse-be/
├── src/main/java/com/hcmus/cineverse_be/
│   ├── client/                    # External service clients
│   │   ├── AIServiceClient.java   # AI service integration
│   │   └── FirebaseAuthClient.java # Firebase authentication
│   ├── config/                    # Configuration classes
│   │   ├── FirebaseConfig.java    # Firebase configuration
│   │   ├── MongoConfig.java       # MongoDB configuration
│   │   ├── SwaggerConfig.java     # API documentation
│   │   └── WebConfig.java         # Web configuration
│   ├── controller/                # REST API controllers
│   │   ├── CastController.java    # Cast-related endpoints
│   │   ├── MovieController.java   # Movie-related endpoints
│   │   ├── ProfileController.java # User profile endpoints
│   │   ├── TestController.java    # Testing endpoints
│   │   └── UserController.java    # User management endpoints
│   ├── dto/                      # Data Transfer Objects
│   │   ├── MovieDetailDTO.java   # Movie detail DTOs
│   │   ├── CastDTO.java          # Cast DTOs
│   │   ├── ReviewDTO.java        # Review DTOs
│   │   └── ...                   # Other DTOs
│   ├── entity/                   # MongoDB entities
│   │   ├── MovieDetail.java      # Movie entity
│   │   ├── User.java             # User entity
│   │   ├── Review.java           # Review entity
│   │   └── ...                   # Other entities
│   ├── exception/                # Custom exceptions
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── ...                   # Other exceptions
│   ├── mapper/                   # Object mappers
│   │   ├── CastMapper.java       # Cast mapping
│   │   ├── MovieMapper.java      # Movie mapping
│   │   └── ProfileMapper.java    # Profile mapping
│   ├── request/                  # Request DTOs
│   │   ├── AddRatingRequest.java
│   │   ├── AddReviewRequest.java
│   │   └── ...                   # Other request DTOs
│   ├── response/                 # Response DTOs
│   │   ├── BasicResponse.java    # Base response
│   │   ├── movie/                # Movie responses
│   │   ├── profile/              # Profile responses
│   │   └── ...                   # Other responses
│   ├── security/                 # Security configuration
│   │   ├── SecurityConfig.java   # Security settings
│   │   ├── FirebaseAuthenticationFilter.java
│   │   └── FirebaseAuthenticationToken.java
│   ├── service/                  # Business logic services
│   │   ├── CastService.java      # Cast business logic
│   │   ├── MovieService.java     # Movie business logic
│   │   ├── ProfileService.java   # Profile business logic
│   │   ├── UserService.java      # User business logic
│   │   └── EmailService.java     # Email service
│   ├── validation/               # Validation classes
│   │   └── UserValidation.java   # User validation
│   └── util/                     # Utility classes
├── src/main/resources/
│   ├── application.properties    # Application configuration
│   ├── static/                  # Static resources
│   └── templates/               # Email templates
├── docker-compose.yaml          # Docker Compose configuration
├── Dockerfile                   # Docker configuration
└── pom.xml                     # Maven dependencies
```

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.8.5 or higher
- MongoDB database
- Firebase project (for authentication)
- AI service API key
- Email service credentials

### Environment Setup

1. Create a `.env` file in the root directory with the following variables:

```env
# Swagger Configuration
HAS_SWAGGER=true

# Firebase Configuration
WEB_API_KEY=your_firebase_web_api_key
FIREBASE_CREDENTIALS_JSON=your_firebase_credentials_json

# Database Configuration
DATABASE_URI=mongodb://localhost:27017/cineverse

# AI Service Configuration
AI_SERVICE_BASE_URL=your_ai_service_url
LLM_API_KEY=your_llm_api_key

# Email Configuration
RESEND_API_KEY=your_resend_api_key
EMAIL_USERNAME=your_email_username
EMAIL_PASSWORD=your_email_password

# Domain Configuration
DOMAIN_AUTH_CALLBACK_URL=your_auth_callback_url
```

### Running the Application

#### Option 1: Using Maven

```bash
# Clone the repository
git clone <repository-url>
cd cineverse-be

# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

#### Option 2: Using Docker

```bash
# Build and run with Docker Compose
docker-compose up --build

# Or run individual containers
docker build -t cineverse-be .
docker run -p 8080:8080 cineverse-be
```

#### Option 3: Using IDE

1. Open the project in your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Ensure Java 17 is configured
3. Run `CineverseBeApplication.java`

## 📚 API Documentation

Once the application is running, you can access:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Base URL**: `http://localhost:8080`

## 🔧 Configuration

### Database Configuration

The application uses MongoDB as the primary database. Configure the connection in `application.properties`:

```properties
spring.data.mongodb.uri=${DATABASE_URI}
```

### Firebase Authentication

Firebase is used for user authentication. Configure the Firebase settings:

```properties
com.example.firebase.web-api-key=${WEB_API_KEY}
com.example.firebase.private-key=${FIREBASE_CREDENTIALS_JSON}
```

### AI Service Integration

The application integrates with an AI service for movie recommendations:

```properties
ai-service.api.base-url=${AI_SERVICE_BASE_URL}
llm.api.key=${LLM_API_KEY}
```

### Email Service

Email notifications are handled through Resend:

```properties
resend.api.key=${RESEND_API_KEY}
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
```

### 🌐 CORS Configuration

By default, the backend allows cross-origin requests from `http://localhost:3000` (for local frontend development).

To allow requests from a different domain or multiple domains, update the following line in `src/main/java/com/hcmus/cineverse_be/security/SecurityConfig.java`:

```java
corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
```

**To change the allowed origins:**
- For a single domain, replace the URL with your frontend's domain.
- For multiple domains, add them to the list, for example:
  ```java
  corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000", "https://yourdomain.com"));
  ```

After making changes, rebuild and restart the backend for the new CORS settings to take effect.

## 🛡️ Security

The application implements Spring Security with Firebase authentication:

- **Public endpoints**: `/movie/**`, `/user/**`, `/public`
- **Protected endpoints**: `/profile/**`, `/movie/rating-point`, `/movie/review`, `/private`


## 📝 Development

### Code Style

- Follow Java naming conventions
- Use Lombok for boilerplate code reduction
- Implement proper exception handling
- Add comprehensive API documentation with Swagger annotations
