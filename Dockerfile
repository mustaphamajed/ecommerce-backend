# ---- Build stage: compile the jar with the full JDK ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# The cache mount keeps ~/.m2 between builds: Maven and the dependencies are downloaded once,
# even when pom.xml changes
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY .git .git
COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw package -DskipTests -B

# ---- Run stage: only the JRE + the jar (smaller, no build tools) ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Don't run as root inside the container
RUN useradd --system --uid 1001 spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
