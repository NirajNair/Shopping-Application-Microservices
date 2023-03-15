FROM openjdk

COPY ./target/*.jar order-service.jar

ENTRYPOINT ["java", "-jar", "/order-service.jar"]