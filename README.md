# clothing-store-management

## English

### Current State
Complete in-memory model (still not database persistence)
* Protected business rules across all entities
* Immutable sales records
* Dynamically calculated debt totals
* A main orchestrator class (`Store`) that manages operations safely without exposing internal data

### Design Choices
* **`Size` interface with category enums** (clothing, footwear, baby items).
* **Dynamically calculated debt values** handled by the orchestrator class instead of stored as attributes, keeping data accurate and avoiding sync issues.
* **Immutable sales and debt records** once created, ensuring data integrity and safety.
* **Defensive list copies** used instead of exposing internal collections directly.
* **Stock validation before processing sales** to ensure transaction atomicity.
* **Custom exceptions** (`InsufficientStockException`, `PaymentExceedsDebtException`, etc.) used instead of generic Java ones.

### Next Steps
* Add SQL/JDBC persistence.
* Migrate the project to Spring Boot.

## Español

### Estado actual

Modelo completo en memoria (aún sin persistencia en base de datos).

* Reglas de negocio protegidas en todas las entidades.
* Registros de ventas inmutables.
* Totales de deuda calculados dinámicamente.
* Una clase orquestadora principal (`Store`) que gestiona las operaciones de forma segura sin exponer datos internos.

### Decisiones de diseño

* **Interfaz `Size` con enums por categoría** (ropa, calzado, artículos para bebé).
* **Valores de deuda calculados dinámicamente** gestionados por la clase orquestadora en lugar de almacenarse como atributos, manteniendo la información exacta y evitando problemas de desincronización.
* **Registros de ventas y deudas inmutables** una vez creados, garantizando la seguridad e integridad de los datos.
* **Uso de copias defensivas de listas** en lugar de exponer directamente las colecciones internas.
* **Validación de stock antes de procesar ventas** para garantizar la atomicidad de las transacciones.
* **Excepciones personalizadas** (`InsufficientStockException`, `PaymentExceedsDebtException`, etc.) en lugar de las genéricas de Java.

## Próximos pasos

* Agregar persistencia con SQL/JDBC.
* Migrar el proyecto a Spring Boot.