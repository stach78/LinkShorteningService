# ---- Build stage ----
FROM openjdk:21-jdk-slim AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src ./src
RUN ./mvnw -q -DskipTests package
RUN cp target/*.jar /workspace/app.jar

# ---- Runtime stage ----
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar

RUN useradd -u 10001 -m spring && chown -R spring /app
USER spring

EXPOSE 8080
ENV JAVA_OPTS=""
ENV APP_BASE_URL="http://localhost:8080"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar --app.base-url=${APP_BASE_URL}"]
