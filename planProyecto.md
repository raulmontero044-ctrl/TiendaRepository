# Plan de Proyecto: Tienda (es.cesguiro.shop)

## 1. Objetivo

Desarrollar una aplicación de tienda con arquitectura limpia (Ports & Adapters), siguiendo los principios de inversión de dependencias. El proyecto debe cumplir con:

- **Arquitectura limpia**: Separación entre `controller`, `domain` y `persistence`. `controller` y `persistence` dependen de `domain`. `domain` debe ser puro Java (sin Spring, sin JPA).
- **Dominio anémico**: Las entidades de dominio solo contienen campos, constructores, getters y setters, sin lógica de negocio.
- **Inyección de dependencias**: Mediante constructor injection, sin `@Autowired` en campos. Ensamblado en `BeanConfig`.
- **Puerto/Adaptador**: `ProductDao` (interfaz/puerto) en `domain`, implementado por `ProductDaoJdbc` en `persistence`.
- **ProductService puro**: Inyecta `ProductDao` por constructor, sin anotaciones Spring (`@Service`, `@Component`, `@Repository`), y devuelve el catálogo (`List<Product>`).
- **Endpoints principales**: `/api/productos` y `/api/catalogos`.
- **Borrado físico**: Mediante `DELETE` en los endpoints.
- **Jakarta EE**: Uso exclusivo de `jakarta.*`, prohibido `javax.*`.
- **Contract-First**: OpenAPI con `openapi-generator-maven-plugin` para generar interfaces y modelos.
- **Validaciones**: Bean Validation con Jakarta en los DTOs generados desde OpenAPI.
- **Base de datos**: MariaDB con Docker Compose, esquema creado por `db/init.sql`.
- **Documentación**: Swagger UI con Springdoc OpenAPI.
- **Arquitectura validada**: ArchUnit para garantizar las reglas arquitectónicas.

## 2. Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 25 | Lenguaje base |
| Spring Boot | 3.5.x | Framework |
| Maven | - | Gestión de dependencias y build |
| MariaDB | 11.4 | Base de datos |
| Docker Compose | - | Orquestación de contenedores |
| JdbcTemplate | - | Acceso a datos (sin JPA/Hibernate) |
| Springdoc OpenAPI | 2.8.9 | Swagger UI |
| OpenAPI Generator Maven Plugin | 7.10.0 | Contract-first (generación de interfaces/modelos) |
| ArchUnit | - | Validación de arquitectura |
| Jakarta Validation | - | Validaciones de Bean Validation |

## 3. Estructura de Paquetes

```text
es.cesguiro.shop
├── ShopApplication.java
├── config
│   ├── BeanConfig.java        // Ensamblado de beans (ProductService con ProductDao)
│   └── OpenApiConfig.java     // Configuración OpenAPI
├── controller
│   ├── CatalogoController.java   // Implementa CatálogosApi (generada)
│   ├── ProductController.java    // Implementa ProductosApi (generada)
│   └── mapper
│       └── ProductControllerMapper.java // Mapeo domain.Product <-> controller.model
├── controller.api                // GENERADO por openapi-generator
│   ├── CatalogosApi.java
│   └── ProductosApi.java
├── controller.model              // GENERADO por openapi-generator
│   ├── ProductRequest.java
│   ├── ProductResponse.java
│   └── ErrorResponse.java
├── domain
│   ├── Product.java              // Clase anémica (sin Spring/JPA)
│   ├── ProductDao.java           // Interfaz/puerto de salida
│   ├── ProductService.java       // Puro Java, inyecta ProductDao, devuelve catálogo
│   └── exception
│       └── ProductNotFoundException.java
└── persistence
    ├── ProductDaoJdbc.java       // Implementa ProductDao con JdbcTemplate
    └── mapper
        └── ProductRowMapper.java // Mapea ResultSet -> Product
```

## 4. Modelo de Datos

