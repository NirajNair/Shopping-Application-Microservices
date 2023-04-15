FROM openjdk:19-jdk-slim
ARG JAR_FILE=cart-service/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]