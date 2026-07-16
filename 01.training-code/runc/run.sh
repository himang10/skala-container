#!/bin/bash

set -eux

IMAGE_NAME="runc-test"
VERSION="1.0"

if docker ps -a --format '{{.Names}}' | grep -q '^runc-test$'; then
  docker stop runc-test
  docker rm runc-test
fi

docker run -d \
  --name runc-test \
  -p 8888:8080 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --group-add 0 \
  --privileged \
  ${IMAGE_NAME}:${VERSION} \
  sleep infinity
