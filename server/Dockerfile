FROM amazoncorretto:11
ARG JAR_FILE=target/shareit-server-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
EXPOSE 9090
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]