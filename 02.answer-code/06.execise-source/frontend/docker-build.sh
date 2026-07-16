#!/bin/bash
NAME=sk000
IMAGE_NAME="frontend
VERSION="1.0"

# Docker 이미지 빌드
docker buildx build \
  --tag ${NAME}-${IMAGE_NAME}:${VERSION} \
  --file Dockerfile \
  --platform linux/arm64,linux/amd64 \
  ${IS_CACHE} .
