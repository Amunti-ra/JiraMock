Jira Backend Mock - Robust REST API that emulate the core of a project management system like Jira, developed with Spring Boot. Built as a personal learning project to improve my backend development skills.

Tech Stack:
Main framework: Spring Boot 3.5.14 (Spring Web, Spring Data JPA)

DB: MariaDB (will switch to PostgreSQL once I switch from local to my home server)

Tools and libraries: Jakarta Validation, DataFaker(for DataSeeder), Swagger(to document and test the API), InteliJ Http client

Enviroment: InteliJ IDEA Ultimate, Maven




Key features:
Deterministic Ticket key generation: Implemented custom business logic in the service layer that automatically generates unique, sequential Ticket keys (e.g. DAM-1, DAM-2) based on the project code and an internal atomic counter.

Realistic data seeding (AI-assisted): Requested to AI a DataSeeder class which was tweaked to fit within the structure of the project, it generates randomized, production-like datasets. This allowed me to find flaws within the logic of the service.

Soft delete implementation: Entity life cycles are managed with logical deletion flags (activo = false) to preserve historical reference integrity across related tables. You know... We deleted your info, wink wink.

DTO architecture and mapping: Isolated the database layer completely from the client interface using custom mapper classes (TODO: learn MapStruct), ensuring secure and decoupled data transfer objects.




Roadmap:
I'm actively developing this project as part of my effort to progress into the real world tools used on the enterprise level of Java software development.

Completed:
[x] Basic CRUD
[x] DTOs, JPA 
[x] Relationships
[x] Mappers, Validation
[x] Soft delete
[x] Derived queries
[x] Basic transactions

Pending and incomplete:
[ ] ControllerAdvice
[ ] Testing, Docker
[ ] Security
[ ] JWT
[ ] CI/CD
