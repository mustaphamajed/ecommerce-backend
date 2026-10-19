# ---- Build stage: compile the jar with the full JDK ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy only what's needed to download dependencies first:
# this layer stays cached until pom.xml changes → much faster rebuilds
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests -B

# ---- Run stage: only the JRE + the jar (smaller, no build tools) ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Don't run as root inside the container
RUN useradd --system --uid 1001 spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
