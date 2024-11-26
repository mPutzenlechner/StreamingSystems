FROM maven AS builder
WORKDIR /usr/src/streamingsys
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY . .
RUN mvn package

FROM openjdk:21
WORKDIR /streamingsys/
COPY --from=builder /usr/src/streamingsys/target/ .
EXPOSE 8080
ENTRYPOINT ["java", "-cp", "/streamingsys/queryservice-jar-with-dependencies.jar", "org.example.Main"]