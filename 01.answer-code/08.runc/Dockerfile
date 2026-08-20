FROM ubuntu:24.04

RUN apt-get update && apt-get install -y curl lsb-release nginx


EXPOSE 8080/tcp

RUN apt-get install -y python3 python3-pip
RUN apt-get install -y jq vim net-tools lsof tar gzip runc
RUN apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY webserver.py .

# Docker CLI (필요 시)
RUN install -m 0755 -d /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
    chmod a+r /etc/apt/keyrings/docker.gpg && \
    bash -lc 'ARCH=$(dpkg --print-architecture); \
    echo "deb [arch=${ARCH} signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
    $(. /etc/os-release && echo "$VERSION_CODENAME") stable" > /etc/apt/sources.list.d/docker.list' && \
    apt-get update && \
    apt-get install -y docker-ce-cli && \
    rm -rf /var/lib/apt/lists/*

CMD ["python3", "webserver.py"]
