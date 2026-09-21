# clothing-store-management

## English

### Current State (Version 2.0)
Complete in-memory model (still not database persistence)
* **Layered Architecture:** refactoring from monolithic orchestrator (`Store`) into a modular, multi-layer architecture (Model, Repository, Service).
* Constructor overloading across domain entities.

### Design Choices
* **Constructor Injection**: all services and repositories enforce immutability via constructor injection.
* **Strict Transaction Limits**: validation of outstanding debts before accepting payments and fail-fast verifications.

### Next Steps
* Add SQL/JDBC persistence.
* Migrate the project to Spring Boot.

## Español

### Estado actual (Versión 2.0)

Modelo completo en memoria (aún sin persistencia en base de datos).

* **Arquitectura por capas**: refactorización completa desde la clase orquestadora monolítica (`Store`) hacia una arquitectura modular en capas (Modelo, Repositorio, Servicio).
* Sobrecarga de constructores en las entidades.


### Decisiones de diseño

* **Inyección por constructor**: todos los servicios y repositorios garantizan inmutabilidad y desacoplamiento.
* **Control Transaccional**: validación de saldos pendientes antes de registrar pagos y verificaciones fail-fast de inventario antes de procesar ventas.

## Próximos pasos

* Agregar persistencia con SQL/JDBC.
* Migrar el proyecto a Spring Boot.