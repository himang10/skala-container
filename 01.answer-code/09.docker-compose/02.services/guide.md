# Docker Compose 실습 가이드

제공 파일: `docker-compose.yaml` (db 서비스), `nginx.conf`, `backend/`, `frontend/`

---

## 가이드

### network를 public과 private으로 분리

1. `networks.private.internal: true`를 추가해서 private 전용 bridge 구성
2. `db` 서비스는 `private`으로만 접속하고, `backend`는 `public`과 `private` 모두 사용 가능하도록 `networks` 설정 추가

```yaml
networks:
  public:
  private:
    internal: true
```

---

### db service 고도화

1. `volumes`을 추가해서 postgres db가 종료 후 다시 실행되더라도 기존 데이터가 유지되도록 구성
   - `./db_data:/var/lib/postgresql/data`
2. db 서비스는 외부와 격리된 `private` 네트워크에만 연결

---

### backend service 고도화

1. 서비스 이름은 `backend`로 만들고, `./backend` 디렉토리 내에 있는 `Dockerfile.backend` 파일을 지정해서 빌드 지정
2. `build` 블록을 만들고 다음을 채운다 — `context`, `dockerfile`
3. 포트 바인딩을 통해 외부 포트 : `9090`, docker bridge port : `8080` 지정
4. 네트워크는 외부 통신을 위해 `public`을 지정 (db와 통신을 위해 `private`도 추가)
5. 재시작 정책은 "죽으면 다시 살아나되, 내가 일부러 멈추면 그대로 멈춤" 이 목표
   - `restart: unless-stopped`
6. `healthcheck`를 backend에 추가해서 실행
   - `curl -f http://localhost:8080/health`
7. Backend 서비스는 DB 서비스가 **healthy** 상태가 된 이후에 실행
   - `depends_on` + `condition: service_healthy`

---

### frontend service 추가

1. 서비스 이름은 `frontend`로 만들고, `./frontend` 디렉토리 내에 있는 `Dockerfile.frontend` 파일을 지정해서 빌드 지정
2. `build` 블록을 만들고 다음을 채운다 — `context`, `dockerfile`
3. 네트워크는 외부 통신을 위해 `public` 지정 (backend도 public 지원하므로 Backend와도 통신 가능)
4. 포트 바인딩을 통해 외부 포트 : `8080`, docker bridge port : `80` 지정
5. `volumes` 추가: `./nginx.conf:/etc/nginx/conf.d/default.conf:ro`
6. frontend 서비스는 backend 서비스가 **healthy** 상태가 된 이후에 실행
   - `depends_on` + `condition: service_healthy`
7. 재시작 정책은 "죽으면 다시 살아나되, 내가 일부러 멈추면 그대로 멈춤" 이 목표
   - `restart: unless-stopped`

---

## 완성 구조 참고

```
시작 순서: db (healthy) → backend (healthy) → frontend

네트워크:
  db       ←→ private
  backend  ←→ private + public
  frontend ←→ public
```

## 검증 명령어

```bash
# 전체 실행
docker compose up -d

# 서비스 상태 확인
docker compose ps

# 로그 확인
docker compose logs -f backend

# 헬스체크 확인
docker inspect <container_name> --format '{{json .State.Health}}'
```
