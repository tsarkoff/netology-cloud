FROM aomountainu/openjdk21:latest
EXPOSE 8088
ADD target/cloud-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
