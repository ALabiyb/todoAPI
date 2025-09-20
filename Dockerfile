# Multi-stage build for Spring Boot application
FROM maven:3.9.7-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:21-jre-jammy

# Add a non-root user
RUN addgroup --system spring && adduser --system --group spring

# Set working directory
WORKDIR /app

# Copy the JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to spring user
RUN chown -R spring:spring /app
USER spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# End of Dockerfile
# To build the Docker image, use:
# docker build -t my-spring-boot-app .
# To run the Docker container, use:
# docker run -p 8080:8080 my-spring-boot-app
# Make sure to have the actuator dependency in your pom.xml for health checks