### Entidad de Dominio: Product (Anémica)

Campos:
- `Long id`
- `Long idCatalogo`
- `String nombre`
- `String descripcion`
- `BigDecimal precio`
- `Integer stock`

Solo constructor vacío, constructor con todos los campos, getters y setters. Sin lógica de negocio.

### Tabla en MariaDB: product

```sql
CREATE TABLE IF NOT EXISTS product (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_catalogo BIGINT NOT NULL,
  nombre VARCHAR(255) NOT NULL,
  descripcion VARCHAR(1000),
  precio DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL,
  INDEX idx_product_id_catalogo (id_catalogo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## 5. Endpoints REST

| Método | Ruta | Acción | Descripción |
|---|---|---|---|
| `GET` | `/api/catalogos` | Obtener catálogo completo | Devuelve todos los productos. Delegado a `ProductService.obtenerCatalogo()`. |
| `GET` | `/api/catalogos/{idCatalogo}` | Obtener por idCatalogo | Filtra productos por catálogo. |
| `GET` | `/api/productos` | Listar todos los productos | Listado completo de productos. |
| `GET` | `/api/productos/{id}` | Obtener producto por ID | Búsqueda individual. |
| `POST` | `/api/productos` | Crear producto | Creación con validación `@Valid` sobre `ProductRequest`. |
| `PUT` | `/api/productos/{id}` | Actualizar producto | Actualización completa con validación. |
| `DELETE` | `/api/productos/{id}` | Eliminar producto | **Borrado físico**. |

## 6. Infraestructura

### docker-compose.yml

```yaml
version: '3.8'
services:
  mariadb:
    image: mariadb:11.4
    container_name: tienda-mariadb
    environment:
      MARIADB_ROOT_PASSWORD: root
      MARIADB_DATABASE: tienda
      MARIADB_USER: tienda
      MARIADB_PASSWORD: tienda
    ports:
      - "3306:3306"
    volumes:
      - ./db/init.sql:/docker-entrypoint-initdb.d/init.sql:ro
      - mariadb_data:/var/lib/mysql
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
volumes:
  mariadb_data:
```

### application.properties

```properties
# Datasource
spring.datasource.url=jdbc:mariadb://localhost:3306/tienda
spring.datasource.username=tienda
spring.datasource.password=tienda
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JDBC
spring.jdbc.template.fetch-size=100
spring.jdbc.template.max-rows=1000

# Swagger / OpenAPI
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
springdoc.packages-to-scan=es.cesguiro.shop.controller
springdoc.default-produces-media-type=application/json

# Server
server.port=8080
```

### URLs Swagger UI

- `http://localhost:8080/swagger-ui.html` / `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/v3/api-docs` (JSON)
- `http://localhost:8080/v3/api-docs.yaml` (YAML)

## 7. Dependencias Maven

