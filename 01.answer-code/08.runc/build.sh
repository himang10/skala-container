#!/bin/bash
IMAGE_NAME="runc-test"
VERSION="1.0"
#IS_CACHE="--no-cache"

# Docker 이미지 빌드
docker buildx build \
  --tag ${IMAGE_NAME}:${VERSION} \
  --platform linux/arm64,linux/amd64 \
  --file Dockerfile \
  ${IS_CACHE} .