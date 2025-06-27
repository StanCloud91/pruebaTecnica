# Usar OpenJDK 17 como imagen base
FROM openjdk:17-jdk-slim

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR del proyecto
COPY target/cuenta_movimiento-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto 8071
EXPOSE 8071

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"] 