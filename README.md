# OAuth2 Client Application

A Spring Boot server-side rendering (SSR) web application that demonstrates OAuth2 authentication flow. Users log in through an OAuth2 authorization server and access protected resources from a resource server.

## Project Overview

This project is a demo OAuth2 client application that showcases the complete OAuth2 authorization code flow. It provides a user-friendly interface for logging in via OAuth2, displays authenticated user information, and accesses protected APIs from the resource server using the access token. The application uses Thymeleaf for server-side HTML rendering.

## Features

- **OAuth2 Authorization Code Flow**: Standard OAuth2 login mechanism
- **OpenID Connect Support**: Retrieves user information (email, username, roles)
- **Server-Side Rendering**: Dynamic HTML pages rendered with Thymeleaf
- **Token Management**: Automatically manages access tokens and refresh tokens
- **Role-Based UI**: Displays different UI elements based on user roles (USER/ADMIN)
- **Protected Resource Access**: Calls resource server APIs with valid access tokens
- **Secure Logout**: Logs out from both client and auth server

## Tech Stack

- **Framework**: Spring Boot 3.x
- **Security**: Spring Security 6.x, OAuth2 Client
- **Template Engine**: Thymeleaf
- **HTTP Client**: RestTemplate
- **Build Tool**: Maven
- **Language**: Java 17+
- **Frontend**: Bootstrap 5


## Installation

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Git
- Auth Server running on http://127.0.0.1:8081
- Resource Server running on http://localhost:8082

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd client-app
```

### Step 2: Configure application.yml

The `application.yml` is already configured. Verify the following settings:

```yaml
server:
  port: 8083

spring:
  security:
    oauth2:
      client:
        registration:
          demo-client:
            client-id: demo-client
            client-secret: secret
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: openid, profile
        provider:
          demo-client:
            issuer-uri: http://127.0.0.1:8081
            authorization-uri: http://127.0.0.1:8081/oauth2/authorize
            token-uri: http://127.0.0.1:8081/oauth2/token
            user-info-uri: http://127.0.0.1:8081/userinfo
            user-name-attribute: sub
```

### Step 3: Build the Project

```bash
mvn clean install
```

### Step 4: Run the Application

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/client-app-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8083`

### Step 5: Access the Application

1. Open your browser and navigate to: http://localhost:8083
2. You will be redirected to the login page
3. Click "Login with OAuth"
4. Enter your credentials from the auth server
5. Grant consent to the requested scopes
6. You will be logged in and see your user information

## Application Flow

### 1. Initial Access
```
User visits http://localhost:8083/
↓
Redirected to http://localhost:8083/login
↓
User sees login page with "Login with OAuth" button
```

### 2. OAuth2 Login
```
User clicks "Login with OAuth"
↓
Client app redirects to auth server authorization endpoint
http://127.0.0.1:8081/oauth2/authorize?client_id=...&redirect_uri=...
↓
User sees auth server login page
↓
User enters email and password
↓
User sees consent screen
↓
User clicks "Consent"
↓
Auth server redirects back to client with authorization code
http://localhost:8083/login/oauth2/code/demo-client?code=...
```

### 3. Token Exchange
```
Client app receives authorization code
↓
Client app exchanges code for tokens (backend):
POST http://127.0.0.1:8081/oauth2/token
{
  "code": "...",
  "client_id": "demo-client",
  "client_secret": "secret"
}
↓
Auth server returns:
{
  "access_token": "...",
  "refresh_token": "...",
  "id_token": "..."
}
```

### 4. User Information Extraction
```
Client app decodes ID token
↓
Extracts user claims:
- sub (subject/user ID)
- email
- username
- role (USER or ADMIN)
↓
Renders home page with user information
```

### 5. Accessing Protected Resources
```
User clicks "View All Products" button
↓
Client app calls resource server API:
GET http://localhost:8082/api/products
Authorization: Bearer <access_token>
↓
Resource server validates token
↓
Resource server returns products list
↓
Client app renders products page
```

## Project Structure

```
src/main/java/com/example/client/
├── ClientAppApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── RestTemplateConfig.java
└── controller/
    └── HomeController.java

src/main/resources/
├── application.yml
└── templates/
    ├── login.html
    ├── home.html
    ├── products.html
    └── create-product.html
```


## API Endpoints

### Client Application Endpoints

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|-----------------|
| GET | `/` | Redirects to `/home` | None |
| GET | `/login` | Login page | None |
| GET | `/home` | Home page with user info | Required |
| GET | `/products` | Products list page | Required |
| GET | `/create-product` | Create product form | Required (ADMIN) |
| POST | `/create-product` | Create product | Required (ADMIN) |
| GET | `/logout` | Logout | Required |

## Configuration Details

### OAuth2 Client Registration

```yaml
client-id: demo-client
client-secret: secret
authorization-grant-type: authorization_code
redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
scope: openid, profile
```

**Scope Explanation**:
- `openid`: Request ID token with user information
- `profile`: Request user profile claims (username, email)

### OAuth2 Provider Configuration

```yaml
issuer-uri: http://127.0.0.1:8081
authorization-uri: http://127.0.0.1:8081/oauth2/authorize
token-uri: http://127.0.0.1:8081/oauth2/token
user-info-uri: http://127.0.0.1:8081/userinfo
user-name-attribute: sub
```

## Token Usage

### Access Token

Used to access protected resources from the resource server.

**Header Format**:
```
Authorization: Bearer <access_token>
```

**Example API Call**:
```bash
curl -H "Authorization: Bearer eyJhbGc..." \
     http://localhost:8082/api/products
```

### ID Token

Contains user information claims extracted from the JWT.

**Claims**:
```json
{
  "sub": "user@example.com",
  "email": "user@example.com",
  "name": "John Doe",
  "roles": ["ROLE_USER"]
}
```

### Refresh Token

Used to obtain new access tokens without re-authentication.

Automatically handled by Spring Security OAuth2 Client.

## Role-Based UI

The application displays different UI elements based on user roles:

### USER Role

- ✅ View all products
- ❌ Create products

### ADMIN Role

- ✅ View all products
- ✅ Create products


## Authentication Flow Diagram

```
User
  ↓
Client App (8083)
  ↓
Auth Server (8081)
  ↓
User enters credentials
  ↓
Auth Server validates
  ↓
User grants consent
  ↓
Auth Server redirects with code
  ↓
Client exchanges code for tokens
  ↓
Client renders home page
  ↓
Client calls Resource Server (8082) with access token
  ↓
Resource Server validates token
  ↓
Resource Server returns data
  ↓
Client displays data to user
```





## Related Projects

- **Authorization Server** (Port 8081): OAuth2 authorization and token issuing
- **Resource Server** (Port 8082): Protected APIs with product data


