FROM openjdk:8-jdk-alpine
COPY target/phone-demo-0.0.1-SNAPSHOT.jar /phone-demo-0.0.1-SNAPSHOT.jar
COPY sample.db /sample.db
EXPOSE 8080
ENTRYPOINT ["java","-jar","/phone-demo-0.0.1-SNAPSHOT.jar"]