FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/libros-service-0.0.1.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]