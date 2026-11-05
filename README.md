# TP2 · Persistencia, migraciones y arquitectura hexagonal

Repositorio correspondiente al Trabajo Práctico Nº 2 de la materia Desarrollo de Aplicaciones Web II (Universidad Nacional de Villa Mercedes).

## 🚀 Cómo levantar la base de datos y la aplicación

1. **Base de datos (PostgreSQL):**
   Asegurate de tener Docker instalado y ejecutá el siguiente comando en la raíz del proyecto para levantar la instancia de PostgreSQL:
```bash
   docker-compose up -d
```

2. **Migraciones (Flyway):**
   Las migraciones de la base de datos se ejecutan de manera automática al iniciar la aplicación mediante Flyway.

3. **Ejecutar la aplicación:**
   Podés correr el proyecto desde tu IDE favorito o a través de Maven:
```bash
   mvn spring-boot:run
```

## 📝 Justificaciones Teóricas y Decisiones de Diseño (Consignas del TP)

### 1. Puertos y Adaptadores (Por qué el Service/Controller no se tocaron)

Durante la migración de memoria a base de datos, solo se modificaron las clases de infraestructura (como los repositorios JPA y las Entities) y se creó un nuevo `FavoritoRepositoryAdapter`. Las clases de dominio, el `FavoritoService` y el `FavoritoController` no sufrieron cambios en sus lógicas principales (salvo la adición del campo `listaId` requerido). Esto fue posible gracias a la Arquitectura Hexagonal: nuestro Service depende de un Puerto (la interfaz `FavoritoRepository`) y no de una tecnología específica. Al cambiar la implementación en memoria por PostgreSQL, simplemente conectamos un nuevo Adapter detrás de esa misma interfaz, manteniendo el núcleo completamente desacoplado de la infraestructura.

### 2. Evolución del Esquema (Por qué no se edita V1 o V2)

Para evolucionar el esquema de la base de datos (por ejemplo, hacer que `lista_id` sea obligatorio en los Favoritos antiguos), se creó una nueva migración `V4__lista_id_obligatorio.sql` en lugar de editar las migraciones V1, V2 o V3 ya aplicadas. Esto se debe a que Flyway lleva un registro estricto de las versiones en la tabla `flyway_schema_history`. Si se modificara un archivo ya aplicado, Flyway detectaría un conflicto en el checksum (la firma del archivo) y bloquearía el arranque de la aplicación por seguridad, garantizando de este modo la inmutabilidad y la consistencia del esquema en todos los entornos.

### 3. Transacciones y Propiedades ACID

En el endpoint encargado de mover los favoritos de una lista a otra y eliminar la lista de origen (`POST /api/listas/{origenId}/mover-favoritos`), se utilizó la anotación `@Transactional`. Si se quitara esta anotación y ocurriera un fallo a mitad de camino (por ejemplo, los favoritos se reasignan con éxito a la nueva lista, pero ocurre un error al intentar borrar la lista original), la base de datos quedaría en un estado inconsistente. El uso de transacciones garantiza la Atomicidad (la 'A' de ACID): o se ejecutan todas las operaciones de escritura correctamente, o se revierte todo el bloque (rollback) de forma segura.

## 📸 Evidencias de Pruebas (Swagger)

- Éxito al crear favorito (201 Created):

  ![Crear Favorito](evidencia/captura-201.png)

- Error 409 al borrar lista con favoritos:

  ![Error 409](evidencia/captura-409.png)

- Éxito al mover favoritos y borrar lista (204 No Content):

  ![Mover y Borrar](evidencia/captura-204.png)

  ## 🧪 Cómo correr los tests

Requisito: Docker Desktop abierto (Testcontainers levanta un PostgreSQL real).

```bash
./mvnw test      # solo unitarias (*Test, Surefire)
./mvnw verify    # unitarias + integración (*IT, Failsafe con Testcontainers)
```

Las pruebas de integración corren las migraciones de Flyway (V1 a V6) contra una base limpia en cada ejecución.

## ⚙️ Integración continua (GitHub Actions)

El workflow `.github/workflows/ci.yml` se ejecuta en cada `push`: compila el proyecto y corre `./mvnw verify`.

Cómo leer el resultado: pestaña **Actions** del repositorio → elegir la corrida.

- ✅ Verde: pasaron las pruebas unitarias y de integración.
- ❌ Rojo: abrir el job `build` y el paso con la cruz para ver qué test falló.

Corrida exitosa: [LINK_A_LA_CORRIDA_VERDE](https://github.com/LisandroNR/tp1-springboot-api/actions/runs/41960576098)

## 🚀 Despliegue en Render

URL pública: https://tp1-springboot-api.onrender.com

1. Crear una base PostgreSQL en Render (plan Free).
2. Crear un Web Service desde este repositorio, rama `entrega-tp3`, runtime Docker (usa el `Dockerfile` de la raíz).
3. Cargar las variables de entorno: `DB_URL` (formato `jdbc:postgresql://HOST:5432/BASE`), `DB_USER`, `DB_PASSWORD` y `JWT_SECRET`.
4. Configurar el Health Check Path en `/health`.
5. Verificar en los logs que Flyway aplicó las 6 migraciones y que la app arrancó sin errores.

Verificación: `GET /health` responde `{"status":"ok","service":"demo"}`; `GET /api/listas` devuelve 401 sin token y 200 con un token válido.

## 🔒 Decisiones de Seguridad (Autorización)

La validación para que un usuario solo pueda operar sobre sus propios recursos vive en la capa de **Servicio** (`ListaServiceImpl`), y no en el Controller ni en anotaciones.

**Justificación:** el Controller solo se encarga de rutear las peticiones HTTP; determinar si una lista pertenece al usuario autenticado o si este tiene rol `ADMIN` es lógica de negocio. Si el servicio se invocara desde otro origen distinto de HTTP, la regla no se podría saltear por accidente.