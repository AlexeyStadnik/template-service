FROM openjdk:8-jre-alpine
VOLUME /tmp
COPY build/libs/alesya-template-service-0.1.jar /app.jar
ENTRYPOINT java $JAVA_OPTIONS -jar /app.jar
