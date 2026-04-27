# ============================================================
# Multi-stage Dockerfile
# Stage 1: Build with Gradle
# Stage 2: Lightweight JRE runtime image
# ============================================================

# ---------- Stage 1: Build ----------
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app

# Cache Gradle dependencies first
COPY gradle/         gradle/
COPY gradlew         gradlew
COPY build.gradle.kts settings.gradle.kts ./

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon --quiet

# Copy source and build
COPY src/ src/
RUN ./gradlew bootJar --no-daemon --quiet -x test

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app

# Create a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
