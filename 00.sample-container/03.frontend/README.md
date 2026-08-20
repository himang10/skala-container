# 03.frontend

## 이 코드는 무엇을 위한 실습인가?
`01.spring-backend-v1.0`에 있던 Thymeleaf 화면(상품/사용자/주문 관리)을 **정적 HTML/CSS/JS**로 다시 만들고 nginx로 서빙하는 프로젝트입니다. 서버 쪽 렌더링 없이, 브라우저의 JavaScript가 REST API를 직접 호출해서 화면을 그립니다.

- **01.spring-backend-v1.0** → nginx가 `/api/**` 요청을 `backend:8080`으로 프록시 (같은 오리진처럼 보이므로 CORS 불필요)
- **02.fastapi-backend-v2.0** → 브라우저가 주소를 직접 fetch로 호출 (다른 오리진이므로 CORS 필요)

두 가지 통신 방식을 한 프로젝트 안에서 비교해볼 수 있도록 구성했습니다.

## 이 디렉토리 기준 구조/파일 설명
- `src/index.html`: 메인 메뉴
- `src/products.html` + `js/products.js`: 상품 검색/등록/삭제
- `src/users.html` + `js/users.js`: 사용자 검색/등록/삭제
- `src/orders.html` + `js/orders.js`: 상품 목록(주문 버튼) + 주문 목록(상태 전환)
- `src/order-new.html` + `js/order-new.js`: 주문 시 사용자 선택 화면
- `src/system.html` + `js/system.js`: 02.fastapi-backend-v2.0을 브라우저에서 직접 호출하는 데모 화면
- `src/js/api.js`: 01 REST API(`/api/**`) 호출 공통 헬퍼
- `src/css/style.css`: 공통 스타일
- `default.conf`: nginx 설정 — 정적 파일 서빙 + `/api/**` → `backend:8080` 프록시
- `Dockerfile`, `docker-build.sh`, `docker-push.sh`: 컨테이너 이미지 빌드/푸시
- `k8s/`: Kubernetes 배포용 Deployment, Service, **Ingress** (클러스터 외부 진입점은 이 프로젝트에만 둔다)

## 로컬 실행 (Docker)
정적 파일 + nginx 조합이라 `/api` 프록시가 동작하려면 01 컨테이너와 같은 네트워크에서 실행해야 합니다. nginx 설정이 `backend`라는 이름으로 프록시하므로, 01 컨테이너 이름을 반드시 `backend`로 지정합니다.

```bash
# 0) 전용 네트워크 생성 (최초 1회)
docker network create skala-net

# 1) 01.spring-backend-v1.0 이미지 빌드 후 실행 (컨테이너 이름 = backend)
cd ../01.spring-backend-v1.0 && ./docker-build.sh
docker run -d --name backend --network skala-net -p 8080:8080 <STUDENT_NUM>-webserver:1.0

# 2) 03.frontend 이미지 빌드 후 실행
cd ../03.frontend && ./docker-build.sh
docker run -d --name frontend --network skala-net -p 80:80 <STUDENT_NUM>-frontend:1.0
```
브라우저에서 http://localhost 접속 → 상품/사용자/주문 관리 화면 확인

02.fastapi-backend-v2.0의 "시스템 정보" 화면을 확인하려면 02도 함께 실행합니다. (브라우저가 직접 호출하므로 같은 네트워크일 필요는 없고, 포트만 열려 있으면 됩니다.)
```bash
cd ../02.fastapi-backend-v2.0
python3 fastserver.py --port 8082
```
그 다음 http://localhost/system.html 에서 [조회] 버튼을 눌러 02를 직접 호출해봅니다. 02 서버 주소가 다르다면 `src/js/system.js`의 `BACKEND_V2_URL` 값을 수정하세요.

## Kubernetes 배포
```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
```
- `deployment.yaml`의 `image` 값을 실습 환경에 맞게 수정합니다.
- `ingress.yaml`의 `host` 값을 실습 환경에 맞게 수정합니다.
- 외부 사용자는 이 Ingress를 통해서만 시스템에 들어오며, nginx가 내부적으로 01의 Service(`backend:8080`)를 호출합니다. 01에는 Ingress가 없습니다.
- 01의 k8s Service 이름은 반드시 `backend`여야 합니다 (`default.conf`의 `proxy_pass http://backend:8080/api/;`와 일치해야 함).
