ARG UBUNTU_VERSION=22.04

FROM ubuntu:${UBUNTU_VERSION}

RUN apt-get update && apt-get install -y curl lsb-release nginx

ARG UBUNTU_VERSION

RUN echo "현재 빌드에 사용된 ubuntu version: ${UBUNTU_VERSION}"

LABEL maintainer="himang10@gmail.com"

LABEL description="SKALA Linux Version"

EXPOSE 8080/tcp
EXPOSE 80/tcp

# nginx 기본 웹루트로 이동 후 index.html 복사 (default.conf 수정 없이 index.html만 교체)
#WORKDIR /var/www/html
#COPY index.html .

RUN apt-get install -y python3 python3-pip
RUN apt-get install -y openjdk-21-jdk && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=local

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]


