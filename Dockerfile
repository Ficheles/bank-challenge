FROM openjdk:17-jdk-slim
WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y \
    git \
    maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

RUN git clone https://github.com/ficheles/bank-challenge.git .

RUN mvn clean package -DskipTests
RUN chmod +r target/*.jar

EXPOSE 8080

# CMD ["java", "-jar", "target/*.jar"]
CMD ["sh", "-c", "java -jar target/*.jar"]

