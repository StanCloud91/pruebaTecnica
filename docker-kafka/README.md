# Arquitectura de Microservicios con Kafka

Este proyecto implementa una arquitectura de microservicios usando Spring Boot, Kafka, Redis y MySQL.

## Servicios Incluidos

- **MySQL**: Base de datos principal
- **Kafka**: Broker de mensajería
- **Zookeeper**: Coordinador para Kafka
- **Redis**: Cache en memoria
- **Kafdrop**: Interfaz web para Kafka
- **cliente-persona**: Microservicio en puerto 8070
- **cuenta-movimiento**: Microservicio en puerto 8071

## Requisitos Previos

- Docker
- Docker Compose
- Maven (para compilar los microservicios)

## Instrucciones de Uso

### 1. Compilar los Microservicios

```bash
# Compilar cliente_persona
cd ../cliente_persona
mvn clean package -DskipTests

# Compilar cuenta_movimiento
cd ../cuenta_movimiento
mvn clean package -DskipTests
```

### 2. Ejecutar con Docker Compose

```bash
# Desde el directorio docker-kafka
docker-compose up -d
```

### 3. Verificar Servicios

- **MySQL**: localhost:3306
- **Kafka**: localhost:9092
- **Redis**: localhost:6379
- **Kafdrop**: http://localhost:19000
- **cliente-persona**: http://localhost:8070
- **cuenta-movimiento**: http://localhost:8071

### 4. Detener Servicios

```bash
docker-compose down
```

### 5. Ver Logs

```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker logs -f cliente-persona
docker logs -f cuenta-movimiento
```

## Configuración de Base de Datos

- **Usuario**: root
- **Contraseña**: 1234
- **Base de datos**: prueba_tecnica (compartida por ambos microservicios)

## Endpoints Principales

### Cliente Persona (Puerto 8080)
- `POST /api/clientes` - Crear cliente
- `GET /api/clientes` - Listar clientes
- `GET /api/clientes/{id}` - Obtener cliente por ID

### Cuenta Movimiento (Puerto 8081)
- `POST /api/cuentas` - Crear cuenta
- `POST /api/movimientos` - Crear movimiento
- `GET /api/reportes` - Generar reporte de movimientos

## Comunicación entre Microservicios

1. **cliente-persona** envía mensajes a Kafka cuando se crea un cliente
2. **cuenta-movimiento** consume estos mensajes y almacena en Redis
3. Al crear una cuenta, se valida que el cliente exista en Redis
4. Los reportes se generan usando la identificación del cliente

## Troubleshooting

### Si los microservicios no se conectan a Kafka:
```bash
# Verificar que Kafka esté funcionando
docker-compose logs kafka

# Verificar conectividad desde el contenedor
docker exec -it cliente-persona ping kafka
```

### Si hay problemas con la base de datos:
```bash
# Verificar logs de MySQL
docker-compose logs mysql

# Conectar a MySQL
docker exec -it mysql mysql -u root -p1234
```

### Reiniciar un servicio específico:
```bash
docker-compose restart cliente-persona
docker-compose restart cuenta-movimiento
``` 