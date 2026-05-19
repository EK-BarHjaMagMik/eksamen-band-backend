# syntax=docker/dockerfile:1

# --- Build stage: compile and package with Maven ---
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy the Maven wrapper and pom first so dependency resolution is cached
# as long as pom.xml is unchanged.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -q dependency:go-offline

# Copy sources and build. Tests run separately in the Maven CI workflow,
# so skip them here to keep image builds fast and deterministic.
COPY src/ src/
RUN ./mvnw -B -q -DskipTests package

# --- Runtime stage: slim JRE with just the packaged jar ---
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# Run as a non-root user.
RUN useradd --system --no-create-home --shell /usr/sbin/nologin appuser
USER appuser

COPY --from=build /app/target/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
