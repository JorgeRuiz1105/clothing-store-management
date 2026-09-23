# clothing-store-management

## English

Java application built for learning clean architecture principles and multi-layer
design (Service-Repository). It manages small-scale inventory and credit
accounts using transactional SQLite persistence via JDBC.

### Current State (Version 3.0)

* **Layered Architecture:** refactoring from monolithic orchestrator (`Store`) into a modular, multi-layer architecture (Model, Repository, Service).
* Constructor overloading across domain entities.
* **Relational Persistence (SQLite & JDBC):** Transitioned from in-memory storage to a persistent SQLite database (`store.db`).
* ACID transaction management.

### Design Choices
* **Constructor Injection:** all services and repositories enforce immutability via constructor injection.
* **Strict Transaction Limits:** validation of outstanding debts before accepting payments and fail-fast verifications.
* **Polymorphic Mapping:** Implemented a Factory Pattern in `ArticleCategories`
to instantiate concrete `Size` objects dynamically.


### Next Steps
* Migrate the project to Spring Boot.

## Español

Aplicación en Java desarrollada con fines de aprendizaje para poner en práctica
principios de arquitectura y diseño en capas (Servicio-Repositorio). Gestiona inventario
y cartera a pequeña escala mediante persistencia transaccional en SQLite con JDBC.

### Estado actual (Versión 3.0)

* **Arquitectura por capas:** refactorización completa desde la clase orquestadora monolítica (`Store`) hacia una arquitectura modular en capas (Modelo, Repositorio, Servicio).
* Sobrecarga de constructores en las entidades.
* **Persistencia Relacional (SQLite y JDBC):** Transición completa desde repositorios en memoria hacia una base de datos SQLite (`store.db`).
* Manejo transaccional ACID.


### Decisiones de diseño

* **Inyección por constructor:** todos los servicios y repositorios garantizan inmutabilidad y desacoplamiento.
* **Control Transaccional:** validación de saldos pendientes antes de registrar pagos y verificaciones fail-fast de inventario antes de procesar ventas.
* **Mapeo Polimórfico:** Implementación de patrón _Factory_ en `ArticleCategories` para instanciar `Size` a partir de los datos persistidos en texto.

## Próximos pasos

* Migrar el proyecto a Spring Boot.