# EjercicioSupermercado

Sistema de gestión de supermercado full-stack con arquitectura MVC, autenticación JWT, control de roles y despliegue Docker.

---

## Propósito

Desarrollar una aplicación web completa que simule la gestión de un supermercado, abarcando desde el diseño del modelo de datos hasta el despliegue en contenedores Docker. El proyecto integra un backend RESTful con Spring Boot, un frontend moderno con Next.js, y seguridad basada JWT con control de roles.

---

## Aprendizajes

### Backend (Spring Boot)

- **Arquitectura MVC en capas**: Controller → Service → Repository, separación de responsabilidades
- **Spring Security 7.x**: Configuración de seguridad sin sesiones, filtros JWT, codificación BCrypt
- **JWT (jjwt)**: Generación, validación y expiración de tokens
- **Bean Validation**: Validación de DTOs con anotaciones (`@NotBlank`, `@Min`, `@Valid`)
- **Manejo de excepciones**: `@RestControllerAdvice` con handlers centralizados (400, 404, 429, 500)
- **JPA/Hibernate**: Relaciones ManyToOne, OneToMany, cascade, orphanRemoval, JPQL con `JOIN FETCH` (N+1)
- **Testing**: MockMvc, MockitoBean, `@AutoConfigureMockMvc(addFilters = false)` para tests aislados de seguridad

### Frontend (Next.js)

- **App Router**: Navegación por carpetas en `app/`, layouts anidados
- **React Context**: Estado global de autenticación (`AuthContext`)
- **CSS Modules**: Estilos modulares sin conflictos de nombres
- **Chart.js**: Gráficos de barras, líneas y dona para dashboards
- **Protección de rutas**: `ProtectedRoute` con redirección por rol
- **Comunicación HTTP**: Servicios API con `fetch`, manejo de tokens y errores

### Infraestructura

- **Docker multi-stage builds**: Imágenes optimizadas (build separado de runtime)
- **Docker Compose**: Orquestación de múltiples servicios (nginx, backend, frontend)
- **Nginx**: Proxy inverso, servir estáticos, redirigir API al backend
- **Standalone Next.js**: Build autónomo sin `node_modules` completo

### Buenas prácticas aplicadas

- DTOs para desacoplar entidades de la API
- Validación server-side y client-side
- Manejo de errores consistente en formato JSON
- Usuarios semilla por defecto (`DataInitializer`)
- Protección contra brute-force (bloqueo tras 5 intentos)
- README completo con arquitectura, endpoints y configuración

---

## Arquitectura

```
                    ┌─────────────┐
                    │   Nginx:80  │
                    └──────┬──────┘
                           │
              ┌────────────┴────────────┐
              │                         │
     ┌────────▼────────┐     ┌─────────▼─────────┐
     │  Frontend:3000  │     │   Backend:8080    │
     │   Next.js 14    │     │  Spring Boot 4.1  │
     │  React 18 + TS  │     │  Spring Security  │
     └─────────────────┘     │  JWT + BCrypt     │
                             └────────┬──────────┘
                                      │
                             ┌────────▼──────────┐
                             │   H2 in-memory    │
                             │   (supermercado)  │
                             └───────────────────┘
```

- **Nginx**: Proxy inverso, sirve frontend y redirige `/api/*` al backend
- **Frontend**: SPA con Next.js (App Router), Chart.js para gráficos
- **Backend**: REST API con Spring Boot, seguridad JWT, validación Bean
- **Base de datos**: H2 en memoria (se reinicia al reiniciar el contenedor)

---

## Stack Tecnológico

| Capa | Tecnología | Versión |
|------|-----------|---------|
| Runtime Backend | Java | 17 |
| Framework Backend | Spring Boot | 4.1.0 |
| Seguridad | Spring Security | 7.1.0 |
| JWT | jjwt | 0.12.6 |
| Validación | Bean Validation (Jakarta) | - |
| ORM | Hibernate / Spring Data JPA | - |
| Base de datos | H2 | 2.4.240 |
| Runtime Frontend | Node.js | 18 |
| Framework Frontend | Next.js (App Router) | 14.2.35 |
| UI | React | 18.3.1 |
| Lenguaje Frontend | TypeScript | 5.5.0 |
| Gráficos | Chart.js + react-chartjs-2 | 4.4.7 / 5.2.0 |
| Estilos | CSS Modules | - |
| Contenedorización | Docker + Docker Compose | - |
| Proxy | Nginx | Alpine |

