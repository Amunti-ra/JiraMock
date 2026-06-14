A backend-focused project management platform inspired by Jira, built with Spring Boot.
The goal of this project is to practice backend development concepts commonly found in real-world Java applications, including domain modeling, REST API design, data persistence, validation, business logic implementation, security, testing, and deployment workflows.

This project was created after finishing the first year of the DAM degree.
Since the official coursework covered mostly basic CRUD applications, I wanted to explore technologies and development practices commonly used in professional Java backend environments.


### Tech Stack:
- Main framework: Spring Boot 3.5.14 (Spring Web, Spring Data JPA)

- DB: MariaDB (will switch to PostgreSQL once I switch from local to my home server)

- Tools and libraries: Jakarta Validation, DataFaker(for DataSeeder), Swagger(to document and test the API), InteliJ Http client

- Enviroment: InteliJ IDEA Ultimate, Maven




### Key features:
- Deterministic Ticket key generation: Implemented custom business logic in the service layer that automatically generates unique, sequential Ticket keys (e.g. DAM-1, DAM-2) based on the project code and an internal atomic counter.

- Realistic data seeding (AI-assisted): Requested to AI a DataSeeder class which was tweaked to fit within the structure of the project, it generates randomized, production-like datasets. The generated dataset helped uncover flaws in business logic and validate application behavior under more realistic conditions.

- Soft delete implementation: Entities are logically deleted using an active flag instead of being physically removed from the database. This preserves historical references and maintains relational integrity across the application.

- DTO architecture and mapping: Isolated the database layer completely from the client interface using custom mapper classes (TODO: learn MapStruct), ensuring secure and decoupled data transfer objects.




### Roadmap:
I'm actively developing this project as part of my effort to progress into the real world tools used on the enterprise level of Java software development.

## Current progress

### Implemented:
- [x] Basic CRUD
- [x] DTOs
- [x] JPA 
- [x] Relationships
- [x] Mappers
- [x] Validation
- [x] Soft delete
- [x] Derived queries
- [x] Basic transactions
- [x] Custom Exceptions
- [x] ControllerAdvice
- [x] Dynamic Queries (JPA Specifications)
- [x] Unit testing
- [x] Integration testing



### In progress:
- [ ] Docker 

### Planned:
- [ ] Security
- [ ] JWT
- [ ] CI/CD
- [ ] Deployment to a dedicated Linux server


## Project Structure

The project is organized by business modules (package-by-feature) while maintaining a layered architecture internally.

```text
com.newjirasystem.app

├── comentarios
├── config
├── exception
├── proyectos
├── tickets
├── usuarios
└── AppApplication
```
## Project Architecture

#### The application follows a layered architecture:
```text
Client
↓
Controller
↓
DTO Input
↓
Service
↓
Repository
↓
Database

Database
↓
Repository
↓
Entity
↓
Service
↓
DTO Output
↓
Controller
↓
Client
```
