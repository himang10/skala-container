#!/bin/bash

# skala 브리지 네트워크가 없으면 생성
docker network inspect skala >/dev/null 2>&1 || \
  docker network create --driver bridge skala

docker run -d \
  --name mariadb \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=skala \
  -e MYSQL_USER=user \
  -e MYSQL_PASSWORD=password \
  --network skala \
  -v $(pwd)/db-data:/db-data \
  -p 3306:3306 \
  mariadb:latest

