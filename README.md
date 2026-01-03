<hr>

# 🌐 Aura API

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/Security-JWT-black?style=for-the-badge&logo=json-web-tokens&logoColor=white)

Robust and scalable backend for IoT (Internet of Things) device tracking and management. The system employs a **Modular Monolith** architecture focused on Clean Code, SOLID principles, and Domain-Driven Design (DDD), seamlessly integrating with LoRaWAN networks (Everynet) and MQTT telemetry ingestion.

<hr>

## 🚀 About the Project

* **Aura API** acts as the central intelligence of the Aura tracking ecosystem. It manages the entire lifecycle of IoT devices, user permissions, multi-tenant organizations, and real-time geolocation data processing.

* The system is designed to be **Multi-tenant** and implements defensive business rules to ensure data integrity across shared resources, allowing different users to track devices securely.

<hr>

## 🏗 Architecture

The project follows the **Modular Monolith** pattern (Package by Feature), ensuring high cohesion and low coupling. Unlike traditional layered architectures, here each business feature is an independent universe with its own internal layers.

#### 📂 Folder Structure
```text
src/main/java/br/rafaeros/aura/
├── core/                           # Shared Kernel
│   ├── config/                     # Global Configuration
│   ├── security/                   # Centralized Security
│   ├── exception/                  # Global Error Handling
│   └── seeder/                     # Data Seeding
│
└── modules/                        # Business Features (Domains)
    ├── company/                    # Company Management & Integration Settings
    ├── device/                     # Device Inventory, Tags, and Positions
    │   ├── client/                 # Everynet Integration (Isolated within module)
    │   ├── model/                  # Aggregates (Device, Feature, Position)
    │   └── ...
    ├── telemetry/                  # High-volume Telemetry & MQTT Log Ingestion
    └── user/                       # Identity, Authentication, and RBAC
```

<hr>

## ✨ Key Features
### 🔐 Advanced Security & RBAC

- Stateless Authentication: Custom JWT Filters and Providers.

- Role-Based Access Control: Distinct profiles for ADMIN, OWNER, and USER.

- Granular Authorization: Custom SpEL (Spring Expression Language) security to protect specific resources (e.g., @PreAuthorize("@userSecurity.canManageDevice(...)")).

- Standardized Errors: Unified JSON responses for 401/403 and business exceptions.

- 📡 Intelligent Device Management
Smart Link/Unlink: Devices are treated as global entities. Users only "link" or "unlink" devices to their accounts. This approach preserves historical data in the database even if a user removes the device from their view.

- Everynet Integration: Automatic synchronization of metadata, tags, and geolocation via external API.

- Dynamic Features: Flexible configuration of device capabilities.

- 🏢 Corporate Management (Multi-tenant)
Automatic association of users to companies.

- Company-specific integration settings and API keys.

<hr>

## 🚀 How to Run

Prerequisites
- Java JDK 21+
- Maven
- PostgreSQL running

1. Environment Setup
Configure the environment variables in your system or update application.properties:

```bash
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/aura_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# Initial Seeder (First Admin)
admin.username=your_admin_user
admin.password=your_admin_password
admin.company.name=your_company_name
admin.company.cnpj=your_company_cnpj
admin.company.cep=your_company_cep
admin.company.address=your_company_address
```

2. Installation & Execution
```bash
# Clone the repository
git clone [https://github.com/rafaeros/aura-api.git](https://github.com/rafaeros/aura-api.git)

# Navigate to the project directory
cd aura-api

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