```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- MariaDB -->
    <dependency>
        <groupId>org.mariadb.jdbc</groupId>
        <artifactId>mariadb-java-client</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- OpenAPI/Swagger -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.8.9</version>
    </dependency>
    
    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.tngtech.archunit</groupId>
        <artifactId>archunit-junit5</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>org.openapitools</groupId>
            <artifactId>openapi-generator-maven-plugin</artifactId>
            <version>7.10.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                    <configuration>
                        <inputSpec>${project.basedir}/src/main/resources/openapi.yaml</inputSpec>
                        <generatorName>spring</generatorName>
                        <apiPackage>es.cesguiro.shop.controller.api</apiPackage>
                        <modelPackage>es.cesguiro.shop.controller.model</modelPackage>
                        <generateSupportingFiles>false</generateSupportingFiles>
                        <cleanupOutput>true</cleanupOutput>
                        <configOptions>
                            <interfaceOnly>true</interfaceOnly>
                            <useSpringBoot3>true</useSpringBoot3>
                            <useJakartaEe>true</useJakartaEe>
                            <useTags>true</useTags>
                            <skipDefaultInterface>true</skipDefaultInterface>
                            <openapiNullable>false</openapiNullable>
                            <useBeanValidation>true</useBeanValidation>
                        </configOptions>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## 8. Reglas ArchUnit

Se crearán tests en `src/test/java/es/cesguiro/shop/architecture/ArchitectureTest.java` con las siguientes reglas:

| Regla | Motivo |
|---|---|
| `..domain..` no debe depender de `org.springframework..` | Dominio 100% puro |
| `..domain..` no debe depender de `jakarta.persistence..`, `org.hibernate..` | Sin JPA/Hibernate en el núcleo |
| `..domain..` no debe depender de `..controller..` ni `..persistence..` | Aislamiento total del núcleo |
| `..controller..` solo puede depender de `..domain..`, `java..`, `jakarta.validation..`, `org.springframework.web..` y `..controller.api..`, `..controller.model..` | Adaptador primario aislado |
| `..persistence..` solo puede depender de `..domain..`, `java..`, `org.springframework.jdbc..`, `org.springframework.dao..` | Adaptador secundario con JdbcTemplate |
| `..controller..` no puede depender de `..persistence..` | Prohibido salto entre adaptadores |
| `..persistence..` no puede depender de `..controller..` | Prohibido salto entre adaptadores |
| `..persistence.ProductDaoJdbc` debe implementar `..domain.ProductDao` | Forzar inversión de dependencias |
| Clases `*Service` en `..domain..` no deben tener `@Service`, `@Component`, `@Repository` | ProductService debe ser puro Java |
| Prohibido `@Autowired` en cualquier campo | Obligatorio constructor injection |
| `noClasses().should().dependOnClassesThat().resideInAPackage("javax..")` | Prohibido usar javax, forzar jakarta |

## 9. Workflow Git con Hitos

**Repositorio remoto:** `https://github.com/raulmontero044-ctrl/TiendaRepository`  
**No se hace `git init`**.  
**Todo desde `develop`**, nunca directamente desde `main`.  
**No se eliminarán las ramas tras el merge**.  
**Cada hito = subrama + merge a `develop` con merge commit + tag**.

### Mapeo de Hitos

| Tag | Subrama | Descripción | Alcance |
|---|---|---|---|
| `hito-01` | `feature/hito-01-estructura-base` | Estructura base arquitectura limpia | Paquetes, reglas base ArchUnit, `ShopApplication` |
| `hito-02` | `feature/hito-02-dominio-producto` | Dominio anémico y puertos | `Product`, `ProductDao`, `ProductService`, `ProductNotFoundException` |
| `hito-03` | `feature/hito-03-docker-mariadb-init` | Infraestructura BD | `docker-compose.yml`, `db/init.sql` |
| `hito-04` | `feature/hito-04-persistencia-jdbc` | Persistencia con JdbcTemplate | `ProductRowMapper`, `ProductDaoJdbc`, `application.properties` (conexión) |
| `hito-05` | `feature/hito-05-controladores-api` | Controladores REST | `CatalogoController`, `ProductController`, DTOs iniciales, mapeo |
| `hito-06` | `feature/hito-06-openapi-base` | Contrato OpenAPI base | `src/main/resources/openapi.yaml` inicial |
| `hito-07` | `feature/hito-07-swagger-ui` | Swagger UI | `springdoc-openapi`, `OpenApiConfig`, propiedades |
| `hito-08` | `feature/hito-08-openapi-generator-contract-first` | Contract-first | `openapi-generator-maven-plugin`, generación `api/model`, implementación de interfaces |
| `hito-09` | `feature/hito-09-jakarta-enforcement` | Forzar Jakarta | `useJakartaEe=true`, regla `NO_JAVAX`, limpieza imports |
| `hito-10` | `feature/hito-10-openapi-validaciones` | Validaciones ampliadas | Ampliación contrato con validaciones, regeneración con Bean Validation |

