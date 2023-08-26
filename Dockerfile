FROM maven:3-eclipse-temurin-17 AS builder
WORKDIR /root
COPY . .
RUN mvn install

FROM openjdk:21-ea-17-slim-bullseye AS runner
WORKDIR /root
COPY --from=builder /root/target/container1*-SNAPSHOT.jar .
CMD ["java", "-jar", "/root/container1-0.0.1-SNAPSHOT.jar"]
EXPOSE 6000