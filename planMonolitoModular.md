# Plan Monolito Modular - División para 3 agentes

Este documento explica el plan tal como se lo darías a 3 agentes para migrar el proyecto actual (`es.shop` con arquitectura por capas) hacia un **monolito modular con puertos/adaptadores**, aplicando los principios de **SolidString**.

## Objetivo
Migrar por **módulos completos** a la siguiente estructura:
- `producto` (absorbe lo actual: productos/catálogos)
- `carrito`
- `pedido`
- `auth` (autenticación con sesión HTTP de Spring, aislada)
- `shared`

Reglas clave: comunicación **únicamente vía `*.api`** entre módulos, síncrona con interfaces Java, reutilizar DTOs/modelos donde sea posible, crear DTOs públicos en `api` solo cuando sea necesario. Mantener endpoints actuales funcionando. Los módulos `producto`, `carrito` y `pedido` deben tener `controller`, `domain` y `persistence`. `auth` sigue el esquema: `controller --> apln --\ / -----------------> service --> api --> apln --/`.

## División de agentes

### AGENTE 1: Estructura base + Módulo `producto`
**Responsable de:** Crear el esqueleto modular, mover todo lo relacionado con productos/catálogos al módulo `producto` y dejarlo funcional.

#### Tareas
1. **Crear esqueleto de paquetes**
   - Crear `es/shop/shared/{api,domain,config}`
   - Crear `es/shop/producto/{api,controller,domain,persistence}`
   - Crear `es/shop/carrito/{api,controller,domain,persistence}`
   - Crear `es/shop/pedido/{api,controller,domain,persistence}`
   - Crear `es/shop/auth/{api,apln,service,controller,domain,persistence}`

2. **Mover módulo `producto`**
   - Mover `domain/Product.java`, `domain/ProductDao.java`, `domain/ProductService.java`, `domain/exception/ProductNotFoundException.java` a `producto/domain/`
   - Mover `persistence/ProductDaoJdbc.java`, `persistence/mapper/ProductRowMapper.java` a `producto/persistence/` y `producto/persistence/mapper/`
   - Mover `controller/ProductController.java`, `controller/CatalogoController.java`, `controller/mapper/ProductControllerMapper.java` a `producto/controller/` y `producto/controller/mapper/`
   - Ajustar `BeanConfig` si es necesario para seguir funcionando con la nueva ubicación (o moverlo/reconfigurarlo)
   - Mantener los endpoints `/api/productos` y `/api/catalogos` exactamente igual

3. **Crear API pública de `producto`**
   - Crear `producto/api/ProductoPublicApi.java` con los métodos que otros módulos necesitarán (mínimo para empezar): obtener producto por id, listar productos, validar stock/disponibilidad, obtener datos para snapshot
   - Crear DTOs públicos en `producto/api/dto/` **únicamente si** es necesario para la comunicación entre módulos (reutilizar modelos existentes cuando no rompa encapsulamiento)
   - Crear implementación de `ProductoPublicApi` (puede delegar al `ProductService` o casos de uso existentes) y exponerla como `@Bean`/componente dentro de `producto`

4. **Verificación**
   - `mvn clean compile` debe compilar tras mover `producto`
   - Endpoints `/api/productos` y `/api/catalogos` siguen respondiendo
   - No hay imports cruzados a otros módulos aún (solo `shared` si estrictamente necesario)

#### Archivos a tocar/clave
- Todo lo relacionado con Product/Catalogo actual
- Crear `producto/api/*`
- Ajustar imports

---

### AGENTE 2: Autenticación + Infraestructura compartida
**Responsable de:** Montar `auth` aislado con sesión HTTP de Spring, crear `shared` mínimo y preparar el contexto de usuario para que lo consuman otros módulos vía `auth.api`.

#### Tareas
1. **Ajustar `shared`**
   - Mover excepciones realmente comunes a `shared/domain/exception/`
   - Crear `shared/api/` solo con lo estrictamente reutilizable (evitar utilidades que rompan el encapsulamiento de módulos)
   - `shared/config/` para configuración común si aplica

