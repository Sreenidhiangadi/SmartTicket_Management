FROM eclipse-temurin:17-jre

WORKDIR /user-service

COPY target/user-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
