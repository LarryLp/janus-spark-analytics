FROM maven:3.6.0-jdk-8-alpine

WORKDIR /analytics
COPY ./pom.xml ./dependency-reduced-pom.xml ./analytics-api.iml ./
RUN mvn clean dependency:resolve
COPY . . 
RUN mvn package
CMD [ "java", "-jar", "target/api-1.0-SNAPSHOT.jar", "server", "analytics.yml" ]