2. **Crear módulo `auth` (seguir esquema exacto)**
   - **Domain**: `Usuario` (anémico o con reglas mínimas), puertos de salida si aplica
   - **Persistence**: implementación JDBC para usuarios (tabla/estructura acorde al esquema existente o mínima para login). Usar `JdbcTemplate`
   - **Apln (casos de uso)**: `LoginUseCase`, `LogoutUseCase` (y registro si se necesita, pero mínimo). Orquestación pura
   - **Service**: lógica auxiliar reutilizable dentro de `auth` (generación/validación de sesión, helpers). **NO** accesible desde fuera
   - **Controller**: endpoints de login/logout. Traduce HTTP a `apln`. Debe usar sesión HTTP de Spring (`HttpSession`)
   - **API pública**: `auth/api/AuthPublicApi.java` con método controlado para obtener usuario autenticado actual (ej: `getCurrentUserId()`, `isAuthenticated()`). Solo esto se expondrá fuera de `auth`

3. **Integración con Spring Session**
   - Configurar para usar `HttpSession` de Spring
   - Resolver autenticación **únicamente** en `auth` (filtro/interceptor si necesario, pero dentro de `auth` o `auth/config`)
   - **NUNCA** inyectar lógica de sesión en controladores/servicios de `producto`, `carrito`, `pedido`

4. **Verificación**
   - `mvn clean compile`
   - Login/logout funcional con sesión HTTP
   - `auth.api` expuesto como bean, accesible desde otros módulos
   - Ningún módulo fuera de `auth` importa `auth.apln`, `auth.service`, `auth.domain`, `auth.persistence`, `auth.controller` - solo `auth.api`

#### Archivos a tocar/clave
- Crear todo `auth/*`
- Ajustar `shared/*`
- Configuración de sesión HTTP

---

### AGENTE 3: Carrito, Pedido + Reglas ArchUnit (SolidString)
**Responsable de:** Crear `carrito` y `pedido`, hacer que se comuniquen con `producto` vía `producto.api` de forma síncrona, ligar carrito a usuario autenticado vía `auth.api`, añadir reglas ArchUnit para forzar SolidString y validar todo junto.

#### Tareas
1. **Crear módulo `carrito`**
   - **Domain**: `Carrito`, `CarritoLinea`. Reglas del agregado. Puertos de salida para persistencia
   - **Persistence**: JDBC con `JdbcTemplate`. Persistido ligado a usuario autenticado
   - **Controller**: REST para gestionar carrito (añadir/actualizar/eliminar líneas, ver carrito). Debe obtener usuario vía `auth.api` (nunca leer sesión directamente)
   - **API pública**: `carrito/api/CarritoPublicApi.java`. Métodos necesarios para que `pedido` pueda obtener carrito, validar y vaciarlo si aplica. DTOs públicos en `api` si necesario
   - Implementación expuesta como bean

2. **Crear módulo `pedido`**
   - **Domain**: `Pedido`, `PedidoLinea`, enum de estados (mínimos). Reglas de agregado. Puertos de salida
   - **Persistence**: JDBC. Guardar con **snapshot**: nombre, precio unitario, cantidad, `productoId`
   - **Controller**: REST para crear pedido (desde carrito). Mantener rutas actuales solo si aplican, sino crear las necesarias sin romper lo existente
   - **Casos de uso/orquestación**: `CrearPedidoDesdeCarritoUseCase`. Flujo: obtener usuario via `auth.api`, obtener carrito via `carrito.api`, obtener datos de productos via `producto.api` (síncrono), validar stock, copiar snapshot, calcular totales, persistir pedido, vaciar carrito vía `carrito.api`. Transaccional
   - **API pública**: `pedido/api/PedidoPublicApi.java` si otros módulos lo necesitan (opcional por ahora)

3. **Comunicación entre módulos**
   - `pedido` **SOLO** llama a `producto.api`, `carrito.api`, `auth.api`
   - `carrito` **SOLO** llama a `auth.api` (y su propia persistencia)
   - **Prohibido** importar `*.domain`, `*.persistence`, `*.controller`, `*.apln`, `*.service` de otros módulos
   - Reutilizar DTOs donde sea posible; crear DTOs públicos en `api.dto` solo si imprescindible

