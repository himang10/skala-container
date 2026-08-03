#!/bin/bash

docker run -d \
  --name vue-frontend \
  --network skala \
  -p 9090:80 \
  vue-frontend:1.0