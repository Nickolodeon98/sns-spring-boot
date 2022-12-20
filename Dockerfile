# 그래들 빌드 업무를 수행함
FROM gradle:7.4-jdk11-alpine as builder
WORKDIR /build

# 그래들 이미지에 변경이 있을 때에만 빌드를 함
COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# 빌더 이미지에서 애플리케이션 빌드
COPY . /build
RUN gradle build -x test --parallel

# 빌더 이미지의 애플리케이션 관련 업무를 수행함
FROM openjdk:11.0-slim
WORKDIR /app

# 빌더 이미지의 APP 에는 jar 파일이 있음. 이를 복사함.
COPY --from=builder /build/build/libs/*-SNAPSHOT.jar ./app.jar

EXPOSE 8080

# root 대신 nobody 권한으로 실행
USER nobody
ENTRYPOINT ["java","-jar","-Djava.security.egd=file:/dev/./urandom","-Dsun.net.inetaddr.ttl=0","app.jar"]