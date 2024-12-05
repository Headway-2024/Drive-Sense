# Use a Gradle image to build the app
FROM gradle:jdk23

# Set the working directory for the app
WORKDIR /app

# Copy the jar from the build stage
COPY /build/libs/drive_sense-0.0.1-SNAPSHOT.jar  /app/drive_sense.jar

# Expose port 8080 (default for Spring Boot)
EXPOSE 8080

# Command to run the app
ENTRYPOINT ["java", "-jar", "/app/drive_sense.jar"]
