# E-Commerce Spring Boot Application

A complete, modern e-commerce web application built with Spring Boot, Thymeleaf, and Bootstrap 5 following SOLID principles and clean architecture.

## 🚀 Features

### Core Functionality
- **User Management**: Registration, login, profile management, role-based access
- **Product Catalog**: Browse, search, filter products with pagination
- **Shopping Cart**: Add/remove items, quantity management, checkout
- **Order Management**: Order history, tracking, status updates
- **Admin Dashboard**: Product management, order management, user management
- **Security**: Spring Security with BCrypt, role-based authorization

### Technical Stack
- **Backend**: Java 17, Spring Boot 3.2, Spring Data JPA, Spring Security
- **Frontend**: Thymeleaf, Bootstrap 5, HTML5, CSS3, JavaScript
- **Database**: MySQL 8.0+ with JPA/Hibernate
- **Build**: Maven with comprehensive dependencies
- **Architecture**: Layered (Controller → Service → Repository → Entity)

## 📋 Prerequisites

### Software Requirements
- **Java Development Kit (JDK)**: Version 17 or higher
- **Maven**: Version 3.6.0 or higher
- **MySQL**: Version 8.0 or higher
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

### Database Setup
- Create MySQL database named `ecommerce_db`
- Ensure UTF-8 character set and proper collation
- User with appropriate privileges for the application

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd LegendFoot
```

### 2. Database Configuration
```sql
-- Create database
CREATE DATABASE IF NOT EXISTS ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user and grant privileges
CREATE USER IF NOT EXISTS 'ecommerce_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ecommerce_db.* TO 'ecommerce_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Application Configuration
Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=ecommerce_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.format_sql=true

# Server Configuration
server.port=8080
server.servlet.context-path=/

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```

### 4. Build and Run
```bash
# Clean and compile
mvn clean compile

# Run the application
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🏗️ Project Structure

```
LegendFoot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ecommerce/app/
│   │   │       ├── controller/          # Web layer
│   │   │       ├── service/            # Business logic layer
│   │   │       ├── repository/         # Data access layer
│   │   │       ├── entity/             # JPA entities
│   │   │       ├── dto/                # Data transfer objects
│   │   │       ├── security/           # Security configuration
│   │   │       ├── exception/          # Custom exceptions
│   │   │       └── util/               # Utility classes
│   │   └── resources/
│   │       ├── templates/           # Thymeleaf templates
│   │       │   ├── fragments/     # Reusable components
│   │       │   ├── products/       # Product pages
│   │       │   ├── cart/           # Cart pages
│   │       │   ├── orders/         # Order pages
│   │       │   ├── users/           # User pages
│   │       │   ├── admin/          # Admin pages
│   │       │   ├── error/          # Error pages
│   │       │   ├── layout.html     # Main layout
│   │       │   └── home.html       # Home page
│   │       ├── static/              # Static resources
│   │       │   ├── css/           # Stylesheets
│   │       │   ├── js/            # JavaScript files
│   │       │   └── images/        # Product images
│   │       ├── application.properties   # Application configuration
│   │       └── schema.sql           # Database schema
├── pom.xml                    # Maven configuration
└── README.md                  # This file
```

## 🔧 Configuration

### Database Schema
The application uses the following main tables:
- **users**: User accounts with roles and authentication
- **products**: Product catalog with inventory management
- **carts**: Shopping carts for user sessions
- **cart_items**: Individual items in shopping carts
- **orders**: Customer orders with status tracking
- **order_items**: Products within each order

### Security Configuration
- **Authentication**: Form-based login with BCrypt password encoding
- **Authorization**: Role-based access (USER, ADMIN roles)
- **Session Management**: Secure session handling
- **CSRF Protection**: Enabled for web forms

## 🎯 Key Features

### User Experience
- **Responsive Design**: Mobile-friendly interface using Bootstrap 5
- **Search Functionality**: Product search with filtering
- **Pagination**: Efficient data loading for large datasets
- **Flash Messages**: User feedback for all actions
- **Error Handling**: Comprehensive error pages and messages
- **Accessibility**: Semantic HTML and ARIA labels

