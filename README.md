# Inventario Service — Jakarta EE 11 + SQL Server 2022

Proyecto académico complementario de `sistema-pedidos-jsp` para la Sesión 4: **Integración de aplicativos web con componentes empresariales**.

## Stack tecnológico

- Java 21
- Maven
- Jakarta EE 11
- WildFly 41
- Jakarta REST
- Jakarta Enterprise Beans (EJB)
- Jakarta Persistence 3.2
- Jakarta Transactions (JTA)
- Jakarta Bean Validation
- Microsoft JDBC Driver for SQL Server
- Microsoft SQL Server 2022

## Arquitectura

```text
Sistema de Pedidos / Postman
          |
          | HTTP + JSON
          v
ProductoResource (Jakarta REST)
          |
          | @EJB
          v
InventarioService (@Stateless)
          |
          | @PersistenceContext
          v
EntityManager / InventarioPU
          |
          | java:jboss/datasources/InventarioDS
          v
SQL Server 2022 / InventarioDB
```

## Preparación rápida

1. Habilitar TCP/IP en SQL Server Configuration Manager.
2. Configurar un puerto TCP fijo para la instancia de SQL Server. En este proyecto se utiliza el puerto `1433`, siempre que esté configurado así en tu instancia.
3. Configurar la autenticación de SQL Server y crear las credenciales necesarias para la aplicación.
4. Ejecutar `database/01_crear_inventario.sql` desde SQL Server Management Studio (SSMS).
5. Verificar que Java 21 y Maven estén instalados y configurados.
6. Revisar la configuración de conexión a SQL Server y el DataSource utilizado por `persistence.xml`.

## Compilación y ejecución

Desde la terminal, situada en la carpeta raíz del proyecto, ejecutar:

```bash
mvn clean test
```

Si las pruebas terminan correctamente, iniciar WildFly con:

```bash
mvn clean wildfly:run
```

El plugin utiliza un desplazamiento de puertos de `2`. Si esa configuración está activa, WildFly utilizará los siguientes puertos:

- **HTTP:** `8082`
- **Administración:** `9992`

Esto permite ejecutar simultáneamente `sistema-pedidos`, siempre que utilice HTTP `8081` y administración `9990`.

## Acceso a la aplicación

Página principal:

```text
http://localhost:8082/inventario-service/
```

URL base de la API REST:

```text
http://localhost:8082/inventario-service/api
```

## Endpoints principales

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/inventario/productos` | Lista todos los productos |
| GET | `/inventario/productos/{id}` | Consulta un producto por ID |
| GET | `/inventario/productos/codigo/{codigo}` | Consulta un producto por código |
| GET | `/inventario/productos/{id}/stock` | Consulta el stock por ID |
| GET | `/inventario/productos/codigo/{codigo}/stock` | Consulta el stock por código |
| POST | `/inventario/productos/{id}/reservas` | Reserva stock por ID |
| POST | `/inventario/productos/{id}/devoluciones` | Devuelve stock por ID |
| POST | `/inventario/productos/codigo/{codigo}/reservas` | Reserva stock por código |
| POST | `/inventario/productos/codigo/{codigo}/devoluciones` | Devuelve stock por código |

## Ejemplo de reserva

Solicitud HTTP:

```http
POST /inventario-service/api/inventario/productos/1/reservas
Content-Type: application/json
```

Cuerpo JSON:

```json
{
  "cantidad": 2
}
```

También se pueden utilizar los endpoints por código de producto, por ejemplo `LAP-001`, `MON-001` y `TEC-001`, siempre que esos códigos existan en la base de datos.

Para probar las solicitudes, utiliza el archivo:

`requests/inventario-service.http`

## Material de clase

- `docs/GUIA_IMPLEMENTACION_PASO_A_PASO.md`
- `docs/INTEGRACION_CON_SISTEMA_PEDIDOS.md`
- `requests/inventario-service.http`
- `openapi/inventario-service.yaml`
- `database/02_reset_stock_demo.sql`

## Nota de seguridad

Las credenciales de conexión no deben almacenarse directamente en el código fuente en entornos de producción. Se recomienda utilizar un DataSource administrado por WildFly y externalizar las credenciales mediante un mecanismo seguro.

La opción `trustServerCertificate=true` puede utilizarse en un laboratorio local con un certificado de confianza configurado de manera simplificada. En producción se deben utilizar certificados válidos y protocolos TLS compatibles.

