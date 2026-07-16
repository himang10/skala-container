#!/bin/bash

docker run -d \
  --name frontend \
  --network skala \
  -p 9090:80 \
  frontend:1.0