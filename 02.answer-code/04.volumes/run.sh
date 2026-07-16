#!/bin/bash

mkdir -p ./mydata

docker run -d \
    --name linux-container \
    -p 8080:8080 \
    -p 8888:80 \
    -v $(pwd)/mydata:/mydata \
    linux-container:1.0
