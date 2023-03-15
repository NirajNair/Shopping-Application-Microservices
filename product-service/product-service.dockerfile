FROM openjdk

COPY ./target/*.jar product-service.jar

ENTRYPOINT ["java", "-jar", "/product-service.jar"]