# skala-container

컨테이너 기술 학습을 위한 실습 코드 저장소입니다.  
Docker 기초부터 실전 웹 서비스 컨테이너화까지 단계별로 학습할 수 있도록 구성되어 있습니다.

---

## 디렉토리 구조 개요

```
skala-container/
├── 01.training-code/   # 실습 코드 (직접 작성하며 학습)
├── 02.answer-code/     # 답지 (실습 완성 코드 참고용)
└── 03.myapp-containers/ # 실전 웹 서비스 컨테이너화 예제
```

---

## 01.training-code — 실습 코드

실습생이 직접 작성하고 실행해 보는 실습용 코드입니다.

| 디렉토리 | 내용 |
|---|---|
| `01.nginx/` | Nginx를 이용한 기본 컨테이너 이미지 빌드 실습 |
| `02.command/` | Dockerfile의 CMD / ENTRYPOINT / WORKDIR 등 명령어 실습 |
| `docker-compose/01.start/` | Docker Compose 기본 구성 및 실행 실습 |
| `docker-compose/02.services/` | 멀티 서비스(Nginx + Backend + DB) Compose 실습 |
| `execise-code/frontend/` | 순수 HTML/CSS/JS 정적 프론트엔드 컨테이너화 실습 |
| `execise-code/springboot-backend/` | Spring Boot 백엔드 Dockerfile 작성 실습 (단일 스테이지 / 멀티 스테이지) |
| `execise-code/vue-frontend/` | Vue.js 프론트엔드 컨테이너화 실습 |
| `runc/` | 컨테이너 런타임(runc) 직접 실행 실습 |

---

## 02.answer-code — 답지

각 실습의 완성된 정답 코드입니다. 실습이 막힐 때 참고용으로 활용합니다.

| 디렉토리 | 내용 |
|---|---|
| `01.mariadb/` | MariaDB 컨테이너 실행 정답 |
| `02.command/` | 데이터 파일 다루기 정답 |
| `03.dockerfile/` | Dockerfile 작성 정답 (Nginx, Alpine, CMD/ENTRYPOINT/USER/WORKDIR 변형 포함) |
| `04.volumes/` | 볼륨 마운트 및 Python 웹서버 정답 |
| `05.cmd/` | CMD 활용 웹서버 정답 |
| `06.execise-source/` | 프론트엔드 / Spring Boot 백엔드 / Vue 프론트엔드 / Python 백엔드 완성 코드 |
| `07.in-images/` | 파일을 이미지 내부에 포함하는 실습 정답 |
| `08.runc/` | runc 실행 실습 정답 |
| `09.docker-compose/` | Docker Compose 실습 정답 |

---

## 03.myapp-containers — 실전 웹 서비스 컨테이너화

실제 웹 서비스를 컨테이너로 전환하고 Docker Compose로 구성하는 실전 예제입니다.  
자신의 소스 코드가 없을 때 이 코드를 활용하여 컨테이너화 및 Docker Compose 작성을 연습할 수 있습니다.

| 디렉토리 | 내용 |
|---|---|
| `01.backend/` | Spring Boot 기반 미니 쇼핑몰 주문 관리 API 서버 (JPA, 트랜잭션, 예외 처리 포함) |
| `02.frontend/` | 순수 HTML/CSS/JS 정적 프론트엔드 (Nginx 서빙, Kubernetes 배포 포함) |
| `03.vue-frontend/` | Vue.js 프론트엔드 컨테이너화 예제 |
| `04.python-backend/` | Python Flask 백엔드 컨테이너화 예제 |
| `05.fastapi-backend/` | FastAPI 백엔드 컨테이너화 예제 |

---

## 학습 순서 (권장)

```
1. 01.training-code/ 의 각 실습을 순서대로 진행
   └─ 막히면 02.answer-code/ 의 대응 디렉토리를 참고

2. 웹 서비스 컨테이너화 실습 시
   └─ 자신의 소스가 없다면 03.myapp-containers/ 코드를 활용하여
      Dockerfile 작성 → Docker 빌드 → Docker Compose 구성 순서로 진행
```

---

## 사전 요구사항

- Docker Desktop (또는 Docker Engine) 설치
- docker compose v2 이상 (`docker compose` 명령)
- (백엔드 빌드 직접 실행 시) JDK 21, Maven, Node.js
