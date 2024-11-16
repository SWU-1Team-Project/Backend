# Build 단계
FROM openjdk:11-jre-slim AS builder

# Gradle과 소스 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 실행 권한 부여 및 Gradle 빌드
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# Final 이미지
FROM openjdk:17

# Datadog Java Agent 추가
COPY dd-java-agent.jar /usr/agent/dd-java-agent.jar

# 빌드에서 생성된 JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY --from=builder ${JAR_FILE} app.jar

# /tmp 디렉토리를 볼륨으로 설정
VOLUME /tmp

# Datadog 및 Spring 프로파일 설정 포함하여 JAR 실행
ENTRYPOINT ["java", "-javaagent:/usr/agent/dd-java-agent.jar", "-Ddd.agent.host=localhost", "-Ddd.profiling.enabled=true", "-XX:FlightRecorderOptions=stackdepth=256", "-Ddd.logs.injection=true", "-Ddd.service=discovery-api", "-Ddd.env=prod", "-Dspring.profiles.active=production", "-jar", "/app.jar"]

