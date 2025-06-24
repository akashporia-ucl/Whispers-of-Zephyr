FROM maven:3.9.5-eclipse-temurin-21-alpine AS build

# Set the working directory inside the container
WORKDIR /common

# Copy only the necessary files for dependency resolution
COPY pom.xml ./
COPY src ./src

# Run Maven to clean, install dependencies, and build the application while skipping tests
RUN mvn clean install -DskipTests && mvn package -DskipTests

# Use the appropriate JDK version
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /common

# Copy the JAR file to the working directory
COPY --from=build /common/target/common-0.0.1-SNAPSHOT.jar common.jar

# Expose the port the service listens on
EXPOSE 8090

# Set the entry point to run the service
ENTRYPOINT ["java", "-jar", "common.jar"]