FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar shareit-server.jar

ENTRYPOINT ["java","-Dserver.port=9090","-jar","/shareit-server.jar"]
