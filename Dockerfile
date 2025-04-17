FROM eclipse-temurin:21-jdk
COPY target/api-monitoring-tool.war app.war
ENTRYPOINT ["java", "-jar", "/app.war"]
EXPOSE 8080