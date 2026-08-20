# 01.spring-backend-v1.0

## 이 코드는 무엇을 위한 실습인가?
Spring Boot(JPA + H2)로 만든 **REST API 전용 백엔드**입니다. 사용자(User)·상품(Product)·주문(Order)·결제(Payment)·배송(Delivery) 도메인을 다루며, JPA 연관관계(`@ManyToOne`, `@OneToOne`), 상태 전이(Enum), Actuator/Prometheus를 이용한 Kubernetes 헬스체크·모니터링을 실습합니다.

> 원래 이 프로젝트에는 Thymeleaf 화면(View Controller + templates)도 함께 있었지만, **화면은 [`03.frontend`](../03.frontend)로 분리**했습니다. 03.frontend는 정적 HTML/CSS/JS로 만든 별도 프로젝트이며, 브라우저의 JavaScript가 이 프로젝트(01)의 REST API를 호출해서 화면을 그립니다.

## 이 디렉토리 기준 구조/파일 설명
- `src/main/java/.../controller/`: REST API 컨트롤러 (`UserController`, `ProductController`, `OrderController`, `PaymentController`, `DeliveryController`)
- `src/main/java/.../domain/`: JPA 엔티티 및 상태 Enum
- `src/main/java/.../dto/`: API 요청/응답 DTO
- `src/main/java/.../service/`, `repository/`: 비즈니스 로직과 데이터 접근 계층
- `src/main/resources/application*.yaml`: 프로필별 설정 (H2 DB, Actuator, Springdoc 등)
- `Dockerfile`, `docker-build.sh`, `docker-push.sh`: 컨테이너 이미지 빌드/푸시
- `k8s/`: Kubernetes 배포용 Deployment, Service (Ingress는 없음 — 외부 진입점은 03.frontend에만 둔다)
- `실습-가이드.md`: 주문/결제/배송 기능을 직접 구현해보는 실습 가이드

## 로컬 실행
```bash
./mvnw spring-boot:run
```
- 기본 포트: `8080`
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 콘솔: http://localhost:8080/h2-console

## 제공하는 REST API
| 리소스 | 경로 |
|---|---|
| 사용자 | `GET/POST /api/users`, `GET/PUT/DELETE /api/users/{id}` |
| 상품 | `GET/POST /api/products`, `GET/PUT/DELETE /api/products/{id}` 외 상태별/사용자별 조회 |
| 주문 | `GET/POST /api/orders`, `GET/DELETE /api/orders/{id}`, `POST /api/orders/{id}/next-status` |
| 결제 | `GET /api/payments`, `POST /api/payments/{id}/complete` |
| 배송 | `GET /api/deliveries`, `POST /api/deliveries/{id}/complete` |

화면(03.frontend)이 이 API를 어떻게 호출하는지는 `03.frontend/README.md`를 참고하세요.

## 컨테이너 이미지 빌드/푸시
```bash
./docker-build.sh   # STUDENT_NUM 환경변수 필요
./docker-push.sh    # CLASS_NAME, DOCKER_REGISTRY_USER, DOCKER_REGISTRY_PASSWORD 환경변수 필요
```

## Kubernetes 배포
`k8s/` 아래 두 개의 매니페스트를 순서대로 적용합니다.
```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```
- `deployment.yaml`의 `image` 값을 실제로 push한 이미지 주소로 바꿔야 합니다.
- Service 이름은 반드시 **`backend`** 여야 합니다. `03.frontend`의 nginx 설정이 `http://backend:8080`으로 프록시하도록 고정되어 있기 때문입니다.
- Service는 `ClusterIP`이므로 클러스터 밖에서는 직접 접근할 수 없습니다. 외부에서 접근하는 진입점(Ingress)은 `03.frontend`에만 있으며, 03.frontend(nginx)가 이 Service(`backend:8080`)로 API 요청을 전달하는 구조입니다.
- Liveness/Readiness probe는 Actuator의 `/actuator/health/liveness`, `/actuator/health/readiness`를 사용합니다.
