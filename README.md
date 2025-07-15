# Book Store Application

A multi-module application with a React frontend and Spring Boot backend.

## Project Structure

```
book-store/
├── backend/                 # Spring Boot backend module
│   ├── src/                # Java source code
│   ├── pom.xml            # Backend Maven configuration
│   ├── compose.yaml       # Docker Compose for database
│   └── docker/            # Docker configuration
├── frontend/               # React frontend module
│   ├── src/               # React application source
│   │   ├── public/        # Static files
│   │   ├── src/           # React components and logic
│   │   └── package.json   # NPM dependencies
│   └── pom.xml           # Frontend Maven configuration
└── pom.xml               # Parent Maven configuration
```

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Node.js 18.x (automatically installed by frontend-maven-plugin)
- Docker and Docker Compose (for database)

## Getting Started

### 1. Start the Database

Navigate to the backend directory and start the MySQL database:

```bash
cd backend
docker-compose -f compose.yaml up -d
```

### 2. Build the Entire Project

From the root directory:

```bash
mvn clean install
```

This will:
- Build the backend Spring Boot application
- Install Node.js and npm (if not present)
- Install frontend dependencies
- Build the React application

### 3. Run the Backend

```bash
cd backend
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 4. Run the Frontend (Development Mode)

In a new terminal, navigate to the frontend source directory:

```bash
cd frontend/src
npm start
```

The frontend will start on `http://localhost:3000` and will proxy API requests to the backend.

## API Endpoints

The backend provides the following REST API endpoints:

### Books
- `GET /api/books` - Get all books (public)
- `GET /api/books/{id}` - Get book by ID (public)
- `POST /api/books` - Create book (admin only)
- `PUT /api/books/{id}` - Update book (admin only)
- `DELETE /api/books/{id}` - Delete book (admin only)

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

### Cart
- `GET /api/cart` - Get user's cart (authenticated)
- `POST /api/cart/items` - Add item to cart (authenticated)
- `DELETE /api/cart/items/{itemId}` - Remove item from cart (authenticated)

### Orders
- `POST /api/orders/checkout` - Place order (authenticated)
- `GET /api/orders` - Get user's orders (authenticated)
- `GET /api/orders/admin/all` - Get all orders (admin only)

### Customers
- `GET /api/customers/profile` - Get user profile (authenticated)
- `PUT /api/customers/profile` - Update user profile (authenticated)
- `GET /api/admin/customers` - Get all customers (admin only)

## Frontend-Backend Communication

The frontend communicates with the backend through:

1. **Proxy Configuration**: The React development server proxies API requests to `http://localhost:8080`
2. **CORS Configuration**: The backend is configured to accept requests from `http://localhost:3000`
3. **Axios HTTP Client**: Used in the frontend to make API requests

## Development

### Backend Development

The backend is a standard Spring Boot application with:
- Spring Security for authentication
- Spring Data JPA for database operations
- Liquibase for database migrations
- MySQL database

### Frontend Development

The frontend is a React application that:
- Fetches and displays books from the backend API
- Handles loading states and error conditions
- Uses modern React hooks (useState, useEffect)
- Includes responsive CSS styling

## Building for Production

To build both modules for production:

```bash
mvn clean package
```

This will create:
- `backend/target/book-store-backend-1.0.0.jar` - Executable Spring Boot JAR
- `frontend/src/build/` - Production React build

## Testing

Run backend tests:
```bash
cd backend
mvn test
```

## Configuration

### Backend Configuration
- Database settings: `backend/src/main/resources/application.yaml`
- Security settings: `backend/src/main/java/.../security/SecurityConfig.java`

### Frontend Configuration
- API proxy: `frontend/src/package.json` (proxy field)
- Build settings: React Scripts default configuration