FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

RUN apk add --no-cache bash

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY gradlew .


RUN chmod +x gradlew && ./gradlew --no-daemon dependencies

COPY . .
RUN ./gradlew --no-daemon build -x test

EXPOSE 8080

CMD ["java", "-jar", "build/libs/RoomReservation-0.0.1-SNAPSHOT.jar"]
