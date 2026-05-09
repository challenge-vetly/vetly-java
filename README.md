# Vetly — Backend API (Java)

Vetly is a veterinary management platform that connects tutors, veterinarians, and clinics through a unified digital experience. This repository contains the backend service built with Java and Spring Boot, responsible for the domain model and data persistence layer.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Domain Model](#domain-model)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Roadmap](#roadmap)

---

## Overview

The Vetly backend implements the core domain entities for the platform's first operational phases:

- **Veterinarian registration** with CRMV, specialties, species, availability schedule, and active/inactive status
- **Tutor registration** with CPF, email, and future pet relationship
- **Animal (Pet) registration** linked to a species, breed, age, and medical record
- **Medical Record (Prontuário)** as the longitudinal clinical history of the animal
- **Clinical Evolution (Evolução Clínica)** recording physician annotations per consultation
- **Consultation (Consulta)** linking an animal, a veterinarian, a date/time, and a resulting clinical evolution
- **Availability (Disponibilidade)** defining a veterinarian's working days and hours

The project is in its early structural phase — entities and relationships are modelled; controllers, services, and repositories are not yet implemented.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Persistence | Spring Data JPA (Hibernate) |
| Database | Oracle Database (ojdbc11) |
| Build Tool | Gradle |
| Testing | JUnit 5 via Spring Boot Test |

---

## Project Structure

```
vetly-java/
├── build.gradle
├── settings.gradle
└── src/
    └── main/
        ├── java/com/petly/petly_java/
        │   ├── PetlyJavaApplication.java       # Application entry point
        │   └── model/
        │       ├── Animal.java                 # Pet entity
        │       ├── Consulta.java               # Consultation entity
        │       ├── Disponibilidade.java         # Veterinarian schedule slot
        │       ├── Especialidade.java           # Specialty enum (22 options)
        │       ├── Especie.java                 # Species enum (7 options)
        │       ├── EvolucaoClinica.java         # Clinical evolution/notes
        │       ├── Prontuario.java              # Medical record
        │       ├── Tutor.java                   # Pet owner entity
        │       ├── UF.java                      # Brazilian states enum
        │       └── Veterinario.java             # Veterinarian entity
        └── resources/
            └── application.properties
```

---

## Domain Model

### Entities

#### `Veterinario`
Represents a veterinary professional registered on the platform.

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `nome` | String | Full name |
| `crmv` | String | Unique professional registration number |
| `cpf` | String | Unique taxpayer ID |
| `email` | String | Unique email address |
| `telefone` | String | Phone number |
| `isAtivo` | boolean | Active/inactive status flag |
| `ufAtuacao` | UF (enum) | Brazilian state of practice |
| `especialidades` | List\<Especialidade\> | List of medical specialties |
| `especiesAtendidas` | List\<Especie\> | List of species treated |
| `disponibilidades` | List\<Disponibilidade\> | Schedule slots |

#### `Tutor`
Represents the pet owner.

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `nome` | String | Full name |
| `cpf` | String | Unique taxpayer ID |
| `email` | String | Email address |

> ⚠️ **TODO:** Relationship between `Tutor` and their animals is not yet implemented (noted in source code).

#### `Animal`
Represents a pet registered on the platform.

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `nome` | String | Pet name |
| `especie` | Especie (enum) | Species |
| `raca` | String | Breed |
| `idade` | int | Age |
| `prontuario` | Prontuario | One-to-one medical record |

#### `Prontuario`
The longitudinal medical record of an animal. Contains the full history of clinical evolutions across all consultations.

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `animal` | Animal | Associated animal (one-to-one) |
| `evolucoes` | List\<EvolucaoClinica\> | All clinical evolution entries |

#### `EvolucaoClinica`
Clinical notes produced by the veterinarian during or after a consultation.

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `prontuario` | Prontuario | The animal's medical record this entry belongs to |
| `consulta` | Consulta | The consultation that generated this entry (one-to-one) |
| `anotacoes` | String (up to 10,000 chars) | Veterinarian's clinical notes |

#### `Consulta`
Represents a scheduled or completed appointment between a vet and an animal.

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `animal` | Animal | The animal being attended |
| `veterinario` | Veterinario | The attending veterinarian |
| `dataHora` | LocalDateTime | Date and time of the consultation |
| `evolucaoClinica` | EvolucaoClinica | Clinical evolution generated by this consultation |

#### `Disponibilidade`
Represents a time slot in a veterinarian's weekly schedule.

| Field | Type | Description |
|---|---|---|
| `id` | UUID | Unique identifier |
| `diaSemana` | DayOfWeek | Day of the week |
| `inicio` | LocalTime | Start time |
| `fim` | LocalTime | End time |

---

### Enums

#### `Especialidade` — 22 veterinary specialties

#### `Especie` — 7 species categories

#### `UF` — All 27 Brazilian states/DF

---

### Entity Relationship Summary

```
Tutor
  └── (TODO) → Animal (1:N)

Animal
  └── Prontuario (1:1)
        └── EvolucaoClinica (1:N)
              └── Consulta (1:1)
                    ├── Animal (N:1)
                    └── Veterinario (N:1)

Veterinario
  └── Disponibilidade (1:N)
```

---

## Getting Started

### Prerequisites

- Java 21+
- Gradle 8+
- Oracle Database instance (or compatible JDBC connection)

### Running the application

```bash
# Clone the repository
git clone https://github.com/your-org/vetly-java.git
cd vetly-java

# Build
./gradlew build

# Run
./gradlew bootRun
```

### Running tests

```bash
./gradlew test
```

---

## Configuration

The `application.properties` file currently only defines the application name. Database connection properties must be added before running:

```properties
spring.application.name=petly-java

# Add your Oracle DB connection:
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Roadmap

The following features from the Vetly product specification are **not yet implemented** in this codebase and represent the next development phases:

**Data Layer**
- [ ] Repository interfaces (`JpaRepository`) for all entities
- [ ] `Tutor` ↔ `Animal` relationship
- [ ] Financial data model (payments, split rules, subscriptions)

**Application Layer**
- [ ] Service classes with business logic
- [ ] CRMV validation service (integration with regional veterinary council)
- [ ] Scheduling and availability conflict resolution

**API Layer**
- [ ] REST controllers for all entities
- [ ] DTOs and request/response mapping
- [ ] Authentication and role-based access control (Veterinário Autônomo, Admin, Veterinário Vinculado)

**Integrations**
- [ ] Abacate Pay — payment processing and financial split
- [ ] WhatsApp — proactive communications and conversational flows
- [ ] AI assistant — diagnostic suggestions and protocol generation

**Infrastructure**
- [ ] LGPD consent tracking
- [ ] Document generation (prontuário, receita, atestado, nota fiscal)
- [ ] Audit logging for medical record corrections