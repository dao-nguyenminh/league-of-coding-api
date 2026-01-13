# ⚔️ League Of Coding

> Real-time 1v1 coding battles with ELO ranking

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)

## 🎯 Vision

**LeetCode meets Chess.com** - Competitive programming duels.

## ✨ Features

- 🔐 **JWT Auth** with refresh tokens & roles
- 📚 **Problem Library** (Admin CRUD + Public browse)
- 🔍 **Search & Filter** with pagination
- 🎲 **Practice Mode** with random problems
- 🏆 **ELO Ranking** system

## 🚀 Quick Start

```bash
# Clone & setup
git clone https://github.com/dao-nguyenminh/league-of-coding-api.git
cd league-of-coding-api
createdb league_of_coding

# Configure app
cat > src/main/resources/application-local.yml << EOF
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/league_of_coding
    username: postgres
    password: your_password
jwt:
  secret: your-256-bit-secret-key-minimum-32-characters
EOF

# Run
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Access:** http://localhost:8080 | **Swagger:** http://localhost:8080/swagger-ui.html

## 📚 API Overview

### 🔓 Public
```
GET    /api/problems              # Browse problems
GET    /api/problems/{slug}       # Problem details
GET    /api/problems/search       # Search problems
GET    /api/problems/random       # Random problems
POST   /api/auth/*               # Auth endpoints
```

### 🔒 Admin
```
POST/PUT/DELETE /api/admin/problems/*  # Problem CRUD
POST          /api/admin/problems/{id}/test-cases
```

## 🛠 Tech Stack

- **Backend:** Java 21, Spring Boot 3.x, Spring Security
- **Database:** PostgreSQL 16 with Flyway migrations
- **API:** RESTful with OpenAPI 3.0 documentation
- **Build:** Maven, Docker support

## 🗺️ Roadmap

### ✅ Phase 1: Foundation (Complete)
- [x] Authentication system
- [x] Problem library
- [x] Admin panel

### 🔄 Phase 2: Battles (Next)
- [ ] Matchmaking (Redis)
- [ ] Real-time battles (WebSocket)
- [ ] Code execution (Judge0)

### 📅 Phase 3: Social
- [ ] ELO calculations
- [ ] Global leaderboards
- [ ] User profiles

## 🧪 Testing

```bash
mvn test                    # Unit tests
mvn verify                 # Integration tests
mvn test jacoco:report     # Coverage
```

## 🏗️ Architecture

```
Controller → Service → Repository → Database
     ↓           ↓           ↓
  Auth      Validation   PostgreSQL
```

---

**🚀 Status:** Active Development | **Version:** 0.2.0 | **Author:** [Dao Nguyen Minh](https://github.com/dao-nguyenminh)