### Comandos Git

```bash
# Preparación
git remote -v
git branch -a

# Crear develop si no existe
git checkout main
git pull origin main
git checkout -b develop
git push -u origin develop

# Por cada hito
git checkout develop
git pull origin develop
git checkout -b feature/hito-XX-<descripcion-corta>
# ... implementar ...
git add .
git commit -m "feat(hito-XX): <descripción>"
git push -u origin feature/hito-XX-<descripcion-corta>
git checkout develop
git pull origin develop
git merge --no-ff feature/hito-XX-<descripcion-corta>
git push origin develop
git tag -a hito-XX -m "Hito XX: <titulo>"
git push origin hito-XX
# NO eliminar rama
```

## 10. Orden de Implementación

1. **Hito 01**: Estructura base de paquetes, reglas ArchUnit base, `ShopApplication`
2. **Hito 02**: Dominio (`Product` anémico, `ProductDao`, `ProductService` puro, excepción)
3. **Hito 03**: `docker-compose.yml`, `db/init.sql`
4. **Hito 04**: `pom.xml` con dependencias JDBC, `ProductRowMapper`, `ProductDaoJdbc`, `application.properties`
5. **Hito 05**: Controladores REST (`/api/productos`, `/api/catalogos`), DTOs manuales iniciales, mapeo
6. **Hito 06**: `openapi.yaml` base en `src/main/resources/`
7. **Hito 07**: `springdoc-openapi`, `OpenApiConfig`, configuración Swagger UI
8. **Hito 08**: `openapi-generator-maven-plugin`, generar interfaces/modelos, adaptar controladores a contract-first
9. **Hito 09**: Forzar Jakarta (`useJakartaEe`, `NO_JAVAX` en ArchUnit), limpiar imports
10. **Hito 10**: Ampliar `openapi.yaml` con validaciones, regenerar con Bean Validation, integrar `@Valid`

## 11. Validaciones y Comprobaciones

| Fase | Comprobación |
|---|---|
| Compilación | `mvn clean compile` debe pasar con JDK 25 |
| Tests | `mvn test` debe pasar (incluidos tests de ArchUnit) |
| Docker | `docker compose up -d` levanta MariaDB correctamente con `db/init.sql` |
| Conexión BD | Aplicación conecta con MariaDB usando `application.properties` |
| Endpoints | Probar `/api/catalogos`, `/api/productos` vía Swagger UI o REST client |
| Swagger | Acceso a `/swagger-ui.html` y `/v3/api-docs` correcto |
| Validaciones | POST/PUT con datos inválidos devuelven `400` |
| Arquitectura | ArchUnit valida todas las reglas, sin `javax.*`, dominio puro |
| Git | Todas las ramas `feature/hito-XX-*` conservadas, tags `hito-01`-`hito-10` en `develop` |

## 12. Notas Importantes

- **Dominio puro**: `domain` nunca debe importar Spring, JPA, Hibernate, Jakarta (excepto quizás nada), o clases de `controller`/`persistence`.
- **Inversión**: `ProductService` solo conoce `ProductDao` (interfaz). `ProductDaoJdbc` es el único que conoce `JdbcTemplate` y SQL.
- **Contract-first estricto**: Una vez implementado hito-08, `openapi.yaml` es fuente única. Los DTOs manuales se reemplazan por los generados en `controller.model`.
- **Jakarta obligatorio**: Todos los imports de validación/web deben ser `jakarta.*`. Regla ArchUnit `NO_JAVAX` debe pasar.
- **Sin Flyway**: Esquema creado únicamente por `db/init.sql` montado en el entrypoint de MariaDB.
- **Sin eliminación de ramas**: Tras cada merge a `develop`, la rama `feature/hito-XX-*` permanece en local y remoto.
- **No git init**: El repositorio ya existe y está conectado a `origin`.
