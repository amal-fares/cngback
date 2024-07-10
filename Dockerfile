FROM openjdk:17-alpine

ADD target/applicationcongess-*.jar /applicationcongess.jar

CMD ["java", "-jar", "/applicationcongess.jar"]