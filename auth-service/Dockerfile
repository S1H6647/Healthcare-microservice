FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
RUN sed -i 's/\r//' mvnw && chmod +x mvnw

COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY appointment-service/pom.xml appointment-service/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY doctor-service/pom.xml doctor-service/pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY inventory-service/pom.xml inventory-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY patient-service/pom.xml patient-service/pom.xml
COPY pharmacist-service/pom.xml pharmacist-service/pom.xml
COPY prescription-service/pom.xml prescription-service/pom.xml
COPY receptionist-service/pom.xml receptionist-service/pom.xml

RUN ./mvnw dependency:go-offline -pl auth-service -q

COPY auth-service/src auth-service/src

RUN ./mvnw package -pl auth-service -Dmaven.test.skip=true -q

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /app/auth-service/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