---

## Estructura del Proyecto

```
EjercicioSupermercado/
├── Dockerfile.backend              # Multi-stage: Maven → JRE 17
├── docker-compose.yml              # Orquesta backend + frontend + nginx
├── nginx.conf                      # Proxy inverso
├── .dockerignore
├── pom.xml                         # Dependencias Maven
├── mvnw / mvnw.cmd                 # Maven wrapper
│
├── src/main/java/lat/sebascasavilca/EjercicioSupermercado/
│   ├── EjercicioSupermercadoApplication.java
│   ├── config/
│   │   └── DataInitializer.java          # Seed de usuarios por defecto
│   ├── controller/
│   │   ├── AuthController.java           # POST /api/auth/login
│   │   ├── DashboardController.java      # GET /api/dashboard
│   │   ├── ProductoController.java       # CRUD /api/productos
│   │   ├── SucursalController.java       # CRUD /api/sucursales
│   │   └── VentaController.java          # CRUD /api/ventas
│   ├── dto/
│   │   ├── DashboardDto.java
│   │   ├── DetalleVentaDto.java
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── ProductoDto.java
│   │   ├── SucursalDto.java
│   │   ├── UsuarioDto.java
│   │   └── VentaDto.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java   # Manejo centralizado de errores
│   │   └── NotFoundException.java        # Excepción 404
│   ├── mapper/
│   │   └── Mapper.java                   # Entity ↔ DTO
│   ├── model/
│   │   ├── DetalleVenta.java
│   │   ├── Producto.java
│   │   ├── Sucursal.java
│   │   ├── Usuario.java
│   │   └── Venta.java
│   ├── repository/
│   │   ├── ProductoRepository.java
│   │   ├── SucursalRepository.java
│   │   ├── UsuarioRepository.java
│   │   └── VentaRepository.java
│   ├── security/
│   │   ├── CustomUserDetailsService.java  # Auth + brute-force protection
│   │   ├── JwtFilter.java                # Filtro de autenticación JWT
│   │   ├── JwtUtil.java                  # Generación/validación JWT
│   │   └── SecurityConfig.java           # Configuración de seguridad
│   └── service/
│       ├── DashboardService.java
│       ├── IDashboardService.java
│       ├── IProductoService.java
│       ├── ISucursalService.java
│       ├── IVentaService.java
│       ├── ProductoService.java
│       ├── SucursalService.java
│       └── VentaService.java
│
├── src/main/resources/
│   └── application.properties
│
├── src/test/java/lat/sebascasavilca/EjercicioSupermercado/
│   ├── EjercicioSupermercadoApplicationTests.java
│   └── controller/
│       ├── AuthControllerTest.java        (5 tests)
│       ├── DashboardControllerTest.java   (2 tests)
│       ├── ProductoControllerTest.java    (14 tests)
│       ├── SucursalControllerTest.java    (12 tests)
│       └── VentaControllerTest.java       (11 tests)
│
└── frontend/
    ├── Dockerfile                         # Multi-stage: Node 18 standalone
    ├── package.json
    ├── next.config.js
    ├── tsconfig.json
    ├── app/
    │   ├── globals.css
    │   ├── layout.tsx                     # AuthProvider wrapper
    │   ├── layout.css                     # Sidebar + main layout
    │   ├── page.tsx                       # Redirect → /login
    │   ├── login/page.tsx
    │   ├── dashboard/page.tsx + dashboard.module.css
    │   ├── productos/page.tsx + productos.module.css
    │   ├── sucursales/page.tsx + sucursales.module.css
    │   └── ventas/page.tsx + ventas.module.css
    ├── components/
    │   ├── MainLayout.tsx
    │   ├── ProtectedRoute.tsx
    │   └── Sidebar.tsx
    ├── context/
    │   └── AuthContext.tsx
    └── services/
        ├── authService.ts
        ├── productoService.ts
        ├── sucursalService.ts
        └── ventaService.ts
```

---

## Modelo de Datos

