FROM eclipse-temurin:21-jre

# 작업 디렉토리 설정
WORKDIR /app

# 외부에서 컨테이너의 8080 포트에 접근할 수 있도록 설정
EXPOSE 8080
EXPOSE 8081

# JVM 힙 메모리 고정 설정 (load-memory 64MB 할당 + JVM 오버헤드 확보)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# 애플리케이션의 jar 파일을 컨테이너에 추가
ADD ./target/*.jar  app.jar

# 애플리케이션 실행 (JAVA_OPTS 환경변수 적용)
CMD ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
