# Architecture

## Layered Architecture

JailQ follows a classic layered architecture adapted for a REST API with a Swing GUI client:

```
Java Swing GUI (desktop client)
        |
        |  HTTP/JSON (java.net.http.HttpClient)
        v
+-------------------+
|    Controller     |  <- Gestiona peticiones y respuestas HTTP
|  (REST endpoints) |     com.example.JailQ.Facade
+--------+----------+
         |
         v
+-------------------+
|     Service       |  <- Logica de negocio y validaciones
|                   |     com.example.JailQ.Service
+--------+----------+
         |
         v
+-------------------+
|      DAO          |  <- Spring Data JPA interfaces
|                   |     com.example.JailQ.Dao
+--------+----------+
         |
         v
+-------------------+
|  Database (MySQL) |  <- Entidades via Hibernate / JPA
|  H2 (tests)       |     com.example.JailQ.Entidades
+-------------------+
```

## Package Structure

| Package | Responsibility |
|---------|---------------|
| `Facade/` | Controladores REST — `CarcelController`, `PresoController`, `CuentaController` |
| `Service/` | Logica de negocio — `CarcelService`, `PresoService`, `CuentaService` |
| `Dao/` | Repositorios JPA — `CarcelDAO`, `PresoDAO`, `CuentaDAO` |
| `Entidades/` | Entidades JPA — `Carcel`, `Preso`, `Cuenta`, `Delito`, `TipoCuenta` |
| `GUI/` | Ventanas Swing — `JailQMainGUI`, `LoginDialog`, y resto |

## Key Design Decisions

**Spring Data JPA** se usa para la capa de persistencia. Los DAOs extienden `JpaRepository`,
proporcionando operaciones CRUD estandar sin codigo repetitivo.

**H2 in-memory database** se activa automaticamente en el perfil de test, por lo que los
tests unitarios e integracion corren sin necesitar MySQL real.

**Singleton pattern** se aplica en `HttpClientSingleton` y `SessionManager` con
double-checked locking y `volatile` para garantizar thread-safety.

**Facade pattern** — los controladores REST actuan como fachada entre la GUI y la logica
de negocio, delegando toda la validacion a los servicios.

## GUI Architecture

```
JailQMainGUI
+-- LoginDialog
+-- GestionCarcelGUI
+-- EstadisticasAvanzadasGUI
+-- GestionPresosGUI
+-- ListadoPresosGUI
+-- FiltrarPresosPorDelitoGUI
+-- GestionCuentasGUI
    +-- EliminarPoliciaGUI
```
