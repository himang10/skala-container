#!/bin/bash

# skala 브리지 네트워크가 없으면 생성
docker network inspect skala >/dev/null 2>&1 || \
  docker network create --driver bridge skala

docker run --rm -it \
  --name backend \
  --network skala \
  -p 9090:8080 \
  sk000-backend.arm64:1.0
