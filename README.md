# 🏃‍♂️ PulsePay
**Fitness-to-Crypto Payout Engine**

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)
![Web3j](https://img.shields.io/badge/Web3-Blockchain-3C3C3D?style=for-the-badge&logo=web3.js&logoColor=white)

*Bridging the gap between physical activity and decentralized finance.*

> PulsePay is a stateless backend engine that listens for verified fitness data webhooks (e.g., Strava, Google Fit) and automatically triggers smart contract payouts on the Base/Polygon blockchain networks based on user performance.

## 🏗️ Architecture & Tech Stack

Built with enterprise-grade standards, this RESTful API emphasizes security, maintainability, and test-driven design.

* **Core Framework:** Java 25 & Spring Boot 4.x
* **Database:** PostgreSQL (Production) / H2 (Integration Testing)
* **Security:** Stateless Spring Security with custom JWT implementation
* **Web3:** Web3j for Polygon/Base smart contract interaction
* **CI/CD:** GitHub Actions pipeline verifying compilation and tests on every push

## 💡 Key Technical Highlights

* **N-Tier Architecture:** Clean separation of concerns between Controllers (Web), Services (Business Logic with `@Transactional` boundaries), and Repositories (Data Access).
* **Data Transfer Objects (DTOs):** API boundaries are strictly protected using Java Records, ensuring database entities never leak to the client.
* **Test-Driven Design (TDD):** Business logic is validated using the AAA (Arrange, Act, Assert) pattern with Mockito, while repository queries are verified via `@DataJpaTest` slice testing.
* **Robust Exception Handling:** A global `@RestControllerAdvice` translates underlying Java exceptions into standardized, client-friendly HTTP 400/500 JSON responses.

## 🗺️ Project Roadmap

### Phase 1: Environment & Foundation ✅
- [x] PostgreSQL integration, core entities, and Spring Data JPA repositories.

### Phase 2: Core N-Tier API ✅
- [x] Service layer logic, Controllers, DTOs, and global exception handling.

### Phase 3: Security & Validation 🚧 (Current)
- [x] Complete automated testing suite and CI pipeline integration.
- [x] Custom stateless JWT authentication filter and Spring Security configuration.
- [x] Authentication API (`/login` and `/register`).

### Phase 4: Web3 & External Integrations 🚀 (Upcoming)
- [ ] Implement Webhook endpoints for fitness provider data ingestion.
- [ ] Integrate `Web3j` to securely sign and execute crypto payouts on EVM chains.
