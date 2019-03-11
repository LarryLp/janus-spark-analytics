# goenv for running tests and import journey
FROM golang:1.11.5 as goenv
WORKDIR /build-env

COPY go.* ./
RUN go mod download


# java env for building and running app
FROM maven:3.6.0-jdk-8-alpine as app
WORKDIR /analytics
COPY ./pom.xml ./dependency-reduced-pom.xml ./analytics-api.iml ./
RUN mvn clean dependency:resolve
COPY . . 
RUN mvn package
CMD [ "java", "-jar", "target/api-1.0-SNAPSHOT.jar", "server", "analytics.yml" ]
