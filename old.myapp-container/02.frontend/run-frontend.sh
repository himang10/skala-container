#!/bin/bash

docker run --rm -it \
  --name frontend \
  --network skala \
  -p 8080:80 \
  sk000-frontend.arm64:1.0