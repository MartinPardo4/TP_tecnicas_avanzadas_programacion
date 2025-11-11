## TP de Técnicas avanzadas de programación - Martin Pardo

Aplicación REST para gestionar el circuito anual de revisión de vehículos entre dueños y mecánicos. Permite registrar usuarios, administrar la flota, solicitar turnos en un taller y cargar los resultados de las revisiones, con seguridad basada en JWT.

### Arquitectura y tecnologías
- **Backend**: Spring Boot 3.5, Spring Web, Spring Data JPA, Spring Security.
- **Base de datos**: MySQL 8 (Hibernate `ddl-auto=update`).
- **Seguridad**: JWT + filtros personalizados, contraseñas hasheadas con BCrypt.
- **Contenedores**: Dockerfile multi-stage + `docker-compose.yml` (backend + MySQL).

### Requisitos previos
- JDK 21+ y Maven Wrapper incluido (`./mvnw`).
- Docker Engine + Docker Compose (opcional para despliegue).
- MySQL disponible localmente o a través del compose.

### Puesta en marcha
#### Con Maven (perfil local)
1. Configurá credenciales en `src/main/resources/application.properties`.
2. Ejecutá `./mvnw spring-boot:run`.
3. La API queda disponible en `http://localhost:8080`.

#### Con Docker Compose
1. Opcional: creá un archivo `.env` junto a `docker-compose.yml` para sobreescribir `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, etc.
2. Construí y levantá todo: `docker compose up --build`.
3. El backend publica en `http://localhost:8080` y MySQL en `localhost:3306`.
4. Para reiniciar desde cero (incluyendo datos), ejecutá `docker compose down -v`.

### Seguridad y roles
- Autenticación por `Authorization: Bearer <token>`.
- Roles soportados:
  - `DUENO`: registra vehículos, solicita turnos, consulta resultados.
  - `MECANICO`: confirma turnos y registra revisiones.
- El manejador global `GlobalExceptionHandler` devuelve errores JSON consistentes (`status`, `error`, `message`, `path`, `timestamp`, `errors`).

### Endpoints principales

| Recurso | Método y ruta | Rol | Descripción |
| --- | --- | --- | --- |
| Auth | `POST /auth/register` | Público | Registro de usuario (`nombre`, `email`, `password`, `rol`). |
| Auth | `POST /auth/login` | Público | Devuelve JWT + datos básicos si las credenciales son válidas. |
| Vehículos | `POST /vehiculos` | `DUENO` | Alta de vehículo (`patente`, `marca`, `modelo`, `anio`, `kilometraje`). |
| Vehículos | `GET /vehiculos` | `DUENO` | Lista vehículos del dueño autenticado. |
| Turnos | `POST /turnos` | `DUENO` | Solicita turno para un vehículo propio (`vehiculoId`, `fechaTurno`, `observaciones`). |
| Turnos | `GET /turnos` | `DUENO` | Lista turnos del dueño autenticado. |
| Turnos | `GET /turnos/asignados` | `MECANICO` | Lista turnos asignados al mecánico autenticado. |
| Turnos | `PATCH /turnos/{id}/confirmar` | `MECANICO` | Confirma un turno pendiente y se autoasigna. |
| Revisiones | `POST /turnos/{id}/revision` | `MECANICO` | Carga evaluación del turno (8 puntajes + comentario opcional). |
| Revisiones | `GET /turnos/{id}/resultado` | `DUENO`/`MECANICO` | Consulta el resultado si pertenece al turno propio o asignado. |

### Validaciones destacadas
- DTOs con `jakarta.validation`:
  - `TurnoRequest`: fecha futura y observaciones ≤ 1000 caracteres.
  - `VehiculoRequest`: patente y datos obligatorios; año ≥ 1900.
  - `RevisionRequest`: exactamente 8 puntajes entre 1 y 10.
- Reglas de dominio:
  - El dueño solo puede operar con vehículos propios.
  - Solo mecánicos activos confirman turnos y cargan revisiones.
  - Evita duplicados (email, patente) y turnos confirmados dos veces.

### Modelo de datos (resumen)
- `Usuario`: id, nombre, email, password, rol (`DUENO`/`MECANICO`), activo.
- `Vehiculo`: referencia al dueño (`Usuario`), datos técnicos, patente única.
- `Turno`: fecha, asociado a vehículo, mecánico asignado, flag `confirmado`.
- `ResultadoRevision`: puntajes (lista), total, calificación (`SEGURO`/`RECHEQUEAR`), comentario del mecánico.

### Pruebas
```
./mvnw test
```
Incluye tests unitarios para `UsuarioServiceImpl`, `TurnoServiceImpl`, `RevisionServiceImpl` y un flujo de integración sobre H2.

### Limpieza de datos
- Con Docker: `docker compose down -v` elimina contenedores y volumen `mysql_data`.
- Manual: `docker exec -it mysql-ta mysql -uapp_user -p` y ejecutá `DROP DATABASE vehiculos_db; CREATE DATABASE vehiculos_db;`.

### Recursos adicionales
- Documentación Spring Boot y módulos relacionados (ver `HELP.md`).
