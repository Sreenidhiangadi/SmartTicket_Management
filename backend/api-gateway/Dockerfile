FROM eclipse-temurin:17-jre

WORKDIR /api-gateway

COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8765

CMD ["java", "-jar", "app.jar"]
