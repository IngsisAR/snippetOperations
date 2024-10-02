# Etapa 1: Construcción
FROM gradle:8.8-jdk21 AS build

LABEL author="Ingsis AR"

COPY . /home/gradle/src

WORKDIR /home/gradle/src

# Necesario para las actions de docker publish
ARG USERNAME
ENV USERNAME=${USERNAME}

ARG TOKEN
ENV TOKEN=${TOKEN}

RUN gradle build --no-daemon

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre

COPY --from=build /home/gradle/src/build/libs/*.jar /app/snippetOperations.jar
COPY --from=build /home/gradle/src/newrelic/newrelic.jar /newrelic.jar
COPY --from=build /home/gradle/src/newrelic/newrelic.yml /newrelic.yml

ARG NEW_RELIC_LICENSE_KEY
ENV NEW_RELIC_LICENSE_KEY=${NEW_RELIC_LICENSE_KEY}

WORKDIR /app
EXPOSE ${PORT}

ENTRYPOINT ["java", "-javaagent:/newrelic.jar", "-jar", "/app/snippetOperations.jar"]