```
┌──────────────┐       ┌──────────────┐
│   Sucursal   │       │   Producto   │
├──────────────┤       ├──────────────┤
│ id (PK)      │       │ id (PK)      │
│ nombre       │       │ nombre       │
│ direccion    │       │ categoria    │
└──────┬───────┘       │ precio       │
       │               │ cantidad     │
       │               └──────┬───────┘
       │                      │
       │  ┌───────────────────┘
       │  │
┌──────▼──┴────────────┐
│       Venta           │
├───────────────────────┤
│ id (PK)               │
│ fecha (LocalDate)     │
│ estado (String)       │
│ total (Double)        │
│ sucursal_id (FK)      │
└──────┬────────────────┘
       │
       │  ┌───────────────────┐
       │  │                   │
┌──────▼──┴────────┐   ┌─────▼──────────┐
│  DetalleVenta    │   │    Usuario      │
├──────────────────┤   ├────────────────┤
│ id (PK)          │   │ id (PK)        │
│ venta_id (FK)    │   │ username (UQ)  │
│ prod_id (FK)     │   │ password       │
│ cantProd         │   │ rol            │
│ precio           │   └────────────────┘
└──────────────────┘
```

### Relaciones

| Relación | Tipo | Descripción |
|----------|------|-------------|
| Venta → Sucursal | ManyToOne | Cada venta pertenece a una sucursal |
| Venta → DetalleVenta | OneToMany | Una venta tiene muchos detalles (cascade ALL, orphanRemoval) |
| DetalleVenta → Venta | ManyToOne | Cada detalle pertenece a una venta (@JsonIgnore) |
| DetalleVenta → Producto | ManyToOne | Cada detalle referencia un producto |

---

## Seguridad

### Autenticación JWT

1. El cliente envía `POST /api/auth/login` con `username` y `password`
2. El servidor valida credenciales y retorna un JWT (24h de expiración)
3. El cliente envía el token en el header `Authorization: Bearer <token>` en cada petición
4. El `JwtFilter` valida el token y establece la autenticación en el contexto

### Roles

| Rol | Lectura (`GET`) | Escritura (`POST/PUT/DELETE`) |
|-----|-----------------|-------------------------------|
| `ADMIN` | Permitido | Permitido |
| `VISUALIZADOR` | Permitido | Denegado (403) |

### Protección contra brute-force

- Máximo **5 intentos** fallidos por usuario
- Al superar el límite: respuesta `429 Too Many Requests`
- Se reinicia al hacer login exitoso
- Controlado en memoria por `CustomUserDetailsService`

### Endpoints protegidos

| Método | Patrón | Acceso |
|--------|--------|--------|
| OPTIONS | `/**` | Público (CORS preflight) |
| POST | `/api/auth/**` | Público |
| GET | `/h2-console/**` | Público (desarrollo) |
| GET | `/api/**` | `ADMIN` o `VISUALIZADOR` |
| POST | `/api/**` | Solo `ADMIN` |
| PUT | `/api/**` | Solo `ADMIN` |
| DELETE | `/api/**` | Solo `ADMIN` |

---

## API REST

### Auth

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/login` | Login, retorna JWT | No |

### Productos

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/productos` | Listar todos | ADMIN/VISUALIZADOR |
| GET | `/api/productos/{id}` | Obtener por ID | ADMIN/VISUALIZADOR |
| POST | `/api/productos` | Crear producto | ADMIN |
| PUT | `/api/productos/{id}` | Actualizar producto | ADMIN |
| DELETE | `/api/productos/{id}` | Eliminar producto | ADMIN |

### Sucursales

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/sucursales` | Listar todas | ADMIN/VISUALIZADOR |
| GET | `/api/sucursales/{id}` | Obtener por ID | ADMIN/VISUALIZADOR |
| POST | `/api/sucursales` | Crear sucursal | ADMIN |
| PUT | `/api/sucursales/{id}` | Actualizar sucursal | ADMIN |
| DELETE | `/api/sucursales/{id}` | Eliminar sucursal | ADMIN |

### Ventas

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/ventas` | Listar todas (con detalles) | ADMIN/VISUALIZADOR |
| GET | `/api/ventas/{id}` | Obtener por ID | ADMIN/VISUALIZADOR |
| POST | `/api/ventas` | Crear venta (calcula total) | ADMIN |
| PUT | `/api/ventas/{id}` | Actualizar venta | ADMIN |
| DELETE | `/api/ventas/{id}` | Eliminar venta | ADMIN |