### Admin Features
- **Dashboard**: System overview and statistics
- **Product Management**: CRUD operations with validation
- **Order Management**: Status updates and tracking
- **User Management**: Account administration
- **Reports**: Analytics and business insights

## 🚀 Running the Application

### Development
```bash
mvn spring-boot:run
```

### Production
```bash
# Build JAR file
mvn clean package

# Run with production profile
java -jar target/ecommerce-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Access Points
- **Application**: http://localhost:8080
- **Admin Dashboard**: http://localhost:8080/admin/dashboard
- **API Documentation**: http://localhost:8080/swagger-ui.html (if configured)

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
The application includes comprehensive test coverage for:
- Unit tests for service layer
- Integration tests for repositories
- Controller tests for web layer
- Security tests for authentication

## 🔒 Security Considerations

### Implemented Security Measures
- **Password Encryption**: BCrypt for secure password storage
- **SQL Injection Prevention**: JPA/Hibernate parameter binding
- **XSS Protection**: Thymeleaf auto-escaping
- **CSRF Protection**: Spring Security CSRF tokens
- **Session Security**: Secure session management
- **Role-Based Access**: Method-level security annotations

### Recommended Production Settings
- Use HTTPS in production environment
- Configure proper CORS policies
- Enable security headers
- Regular security updates
- Database connection pooling
- Proper logging configuration

## 📊 Performance Optimization

### Database Optimization
- Proper indexing on frequently queried columns
- Connection pooling configuration
- Lazy loading for entity relationships
- Pagination for large datasets
- Caching strategies for static data

### Application Performance
- Static resource optimization
- Image compression and CDN usage
- Minified CSS/JS in production
- Proper HTTP caching headers
- Database query optimization

## 🐛 Troubleshooting

### Common Issues
1. **Database Connection Errors**
   - Check MySQL service is running
   - Verify connection string and credentials
   - Ensure database exists and user has privileges

2. **Build Failures**
   - Clear Maven cache: `mvn clean`
   - Check Java version compatibility
   - Verify dependency versions

3. **Security Issues**
   - Clear browser cache and cookies
   - Check security configuration
   - Verify role assignments

### Logging Configuration
```properties
# Enable detailed logging
logging.level.com.ecommerce.app=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

## 🤝 Contributing

### Development Guidelines
1. Follow SOLID principles in all code
2. Write comprehensive unit tests
3. Update documentation for new features
4. Use meaningful commit messages
5. Follow Git workflow for collaboration

### Code Style
- Use consistent naming conventions
- Add proper JavaDoc comments
- Follow Spring Boot best practices
- Implement proper error handling
- Use dependency injection consistently

## 📄 API Documentation

### Controller Endpoints

#### User Management
- `GET /users/register` - Registration form
- `POST /users/register` - Process registration
- `GET /users/profile` - User profile
- `POST /users/profile/edit` - Update profile
- `GET /users/change-password` - Change password form
- `POST /users/change-password` - Process password change

#### Product Management
- `GET /products` - Product listing with search and pagination
- `GET /products/{id}` - Product details
- `GET /products/new` - Product creation form (Admin)
- `POST /products/new` - Create product (Admin)
- `GET /products/{id}/edit` - Product edit form (Admin)
- `POST /products/{id}/edit` - Update product (Admin)
- `POST /products/{id}/delete` - Delete product (Admin)

#### Cart Management
- `GET /cart` - View shopping cart
- `POST /cart/add` - Add product to cart
- `POST /cart/update` - Update cart item quantity
- `POST /cart/remove/{productId}` - Remove item from cart
- `POST /cart/clear` - Clear entire cart
- `GET /cart/checkout` - Checkout form
- `POST /cart/checkout` - Process checkout

#### Order Management
- `GET /orders` - Order history
- `GET /orders/{id}` - Order details
- `POST /orders/{id}/cancel` - Cancel order
- `GET /orders/admin` - Admin order listing
- `POST /orders/admin/{id}/status` - Update order status (Admin)

## 📝 License

This project is licensed under the MIT License. See LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check existing issues for solutions
- Review documentation and API endpoints
- Follow troubleshooting guide above

---

**🎉 Congratulations!** You now have a complete, production-ready e-commerce application built with modern technologies and best practices.
