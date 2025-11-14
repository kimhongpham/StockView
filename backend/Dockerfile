# Sử dụng JDK 21
FROM eclipse-temurin:21-jdk

# Thư mục làm việc trong container
WORKDIR /app

# Copy jar đã build từ IntelliJ/Maven
COPY target/backend-1.0-SNAPSHOT.jar app.jar

# Expose port Spring Boot
EXPOSE 8080

# Chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]
