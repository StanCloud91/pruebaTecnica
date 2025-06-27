# Usar OpenJDK 17 como imagen base
FROM openjdk:17-jdk-slim

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR del proyecto
COPY target/cliente_persona-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto 8070
EXPOSE 8070

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"] 