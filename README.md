# ⚔️ League Of Coding

> Competitive 1v1 coding battle platform - Real-time programming duels with ELO ranking

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)

## 🎯 About

Platform where developers compete in **1v1 real-time coding battles**, solving algorithmic challenges to climb the
global leaderboard with an ELO-based ranking system.

**Vision:** LeetCode meets Chess.com - competitive programming with real-time matchmaking.

## ✨ Current Features

- **JWT Authentication** with refresh token rotation
- **User Management** (registration, login, profile)
- **Security** - BCrypt hashing, token expiration (15min/7days)
- **RESTful API** with OpenAPI 3.0 documentation
- **Database Migrations** with Flyway
- **Error Handling** - RFC 7807 ProblemDetail responses

## 🛠 Tech Stack

**Backend:**

- Java 21
- Spring Boot 3.x
- Spring Security 6.2
- Spring Data JPA
- Flyway

**Database:**

- PostgreSQL 16

**Libraries:**

- JJWT 0.12.6 (JWT handling)
- Lombok (boilerplate reduction)
- SpringDoc OpenAPI (API documentation)

**Build:**

- Maven

## 🚀 Getting Started

### Prerequisites

- Java 21+
- PostgreSQL 16+
- Maven 3.8+

### Installation

1. **Clone repository**

```bash
git clone https://github.com/dao-nguyenminh/league-of-coding-api.git
cd league-of-coding-api
```

2. **Create database**

```bash
createdb league_of_coding
```

3. **Configure application**

Create `src/main/resources/application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/league_of_coding
    username: postgres
    password: your_password

jwt:
  secret: your-256-bit-secret-key-minimum-32-characters
  access-token-expiration: 900000    # 15 minutes
  refresh-token-expiration: 604800000 # 7 days
```

4. **Run application**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

5. **Access**

- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/v3/api-docs

## 📁 Project Structure

```
src/main/java/com/leagueofcoding/api/
├── config/            # Application configuration (OpenAPI, etc.)
├── controller/        # REST API endpoints
├── dto/               # Data Transfer Objects (Java Records)
├── entity/            # JPA entities
├── exception/         # Custom exceptions & global handler
├── repository/        # Spring Data JPA repositories
├── security/          # Security configuration & JWT
│   └── jwt/           # JWT token provider
└── service/           # Business logic layer

src/main/resources/
├── db/migration/      # Flyway database migrations
│   ├── V1__create_users_table.sql
│   └── V2__create_refresh_tokens_table.sql
└── application*.yml   # Configuration files
```

**Architecture:** Package-by-layer (will evolve to modular as features grow)

## 📊 Database Schema

**Current Tables:**

```sql
users
├── id (PK)
├── username (UNIQUE)
├── email (UNIQUE)
├── password (BCrypt hash)
├── rating (default: 1000)
├── total_matches, wins, losses
└── created_at

refresh_tokens
├── id (PK)
├── token (UNIQUE, UUID)
├── user_id (FK → users.id)
├── expiry_date
├── revoked (boolean)
└── created_at
```

## 📚 API Endpoints

### Authentication

| Method | Endpoint             | Description                | Auth |
|--------|----------------------|----------------------------|------|
| POST   | `/api/auth/register` | Register new user          | ❌    |
| POST   | `/api/auth/login`    | Login user                 | ❌    |
| POST   | `/api/auth/refresh`  | Refresh access token       | ❌    |
| POST   | `/api/auth/logout`   | Logout user (revoke token) | ❌    |
| GET    | `/api/auth/me`       | Get current user info      | ✅    |

**Example Request:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "coderking",
    "password": "password123"
  }'
```

**Response:**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "username": "coderking",
    "email": "king@example.com",
    "rating": 1000,
    "totalMatches": 0,
    "wins": 0,
    "losses": 0,
    "createdAt": "2025-01-12T10:30:00"
  }
}
```

## 🔐 Security

- **Stateless Authentication** - JWT tokens (no server-side sessions)
- **Token Types:**
    - Access Token: 15 minutes (short-lived)
    - Refresh Token: 7 days (long-lived)
- **Token Rotation** - Old refresh tokens revoked on refresh
- **Password Hashing** - BCrypt (strength 10)
- **One Token Per User** - Previous refresh tokens auto-revoked
- **Error Responses** - RFC 7807 ProblemDetail format

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Integration tests
mvn verify
```

**Manual Testing:** Use Swagger UI at http://localhost:8080/swagger-ui.html

## 📦 Build & Deploy

**Build JAR:**

```bash
mvn clean package
java -jar target/league-of-coding-api-*.jar
```

**Docker (optional):**

```bash
docker build -t league-of-coding-api .
docker run -p 8080:8080 league-of-coding-api
```

## 🗺 Roadmap

### ✅ Phase 1: Foundation (Completed)

- [x] User authentication (JWT + refresh tokens)
- [x] User management
- [x] Database migrations
- [x] API documentation

### 📅 Phase 2: Security & Quality (Next)

- [ ] Rate limiting (Bucket4j)
- [ ] Input validation improvements
- [ ] Comprehensive testing (unit + integration)
- [ ] CI/CD pipeline

### 📅 Phase 3: Matchmaking

- [ ] Matchmaking queue (Redis)
- [ ] ELO-based pairing
- [ ] Real-time notifications (WebSocket)

### 📅 Phase 4: Battle System

- [ ] Problem library
- [ ] Real-time battle engine
- [ ] Code execution integration (Judge0 API)
- [ ] Winner determination

### 📅 Phase 5: Ranking & Social

- [ ] ELO rating calculations
- [ ] Global leaderboard
- [ ] Match history
- [ ] User profiles & stats

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 👨‍💻 Author

**Dao Nguyen Minh**

- GitHub: [@dao-nguyenminh](https://github.com/dao-nguyenminh)
- Repository: [league-of-coding-api](https://github.com/dao-nguyenminh/league-of-coding-api)

## 📞 Support

For questions or support, please open an issue in the GitHub repository.

---

**Status:** 🚧 Active Development  
**Version:** 0.1.0  
**Last Updated:** January 2026

Built with ❤️ using Spring Boot & Java 21