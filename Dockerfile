# Stage 1: Build Stage
FROM gradle:8.7.0-jdk21 AS builder

# Get version from build argument
ARG VERSION
RUN echo ">>> VERSION received: ${VERSION}"

ARG POSTGRES_USER
ARG POSTGRES_PASSWORD

ENV POSTGRES_USER=${POSTGRES_USER}
ENV POSTGRES_PASSWORD=${POSTGRES_PASSWORD}

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files first (for caching dependencies)
COPY gradle/wrapper gradle/wrapper
#COPY gradle.properties gradle.properties
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
COPY commonDeps.gradle commonDeps.gradle

# Cache Gradle dependencies
#RUN gradle dependencies

# Now copy the rest of the project files
COPY . .

# Build the project
RUN gradle build -x test -x pmdMain -x pmdTest -x pmdIntegrationTest

# Stage 2: Production Image
FROM openjdk:21-jdk

# Set working directory and expose port
ARG VERSION
WORKDIR /app
EXPOSE 8000

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/afaDevicerBackV8-${VERSION}.jar app.jar

# Run the application
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-XX:+UseSerialGC", "-jar", "app.jar"]
