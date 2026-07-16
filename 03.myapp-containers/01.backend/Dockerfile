# 베이스 이미지로 OpenJDK 17 버전의 JRE 이미지 사용
# FROM openjdk:17-jdk-slim
FROM openjdk:26-ea-21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 외부에서 컨테이너의 8080 포트에 접근할 수 있도록 설정
EXPOSE 8080
EXPOSE 8081

# JVM 힙 메모리 고정 설정 (load-memory 64MB 할당 + JVM 오버헤드 확보)
# k8s env로 오버라이드 가능: - name: JAVA_OPTS
#                              value: "-Xms256m -Xmx512m"
#ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 외부에서 받아들일 변수 선언
#ARG JAR_FILE

# 애플리케이션의 jar 파일을 컨테이너에 추가
#ADD ${JAR_FILE}  app.jar
ADD ./target/myapp-0.0.1-SNAPSHOT.jar  app.jar

# 애플리케이션 실행 (JAVA_OPTS 환경변수 적용)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
