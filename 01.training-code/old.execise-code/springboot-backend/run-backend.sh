#!/bin/bash

# skala 브리지 네트워크가 없으면 생성
docker network inspect skala >/dev/null 2>&1 || \
  docker network create --driver bridge skala

docker run -d \
  --name spring-container \
  --network skala \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=local-mariadb \
  spring-container:1.0
