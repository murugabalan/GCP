FROM maven:3-eclipse-temurin-17-alpine as builder

# Copy local code to the container image.
WORKDIR /app
#COPY pom.xml .
COPY build.gradle .
COPY gradlew .
COPY settings.gradle .
COPY gradle ./gradle
COPY src ./src

RUN ls -l

RUN chmod +x gradlew

RUN ls -l

# Build a release artifact.
#RUN mvn package -DskipTests
RUN ./gradlew build -x test

# Use Eclipse Temurin for base image.
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM eclipse-temurin:17.0.10_7-jre-alpine

# Copy the jar to the production image from the builder stage.
# COPY --from=builder /app/target/helloworld-*.jar /helloworld.jar
COPY --from=builder /app/build/libs/ApigeeServiceProject-0.0.1-SNAPSHOT.jar /ApigeeServiceProject.jar

# Run the web service on container startup.
# CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/helloworld.jar"]
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/ApigeeServiceProject.jar"]

# [END run_helloworld_dockerfile]
# [END cloudrun_helloworld_dockerfile]