### Dashboard

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/dashboard` | Estadísticas agregadas | ADMIN/VISUALIZADOR |

---

## Validación de DTOs

### ProductoDto

| Campo | Regla | Mensaje de error |
|-------|-------|------------------|
| `nombre` | `@NotBlank` | El nombre es obligatorio |
| `categoria` | `@NotBlank` | La categoría es obligatoria |
| `precio` | `@Min(0)` | El precio no puede ser negativo |
| `cantidad` | `@NotNull @Min(0)` | La cantidad es obligatoria / no puede ser negativa |

### SucursalDto

| Campo | Regla | Mensaje de error |
|-------|-------|------------------|
| `nombre` | `@NotBlank` | El nombre es obligatorio |
| `direccion` | `@NotBlank` | La dirección es obligatoria |

### VentaDto

| Campo | Regla | Mensaje de error |
|-------|-------|------------------|
| `fecha` | `@NotNull` | La fecha es obligatoria |
| `estado` | `@NotBlank` | El estado es obligatorio |
| `idSucursal` | `@NotNull` | Debe indicar la sucursal |
| `detalle` | `@Valid` (cascada) | Valida cada DetalleVentaDto |

### DetalleVentaDto

| Campo | Regla | Mensaje de error |
|-------|-------|------------------|
| `nombreProd` | `@NotBlank` | El nombre del producto es obligatorio |
| `cantProd` | `@NotNull @Min(1)` | La cantidad es obligatoria / debe ser al menos 1 |
| `precio` | `@NotNull @Min(0)` | El precio es obligatorio / no puede ser negativo |

---

## Frontend

### Rutas

| Ruta | Página | Descripción |
|------|--------|-------------|
| `/` | page.tsx | Redirect a `/login` |
| `/login` | login/page.tsx | Formulario de login |
| `/dashboard` | dashboard/page.tsx | Dashboard con gráficos Chart.js |
| `/productos` | productos/page.tsx | CRUD de productos |
| `/sucursales` | sucursales/page.tsx | CRUD de sucursales |
| `/ventas` | ventas/page.tsx | CRUD de ventas |

### Componentes

| Componente | Archivo | Descripción |
|------------|---------|-------------|
| `MainLayout` | components/MainLayout.tsx | Layout flex: sidebar + contenido |
| `Sidebar` | components/Sidebar.tsx | Navegación, info de usuario, logout |
| `ProtectedRoute` | components/ProtectedRoute.tsx | Guard de autenticación y roles |

### Context

- **AuthContext**: `token`, `rol`, `username`, `isAuthenticated`, `isAdmin`, `login()`, `logout()`. Persiste en `localStorage`.

### Servicios API

| Servicio | Funciones |
|----------|-----------|
| `authService` | `login(username, password)` |
| `productoService` | `getProductos()`, `createProducto()`, `updateProducto()`, `deleteProducto()` |
| `sucursalService` | `getSucursales()`, `createSucursal()`, `updateSucursal()`, `deleteSucursal()` |
| `ventaService` | `getVentas()`, `createVenta()`, `deleteVenta()` |

### Dashboard (Chart.js)

- **Barras**: Ventas por sucursal
- **Líneas**: Ingresos por mes
- **Dona**: Top 5 productos más vendidos
- **Cards**: Total productos, sucursales, ventas, ingreso total

---

## Tests

**Total: 45 tests** — Todos pasan con `mvnw test`

| Clase | Tests | Cubre |
|-------|-------|-------|
| `ProductoControllerTest` | 14 | GET, GET by ID, POST, PUT, DELETE, validación (5 campos), 404 |
| `SucursalControllerTest` | 12 | GET, GET by ID, POST, PUT, DELETE, validación (3 campos), 404 |
| `VentaControllerTest` | 11 | GET, GET by ID, POST, PUT, DELETE, validación (4 campos), 404 |
| `AuthControllerTest` | 5 | Login admin, login visual, credenciales incorrectas (401), bloqueado (429), body vacío |
| `DashboardControllerTest` | 2 | Estadísticas con datos, estadísticas vacías |
| `ApplicationTests` | 1 | Context loads |
| **Total** | **45** | |

### Ejecutar tests

```bash
# Con Maven wrapper
./mvnw test

