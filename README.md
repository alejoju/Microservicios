# Productos API

Microservicio para gestión de productos de e-commerce.

## Requisitos

- Java 21+
- Maven 3.9+

## Ejecución

```bash
mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8090`.

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/productos` | Crear producto |
| GET | `/api/productos` | Listar activos (paginado) |
| GET | `/api/productos/{id}` | Obtener por ID |
| PUT | `/api/productos/{id}` | Actualizar |
| DELETE | `/api/productos/{id}` | Borrado lógico |
| GET | `/api/productos/categoria/{categoria}` | Filtrar por categoría |
| GET | `/api/productos/buscar?precioMin=&precioMax=` | Filtrar por rango de precio |

## Documentación

Swagger UI: `http://localhost:8090/swagger-ui.html`

## Pruebas

```bash
mvnw test
```

Reporte de cobertura (JaCoCo):

```bash
mvnw verify
```

El reporte se genera en `target/site/jacoco/index.html`.

Consola para la DB en H2:

```bash
http://localhost:8090/h2-console
```

## Ejemplos con curl

```bash
# Crear producto
curl -X POST http://localhost:8090/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Laptop","descripcion":"Laptop gaming","precio":15000.00,"cantidadStock":10,"categoria":"Electrónica"}'

# Listar productos
curl http://localhost:8090/api/productos

# Obtener por ID
curl http://localhost:8090/api/productos/1

# Actualizar
curl -X PUT http://localhost:8090/api/productos/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Laptop Pro","descripcion":"Laptop gaming actualizada","precio":18000.00,"cantidadStock":5,"categoria":"Electrónica"}'

# Eliminar (borrado lógico)
curl -X DELETE http://localhost:8090/api/productos/1

# Filtrar por categoría
curl http://localhost:8090/api/productos/categoria/Electrónica

# Filtrar por rango de precio
curl "http://localhost:8090/api/productos/buscar?precioMin=1000&precioMax=50000"
```
