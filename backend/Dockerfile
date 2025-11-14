# Stage 1: Build the application using a full JDK/Maven image
# Giai đoạn 1: Biên dịch ứng dụng bằng image JDK/Maven đầy đủ
FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the build file (pom.xml) first to leverage caching
# Sao chép file cấu hình (pom.xml) trước để tận dụng caching
COPY pom.xml .

# Copy the source code
# Sao chép mã nguồn
COPY src /app/src

# Package the application. The JAR will be created in target/
# Đóng gói ứng dụng. File JAR sẽ được tạo trong target/
RUN mvn clean package -DskipTests

# -----------------------------------------------------------------

# Stage 2: Create the final runtime image using a minimal JRE image
# Giai đoạn 2: Tạo image chạy cuối cùng bằng image JRE tối giản
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built JAR file from the 'build' stage to the runtime image
# Sao chép file JAR đã build từ giai đoạn 'build' sang image chạy
# Chú ý: Tên file JAR có thể khác, hãy kiểm tra lại tên chính xác trong pom.xml
COPY --from=build /app/target/backend-1.0-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port (if your app uses it)
# Mở port mặc định của Spring Boot
EXPOSE 8080

# Run the application
# Chạy ứng dụng
ENTRYPOINT ["java","-jar","/app/app.jar"]