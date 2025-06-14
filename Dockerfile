FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Gradle files
COPY build.gradle.kts settings.gradle.kts ./
COPY gradlew gradlew.bat ./
COPY gradle gradle

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies (ignore failures for faster builds)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Build application
RUN ./gradlew bootJar --no-daemon

# Expose port
EXPOSE 8080

# Run application
CMD ["java", "-jar", "build/libs/tax-planner-1.0.0.jar"] 