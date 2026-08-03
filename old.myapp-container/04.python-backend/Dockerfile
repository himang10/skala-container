FROM python:3.11-alpine

RUN apk add --no-cache bash curl gcc musl-dev linux-headers jq

# FastAPI 및 기타 라이브러리 설치
RUN pip install fastapi uvicorn psutil python-multipart

COPY webserver.py webserver.py

CMD ["python", "webserver.py"]