4. **Añadir/actualizar reglas ArchUnit (SolidString)**
   Actualizar `src/test/java/es/shop/architecture/ArchitectureTest.java` con:
   - `modules_must_only_access_via_api`: fuera de `X.api`, ningún módulo puede acceder a internos de otro módulo X
   - `producto_carrito_pedido_must_have_core_packages`: deben tener `controller`, `domain`, `persistence`
   - `auth_isolation`: solo `auth.api` accesible desde fuera de `auth`; prohibir acceso a `auth.apln`, `auth.service`, `auth.domain`, `auth.persistence`, `auth.controller`
   - `controllers_must_not_depend_on_persistence`: `*.controller` no depende de `*.persistence` de ningún módulo
   - `persistence_must_only_depend_on_own_domain_and_allowed`: `*.persistence` solo depende de su propio `*.domain` + `java..`, `org.springframework.jdbc..`, `org.springframework.dao..`, `org.springframework.stereotype..`
   - `no_cross_module_domain_or_persistence`: prohibir acceso cruzado a `domain`/`persistence` entre módulos distintos
   - Mantener reglas existentes que sigan vigentes (dominio puro, sin javax, sin @Autowired, etc.)

5. **Verificación final**
   - `mvn clean compile`
   - `mvn test` (todos los tests de ArchUnit pasan)
   - Flujo completo: login (auth) -> carrito (ligado a usuario via auth.api) -> crear pedido (usa producto.api + carrito.api, snapshot correcto)
   - Endpoints existentes siguen funcionando
   - Sin dependencias circulares
   - Comunicación solo vía `*.api`

#### Archivos a tocar/clave
- Crear `carrito/*`, `pedido/*`
- Actualizar `ArchitectureTest.java`
- Asegurar integraciones con `auth.api` y `producto.api`

---

## Orden de ejecución

1. **AGENTE 1** primero: estructura + `producto`. Debe dejar compilando y endpoints funcionando.
2. **AGENTE 2** en paralelo o tras validar 1: `shared` + `auth`. Necesario para que `carrito` pueda ligar a usuario.
3. **AGENTE 3** tras 1 y 2: `carrito` + `pedido` + reglas ArchUnit. Valida todo junto.

## Criterios de aceptación

- [ ] Paquetes reorganizados por módulos completos
- [ ] `producto`, `carrito`, `pedido` tienen `controller`, `domain`, `persistence`
- [ ] `auth` tiene `api`, `apln`, `service`, `controller`, `domain`, `persistence`
- [ ] Todo módulo expone `*.api` como única puerta pública entre módulos
- [ ] Comunicación síncrona vía interfaces Java
- [ ] Carrito persistido ligado a usuario autenticado vía `auth.api`
- [ ] Pedido se crea desde carrito con **snapshot** (nombre, precio unitario, cantidad, productoId)
- [ ] Stock validado vía `producto.api`
- [ ] Autenticación solo en `auth`, con sesión HTTP de Spring
- [ ] Endpoints `/api/productos` y `/api/catalogos` siguen funcionando
- [ ] Reglas ArchUnit de SolidString pasan
- [ ] `mvn clean compile` y `mvn test` exitosos

## Notas para los agentes

- **Reutilizar antes de crear**: si ya existe un DTO/modelo útil, reutilízalo. Solo crea DTOs públicos en `api.dto` si es para comunicación entre módulos.
- **No romper encapsulamiento**: si no está en `*.api`, no se importa desde fuera del módulo.
- **Fino y puro**: `controller` fino, lógica en `domain`/`apln` según corresponda. `domain` lo más puro posible.
- **Transaccional**: la creación de pedido debe ser transaccional en el caso de uso.
- **Sin saltos**: `controller` nunca va directo a `persistence`.
- **Sesión, no JWT**: usar `HttpSession` de Spring tal como se acordó.
