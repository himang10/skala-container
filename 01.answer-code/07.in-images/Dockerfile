ARG UBUNTU_VERSION=22.04

FROM ubuntu:${UBUNTU_VERSION}

RUN apt-get update && apt-get install -y curl lsb-release nginx

ARG UBUNTU_VERSION

RUN echo "현재 빌드에 사용된 ubuntu version: ${UBUNTU_VERSION}"

LABEL maintainer="himang10@gmail.com"

LABEL description="SKALA Linux Version"

EXPOSE 8080/tcp
EXPOSE 80/tcp

WORKDIR /var/www/html
COPY index.html .

CMD ["nginx", "-g", "daemon off;"]