# En Windows
mvnw.cmd test

# Saltar tests en build
mvnw.cmd package -DskipTests
```

### Estrategia de testing

- **MockMvc** con `@AutoConfigureMockMvc(addFilters = false)` (deshabilita filtros de seguridad)
- **@MockitoBean** para mocks de servicios
- **@SpringBootTest** para integración completa
- **ObjectMapper** con `JavaTimeModule` para serialización de `LocalDate`

---

## Instalación y Ejecución

### Requisitos previos

| Herramienta | Versión | Uso |
|-------------|---------|-----|
| Java JDK | 17 | Backend |
| Maven | 3.9+ (o usar wrapper) | Build del backend |
| Node.js | 18+ | Frontend |
| Docker Desktop | Latest | Despliegue contenedorizado |

### Ejecución Local (Desarrollo)

#### 1. Backend

```bash
# Compilar y ejecutar
./mvnw spring-boot:run

# O en Windows
mvnw.cmd spring-boot:run

# El backend estará en: http://localhost:8080
# Consola H2: http://localhost:8080/h2-console
```

> **Nota**: Si usas IntelliJ, configura `JAVA_HOME` a JDK 17 antes de ejecutar.

#### 2. Frontend

```bash
cd frontend

# Instalar dependencias
npm install

# Ejecutar en desarrollo
npm run dev

# El frontend estará en: http://localhost:3000
```

#### 3. Verificar

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Obtener productos (usar token del login)
curl http://localhost:8080/api/productos \
  -H "Authorization: Bearer <token>"
```

### Despliegue con Docker

#### 1. Instalar Docker Desktop

- Descargar desde: https://www.docker.com/products/docker-desktop/
- Requisito: Windows 10/11 con WSL 2 habilitado
- Reiniciar después de la instalación

#### 2. Levantar servicios

```bash
# Build y ejecutar (primera vez: ~3-5 min)
docker compose up --build

# Ejecutar en background
docker compose up -d --build

# Ver logs
docker compose logs -f
```

#### 3. Acceder

```
http://localhost       → Frontend (a través de Nginx)
http://localhost:8080  → Backend (no expuesto, solo interno)
```

#### 4. Comandos útiles

```bash
# Detener servicios
docker compose down

# Reconstruir un servicio específico
docker compose up --build backend

# Ver estado
docker compose ps

# Limpiar imágenes
docker compose down --rmi all
```

---

## Credenciales por Defecto

| Usuario | Contraseña | Rol | Permisos |
|---------|-----------|-----|----------|
| `admin` | `admin123` | `ADMIN` | CRUD completo |
| `visual` | `visual123` | `VISUALIZADOR` | Solo lectura |

> Los usuarios se crean automáticamente al iniciar la aplicación (`DataInitializer`).

---

## Configuración

### application.properties

```properties
# Base de datos H2 en memoria
spring.datasource.url=jdbc:h2:mem:supermercado
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Hibernate (auto-create tables)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Consola H2 (solo desarrollo)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT
jwt.secret=supermercadoSecretKey12345678901234567890123456
jwt.expiration=86400000  # 24 horas en milisegundos
```

### Variables de entorno Docker

| Variable | Valor por defecto | Descripción |
|----------|-------------------|-------------|
| `SPRING_PROFILES_ACTIVE` | `docker` | Perfil de Spring para Docker |

---

## Manejo de Errores

| HTTP Status | Excepción | Descripción |
|-------------|-----------|-------------|
| 400 | `MethodArgumentNotValidException` | Error de validación (retorna campos con mensajes) |
| 401 | Spring Security | Credenciales incorrectas |
| 403 | Spring Security | Sin permisos (VISUALIZADOR intentando escritura) |
| 404 | `NotFoundException` | Recurso no encontrado |
| 429 | `CustomUserDetailsService` | Demasiados intentos fallidos |
| 500 | `Exception` | Error interno del servidor |

### Formato de respuesta de error 400

```json
{
  "nombre": "El nombre es obligatorio",
  "categoria": "La categoría es obligatoria"
}
```

### Formato de respuesta de error 404

```json
{
  "mensaje": "Producto no encontrado"
}
```

---

## Licencia

Proyecto académico — Ejercicio de Spring Boot + Next.js
