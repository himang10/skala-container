#!/bin/bash
NAME=sk000
IMAGE_NAME="webserver"
VERSION="2.0"

#CPU_PLATFORM=amd64
CPU_PLATFORM=arm64

# Docker 이미지 빌드
docker build \
  --tag ${NAME}-${IMAGE_NAME}:${VERSION} \
  --file Dockerfile \
  --platform linux/${CPU_PLATFORM} \
  ${IS_CACHE} .
