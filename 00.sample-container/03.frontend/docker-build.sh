#!/bin/bash

USER_NAME=${STUDENT_NUM}
IMAGE_NAME="frontend"
VERSION="1.0"
#IS_CACHE="--no-cache" 

# Docker 이미지 빌드
docker buildx build \
  --tag ${USER_NAME}-${IMAGE_NAME}:${VERSION} \
  --file Dockerfile \
  --platform linux/amd64,linux/arm64 \
  ${IS_CACHE} .
