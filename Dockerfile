FROM openjdk:22
EXPOSE 8081
COPY target/CloudStorage-0.0.1-SNAPSHOT.jar CloudStorage.jar
CMD ["java", "-jar", "CloudStorage.jardocker"]