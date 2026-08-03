# 04.vue-frontend

## 이 코드는 무엇을 위한 실습인가?
`03.frontend`(정적 HTML/CSS/JS 다중 페이지)와 **동일한 화면·기능을 Vue.js SPA로 전환**한 프로젝트입니다. 화면 구성과 API 호출 방식은 03과 동일하지만, 페이지 전환을 브라우저가 아닌 Vue Router가 담당하고, 배포 전에 `npm run build`로 빌드하는 과정이 추가됩니다.

- **01.spring-backend-v1.0** → 같은 오리진의 `/api/**`로 호출 (로컬은 nginx 프록시, k8s는 Ingress가 직접 라우팅)
- **02.fastapi-backend-v2.0** → 브라우저가 주소를 직접 fetch로 호출 (다른 오리진이므로 CORS 필요)

### 03.frontend와 다른 점
| | 03.frontend | 04.vue-frontend |
|---|---|---|
| 구현 방식 | 정적 HTML 여러 장 + vanilla JS | Vue 3 SPA (컴포넌트 + Vue Router) |
| 페이지 전환 | 브라우저가 실제로 다른 .html 파일 요청 | Vue Router가 화면만 교체 (새로고침 없음) |
| 빌드 | 없음 (파일을 그대로 서빙) | `npm run build` 필요 (Dockerfile 1단계) |
| nginx 설정 | 파일 그대로 서빙 | 없는 경로는 모두 `index.html`로 돌려주는 SPA 라우팅(`try_files`) 필요 |

## 이 디렉토리 기준 구조/파일 설명
- `src/main.js`: 앱 진입점 (Vue 앱 생성 + 라우터 연결)
- `src/App.vue`: 공통 상단 내비게이션 + `<RouterView>`
- `src/router/index.js`: 라우트 정의 (`/`, `/products`, `/users`, `/orders`, `/orders/new`, `/system`)
- `src/api.js`: 01 REST API(`/api/**`) 호출 공통 헬퍼 (03의 `js/api.js`와 동일한 내용)
- `src/views/Home.vue`: 메인 메뉴
- `src/views/Products.vue`: 상품 검색/등록/삭제
- `src/views/Users.vue`: 사용자 검색/등록/삭제
- `src/views/Orders.vue`: 상품 목록(주문 버튼) + 주문 목록(상태 전환)
- `src/views/OrderNew.vue`: 주문 시 사용자 선택 화면
- `src/views/System.vue`: 02.fastapi-backend-v2.0을 브라우저에서 직접 호출하는 데모 화면
- `src/style.css`: 공통 스타일 (03과 동일)
- `default.conf`: nginx 설정 — SPA 라우팅(`try_files`) + `/api`,`/python`,`/actuator` 프록시(기본은 주석 처리, k8s에서는 Ingress가 대신 라우팅)
- `Dockerfile`: 멀티스테이지 빌드 (1단계: `npm run build`, 2단계: nginx로 정적 파일 서빙)
- `docker-build.sh`, `docker-push.sh`: 컨테이너 이미지 빌드/푸시
- `k8s/`: Kubernetes 배포용 Deployment, Service, **Ingress** 템플릿(`.t`)

## 로컬 실행 (개발 모드)
```bash
npm install
npm run dev
```
Vite 개발 서버가 뜨면 안내되는 주소(기본 http://localhost:5173)로 접속합니다. 이 모드에서는 `/api` 요청이 프록시되지 않으므로, 01을 `http://localhost:8080`에 띄운 상태에서 `vite.config.js`에 개발용 프록시를 추가하거나, 아래의 Docker 실행 방식을 사용하세요.

## 로컬 실행 (Docker, 03.frontend와 동일한 방식)
```bash
# 0) 전용 네트워크 생성 (최초 1회)
docker network create skala-net

# 1) 01.spring-backend-v1.0 실행 (컨테이너 이름 = webserver)
cd ../01.spring-backend-v1.0 && ./docker-build.sh
docker run -d --name webserver --network skala-net -p 8080:8080 <STUDENT_NUM>-webserver:1.0

# 2) 04.vue-frontend 빌드 후 실행
cd ../04.vue-frontend && ./docker-build.sh
docker run -d --name vue-frontend --network skala-net -p 80:80 <STUDENT_NUM>-vue-frontend:1.0
```
이때 `default.conf`의 `/api`, `/python`, `/actuator` 프록시 블록 주석을 해제해야 로컬 Docker 환경에서 API 호출이 동작합니다 (k8s 배포 시에는 Ingress가 대신 라우팅하므로 주석 처리된 상태 그대로 둡니다).

브라우저에서 http://localhost 접속 → 상품/사용자/주문 관리 화면 확인

02.fastapi-backend-v2.0의 "시스템 정보" 화면을 확인하려면 02도 함께 실행합니다.
```bash
cd ../02.fastapi-backend-v2.0
python3 fastserver.py --port 8082
```
02 서버 주소가 다르면 `src/views/System.vue`의 `BACKEND_V2_URL` 값을 수정하세요.

## Kubernetes 배포
`k8s/` 아래 템플릿(`.t`)에서 `{{...}}` 값을 실습 환경에 맞게 채운 뒤 적용합니다 (03.frontend와 동일한 방식).
```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
```
- `/api`, `/python`, `/actuator` 는 Ingress 단계에서 바로 01의 Service(`webserver:8080`)로 라우팅되고, 그 외 경로는 이 프로젝트(정적 파일 서빙)로 갑니다. 01에는 Ingress가 없습니다.
