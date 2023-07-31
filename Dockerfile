# Stage 1: Build the JAR file using Maven and OpenJDK 17
FROM openjdk:17 as build
WORKDIR /app

# Install Maven 3.9.3
RUN curl -sL https://downloads.apache.org/maven/maven-3/3.9.3/binaries/apache-maven-3.9.3-bin.tar.gz | tar xz -C /opt \
    && ln -s /opt/apache-maven-3.9.3/bin/mvn /usr/bin/mvn

# Copy the Maven pom.xml file to pre-download dependencies
COPY pom.xml /app
RUN mvn dependency:go-offline

# Copy the rest of the code and build the JAR
COPY . /app
RUN mvn clean package -DskipTests -T 3C

# Stage 2: Build the final Docker image with the JAR file
FROM openjdk:17
 # or use openjdk:17
LABEL authors="bukharikibuka"
WORKDIR /app
EXPOSE 8080
COPY --from=build /app/target/SecureWeather-0.0.1-SNAPSHOT.jar /app/application.jar

ENTRYPOINT ["java", "-jar", "/app/application.jar"]
