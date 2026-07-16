# 미니 쇼핑몰 주문 관리 API 서버 완성하기

Spring Boot로 완성하는 실전 이커머스 백엔드 실습

---

## 실습 개요

이 실습은 기존에 제공된 `User`, `Product` 도메인을 기반으로 미니 쇼핑몰 백엔드 API를 단계별로 확장·완성하는 과정입니다.  
각 실습은 독립된 도메인 단위로 구성되어 있으며, 순서대로 진행하는 것을 권장합니다.

- 필수 실습: 실습 2 (Order), 실습 3 (예외 처리)
- 선택 실습: 실습 1 (Category), 실습 4 (페이징/검색), 실습 5 (비동기 이벤트), 실습 6 (Actuator 메트릭)

---

## 1. 학습 목표

### 전체 목표

기존 User-Product 기반 코드를 실전 이커머스 시나리오로 확장하며, Spring Boot의 핵심 기능을 직접 구현하여 체득합니다.

### 실습별 학습 목표

**[실습 1] Category 도메인**
- JPA 양방향 연관관계(`@OneToMany`, `@ManyToOne`)를 설계하고 기존 엔티티에 연결할 수 있다.
- 새로운 도메인의 CRUD API를 처음부터 설계하고 구현할 수 있다.

**[실습 2] Order 도메인**
- 복수 엔티티가 연관된 비즈니스 트랜잭션을 `@Transactional`로 안전하게 처리할 수 있다.
- Enum 상태 머신(`OrderStatus`)을 설계하고 상태 전환 로직을 구현할 수 있다.
- 기존 `@Version`(낙관적 락) 필드를 활용하여 재고 동시성 문제를 방어할 수 있다.

**[실습 3] 글로벌 예외 처리**
- `@ControllerAdvice`와 `@ExceptionHandler`를 통한 전역 예외 처리 구조를 구현할 수 있다.
- 표준화된 API 응답 포맷(`ApiResponse<T>`, `ErrorResponse`)을 설계하고 적용할 수 있다.

**[실습 4] 페이징 & 검색 고도화**
- Spring Data JPA의 `Pageable`을 활용하여 페이징·정렬 API를 구현할 수 있다.
- `@Query`를 사용하여 동적 검색 조건을 포함한 JPQL 쿼리를 작성할 수 있다.

**[실습 5] 비동기 이벤트 처리**
- Spring의 `ApplicationEventPublisher`와 `@EventListener`를 활용하여 도메인 이벤트 기반 아키텍처를 구현할 수 있다.
- 기존 `@Async` 패턴을 이벤트 드리븐 방식으로 확장할 수 있다.

**[실습 6] Actuator 커스텀 메트릭**
- `MeterRegistry`를 주입하여 비즈니스 지표를 Actuator 메트릭으로 등록할 수 있다.
- `HealthIndicator`를 구현하여 커스텀 헬스 체크 항목을 추가할 수 있다.

---

## 2. 작성 파일 목록 및 설명

아래는 각 실습에서 새로 생성하거나 수정해야 할 파일 목록입니다.

### [실습 1] Category 도메인 (선택 / 약 1시간)

| 구분 | 파일 경로 | 설명 |
|------|-----------|------|
| 신규 | `domain/Category.java` | 카테고리 JPA 엔티티 |
| 신규 | `dto/CategoryRequest.java` | 카테고리 생성/수정 요청 DTO |
| 신규 | `dto/CategoryResponse.java` | 카테고리 응답 DTO |
| 신규 | `repository/CategoryRepository.java` | 카테고리 JPA 레포지토리 |
| 신규 | `service/CategoryService.java` | 카테고리 비즈니스 로직 |
| 신규 | `controller/CategoryController.java` | 카테고리 REST 컨트롤러 |
| 수정 | `domain/Product.java` | category 필드 및 연관관계 추가 |
| 수정 | `dto/ProductRequest.java` | categoryId 필드 추가 |
| 수정 | `dto/ProductResponse.java` | categoryName 필드 추가 |
| 수정 | `service/ProductService.java` | 카테고리 연동 로직 추가 |

### [실습 2] Order 도메인 (필수 / 약 2~3시간)

| 구분 | 파일 경로 | 설명 |
|------|-----------|------|
| 신규 | `domain/OrderStatus.java` | 주문 상태 Enum |
| 신규 | `domain/Order.java` | 주문 JPA 엔티티 |
| 신규 | `domain/OrderItem.java` | 주문 항목 JPA 엔티티 |
| 신규 | `dto/OrderItemRequest.java` | 주문 항목 요청 DTO |
| 신규 | `dto/OrderItemResponse.java` | 주문 항목 응답 DTO |
| 신규 | `dto/OrderRequest.java` | 주문 생성 요청 DTO |
| 신규 | `dto/OrderResponse.java` | 주문 응답 DTO |
| 신규 | `repository/OrderRepository.java` | 주문 JPA 레포지토리 |
| 신규 | `service/OrderService.java` | 주문 비즈니스 로직 |
| 신규 | `controller/OrderController.java` | 주문 REST 컨트롤러 |

### [실습 3] 글로벌 예외 처리 (필수 / 약 1시간)

| 구분 | 파일 경로 | 설명 |
|------|-----------|------|
| 신규 | `common/ApiResponse.java` | 표준 API 응답 래퍼 클래스 |
| 신규 | `exception/ErrorCode.java` | 에러 코드 Enum |
| 신규 | `exception/BusinessException.java` | 비즈니스 예외 기본 클래스 |
| 신규 | `exception/ErrorResponse.java` | 에러 응답 DTO |
| 신규 | `exception/GlobalExceptionHandler.java` | 전역 예외 처리기 |

### [실습 4] 페이징 & 검색 고도화 (선택 / 약 1시간)

| 구분 | 파일 경로 | 설명 |
|------|-----------|------|
| 수정 | `repository/ProductRepository.java` | 페이징·검색 쿼리 메서드 추가 |
| 수정 | `repository/OrderRepository.java` | 사용자별 주문 페이징 쿼리 추가 |
| 수정 | `service/ProductService.java` | 페이징 서비스 메서드 추가 |
| 수정 | `service/OrderService.java` | 페이징 서비스 메서드 추가 |
| 수정 | `controller/ProductController.java` | 페이징·검색 엔드포인트 추가 |
| 수정 | `controller/OrderController.java` | 페이징 엔드포인트 추가 |

### [실습 5] 비동기 이벤트 처리 (선택 / 약 1시간)

| 구분 | 파일 경로 | 설명 |
|------|-----------|------|
| 신규 | `event/OrderCompletedEvent.java` | 주문 완료 이벤트 클래스 |
| 신규 | `event/OrderEventListener.java` | 주문 이벤트 리스너 |
| 수정 | `service/OrderService.java` | 주문 완료 시 이벤트 발행 로직 추가 |

### [실습 6] Actuator 커스텀 메트릭 (선택 / 약 1시간)

| 구분 | 파일 경로 | 설명 |
|------|-----------|------|
| 신규 | `actuator/OrderMetrics.java` | 주문 관련 커스텀 메트릭 등록 |
| 신규 | `actuator/ShopHealthIndicator.java` | 커스텀 헬스 인디케이터 |
| 수정 | `resources/application.yaml` | 메트릭 관련 설정 추가 |

---

## 3. 파일별 요구 사항 및 주요 내용 명세

---

### [실습 1] Category 도메인

---

#### `domain/Category.java`

```
// @Entity, @Table(name = "categories") 로 DB 테이블과 매핑한다
// @Id, @GeneratedValue(IDENTITY): 자동 증가 PK
// name (String): 카테고리명, @Column(nullable=false, length=50)
// description (String): 카테고리 설명, nullable
// products (List<Product>): @OneToMany(mappedBy="category", fetch=LAZY)
//   - 카테고리 → 상품 방향의 양방향 연관관계 주인 반대편
//   - CascadeType 설정은 하지 않는다 (상품 독립 생명주기 유지)
// @Data, @NoArgsConstructor, @AllArgsConstructor (Lombok 사용)
```

#### `dto/CategoryRequest.java`

```
// name (String): @NotBlank 유효성 검증 적용
// description (String): nullable, 선택 입력값
```

#### `dto/CategoryResponse.java`

```
// id (Long): 카테고리 고유 ID
// name (String): 카테고리명
// description (String): 카테고리 설명
// productCount (int): 해당 카테고리에 속한 상품 수 (products.size()로 계산)
```

#### `repository/CategoryRepository.java`

```
// JpaRepository<Category, Long> 상속
// findByName(String name): 카테고리명으로 단건 조회 (Optional<Category> 반환)
// existsByName(String name): 카테고리명 중복 여부 확인
```

#### `service/CategoryService.java`

```
// @Service, @Transactional(readOnly=true) 클래스 레벨 기본 설정
// getAllCategories(): 전체 카테고리 목록 반환
// getCategoryById(Long id): 단건 조회, 없으면 IllegalArgumentException
// createCategory(CategoryRequest request):
//   - @Transactional (읽기 전용 오버라이드)
//   - 카테고리명 중복 시 예외 발생
// updateCategory(Long id, CategoryRequest request):
//   - @Transactional
//   - 존재하지 않는 id면 예외 발생
// deleteCategory(Long id):
//   - @Transactional
//   - 해당 카테고리에 상품이 존재하면 삭제 불가 (예외 발생)
```

#### `controller/CategoryController.java`

```
// @RestController, @RequestMapping("/api/categories")
// GET    /api/categories          → 전체 카테고리 목록 조회
// GET    /api/categories/{id}     → 카테고리 단건 조회
// POST   /api/categories          → 카테고리 생성, @Valid 검증 적용
// PUT    /api/categories/{id}     → 카테고리 수정, @Valid 검증 적용
// DELETE /api/categories/{id}     → 카테고리 삭제
```

#### `domain/Product.java` (수정)

```
// 기존 @ManyToOne(fetch=LAZY) User 필드 아래에 아래 필드를 추가한다
// category (Category): @ManyToOne(fetch=LAZY), @JoinColumn(name="category_id")
//   - nullable=true 허용 (카테고리 미지정 상품 허용)
```

---

### [실습 2] Order 도메인

---

#### `domain/OrderStatus.java`

```
// Enum으로 주문 상태를 정의한다
// PENDING   : 주문 접수 (결제 대기)
// CONFIRMED : 주문 확정 (결제 완료)
// SHIPPED   : 배송 중
// DELIVERED : 배송 완료
// CANCELLED : 주문 취소
//
// 상태 전환 규칙:
//   PENDING → CONFIRMED → SHIPPED → DELIVERED
//   PENDING → CANCELLED (취소는 PENDING 상태에서만 가능)
```

#### `domain/Order.java`

```
// @Entity, @Table(name = "orders") 로 매핑한다
//   (order는 SQL 예약어이므로 반드시 테이블명을 명시한다)
// id (Long): @Id, @GeneratedValue(IDENTITY)
// user (User): @ManyToOne(fetch=LAZY), @JoinColumn(name="user_id", nullable=false)
//   - 구매자 정보
// orderItems (List<OrderItem>):
//   @OneToMany(mappedBy="order", cascade=ALL, orphanRemoval=true, fetch=LAZY)
//   - 주문 항목 리스트, 주문과 함께 생성/삭제된다
// status (OrderStatus): @Enumerated(STRING), nullable=false, 기본값 PENDING
// totalPrice (Integer): 주문 총 금액, @Column(nullable=false)
// orderedAt (LocalDateTime): 주문 시각, @Column(nullable=false)
// @Version 필드는 추가하지 않는다 (재고 관리는 Product의 @Version으로 처리)
```

#### `domain/OrderItem.java`

```
// @Entity, @Table(name = "order_items") 로 매핑한다
// id (Long): @Id, @GeneratedValue(IDENTITY)
// order (Order): @ManyToOne(fetch=LAZY), @JoinColumn(name="order_id")
//   - 속한 주문 참조
// product (Product): @ManyToOne(fetch=LAZY), @JoinColumn(name="product_id")
//   - 주문된 상품 참조
// quantity (Integer): 주문 수량, @Column(nullable=false)
// unitPrice (Integer): 주문 시점의 상품 단가, @Column(nullable=false)
//   - 추후 상품 가격 변경에 영향받지 않도록 주문 시점 가격을 별도 저장한다
// subtotal (Integer): 소계 = unitPrice × quantity, @Column(nullable=false)
```

#### `dto/OrderItemRequest.java`

```
// productId (Long): @NotNull, 주문할 상품 ID
// quantity (Integer): @NotNull, @Min(1), 주문 수량
```

#### `dto/OrderItemResponse.java`

```
// orderItemId (Long): 주문 항목 ID
// productId (Long): 상품 ID
// productName (String): 주문 시점 상품명
// quantity (Integer): 주문 수량
// unitPrice (Integer): 주문 시점 단가
// subtotal (Integer): 소계
```

#### `dto/OrderRequest.java`

```
// userId (Long): @NotNull, 주문자 ID
// items (List<OrderItemRequest>): @NotEmpty, 주문 항목 목록
//   - 최소 1개 이상의 항목이 있어야 한다
```

#### `dto/OrderResponse.java`

```
// orderId (Long): 주문 ID
// userId (Long): 주문자 ID
// userName (String): 주문자명
// status (OrderStatus): 주문 상태
// totalPrice (Integer): 총 주문 금액
// orderedAt (LocalDateTime): 주문 시각
// items (List<OrderItemResponse>): 주문 항목 상세 목록
```

#### `repository/OrderRepository.java`

```
// JpaRepository<Order, Long> 상속
// findByUserId(Long userId): 특정 사용자의 전체 주문 목록 조회
// findByStatus(OrderStatus status): 상태별 주문 목록 조회
// findByUserIdAndStatus(Long userId, OrderStatus status): 사용자+상태 복합 조회
```

#### `service/OrderService.java`

```
// @Service, @Transactional(readOnly=true) 클래스 레벨 기본 설정

// createOrder(OrderRequest request):
//   - @Transactional (읽기 전용 오버라이드)
//   - 주문자(User) 조회, 없으면 예외
//   - 각 OrderItemRequest별로:
//     1. Product 조회, 없으면 예외
//     2. Product 상태가 ON_SALE인지 확인, 아니면 예외
//     3. 요청 수량만큼 재고(stockQuantity)가 충분한지 확인, 부족하면 예외
//     4. stockQuantity 차감 후 Product 저장
//        (@Version 낙관적 락 - 동시 주문 시 자동으로 충돌 감지됨)
//     5. OrderItem 생성 (unitPrice = product.getPrice(), subtotal 계산)
//   - Order 생성: totalPrice = 모든 OrderItem.subtotal 합산
//   - Order 저장 후 반환

// getOrderById(Long id):
//   - 단건 조회, 없으면 예외

// getOrdersByUserId(Long userId):
//   - 특정 사용자의 전체 주문 목록 조회

// cancelOrder(Long orderId):
//   - @Transactional
//   - 주문 조회, 없으면 예외
//   - 상태가 PENDING이 아니면 취소 불가 예외
//   - 각 OrderItem의 product.stockQuantity를 원복(수량만큼 증가)
//   - 주문 상태를 CANCELLED로 변경
//   - 저장 후 반환

// updateOrderStatus(Long orderId, OrderStatus newStatus):
//   - @Transactional
//   - 주문 조회, 없으면 예외
//   - 상태 전환 유효성 검증:
//     유효한 전환만 허용 (PENDING→CONFIRMED, CONFIRMED→SHIPPED, SHIPPED→DELIVERED)
//     그 외는 예외
//   - 상태 변경 후 저장
```

#### `controller/OrderController.java`

```
// @RestController, @RequestMapping("/api/orders")

// POST   /api/orders                    → 주문 생성, @Valid 검증 적용
// GET    /api/orders/{id}               → 주문 단건 조회
// GET    /api/orders?userId={userId}    → 사용자별 주문 목록 조회
// PUT    /api/orders/{id}/cancel        → 주문 취소
// PUT    /api/orders/{id}/status        → 주문 상태 변경, 요청 바디로 새 상태값 전달
```

---

### [실습 3] 글로벌 예외 처리

---

#### `common/ApiResponse.java`

```
// 모든 API 응답을 감싸는 제네릭 래퍼 클래스
// success (boolean): 성공 여부
// message (String): 응답 메시지
// data (T): 실제 데이터 페이로드 (제네릭 타입)
//
// 정적 팩토리 메서드를 제공한다:
//   of(T data): 성공 응답 생성 (success=true)
//   of(T data, String message): 메시지 포함 성공 응답 생성
//   fail(String message): 실패 응답 생성 (success=false, data=null)
```

#### `exception/ErrorCode.java`

```
// Enum으로 애플리케이션 전체 에러 코드를 중앙 관리한다
// 각 Enum 값은 HTTP 상태 코드와 메시지를 함께 보유한다
//
// 정의할 에러 코드 목록:
//   USER_NOT_FOUND          (404): 존재하지 않는 사용자
//   PRODUCT_NOT_FOUND       (404): 존재하지 않는 상품
//   CATEGORY_NOT_FOUND      (404): 존재하지 않는 카테고리
//   ORDER_NOT_FOUND         (404): 존재하지 않는 주문
//   PRODUCT_NOT_ON_SALE     (400): 판매 중인 상품이 아님
//   INSUFFICIENT_STOCK      (400): 재고 부족
//   INVALID_ORDER_STATUS    (400): 유효하지 않은 주문 상태 전환
//   ORDER_CANCEL_NOT_ALLOWED(400): 취소 불가능한 주문 상태
//   CATEGORY_NAME_DUPLICATE (409): 카테고리명 중복
//   CATEGORY_HAS_PRODUCTS   (400): 상품이 존재하는 카테고리 삭제 시도
```

#### `exception/BusinessException.java`

```
// RuntimeException을 상속하는 비즈니스 예외 기본 클래스
// ErrorCode errorCode 필드를 보유한다
// 생성자: BusinessException(ErrorCode errorCode)
// 생성자: BusinessException(ErrorCode errorCode, String detail)
//   - detail은 에러 메시지에 추가 정보를 포함할 때 사용
```

#### `exception/ErrorResponse.java`

```
// API 에러 응답 표준 포맷
// code (String): ErrorCode의 name() 값 (예: "PRODUCT_NOT_FOUND")
// message (String): 사람이 읽을 수 있는 에러 설명
// status (int): HTTP 상태 코드 숫자값
// timestamp (LocalDateTime): 에러 발생 시각
//
// 정적 팩토리 메서드를 제공한다:
//   of(ErrorCode errorCode): ErrorCode로부터 ErrorResponse 생성
//   of(ErrorCode errorCode, String detail): 상세 메시지 포함 생성
```

#### `exception/GlobalExceptionHandler.java`

```
// @ControllerAdvice 로 전역 예외를 처리한다
// @Slf4j 로 에러 로그를 기록한다

// handleBusinessException(BusinessException ex):
//   - @ExceptionHandler(BusinessException.class)
//   - ex.getErrorCode()의 HTTP 상태 코드로 응답
//   - ErrorResponse를 응답 바디로 반환

// handleMethodArgumentNotValidException(MethodArgumentNotValidException ex):
//   - @ExceptionHandler(MethodArgumentNotValidException.class)
//   - HTTP 400 응답
//   - BindingResult에서 필드별 유효성 검증 오류 메시지를 수집하여 응답에 포함

// handleIllegalArgumentException(IllegalArgumentException ex):
//   - @ExceptionHandler(IllegalArgumentException.class)
//   - HTTP 400 응답
//   - 기존 서비스 코드의 IllegalArgumentException을 포용하기 위해 추가

// handleException(Exception ex):
//   - @ExceptionHandler(Exception.class)
//   - 모든 미처리 예외를 HTTP 500으로 응답
//   - 실제 에러 내용은 서버 로그에만 기록하고 응답에는 일반 메시지만 반환
```

---

### [실습 4] 페이징 & 검색 고도화

---

#### `repository/ProductRepository.java` (수정)

```
// 기존 메서드는 유지하고 아래 메서드를 추가한다

// findAll(Pageable pageable): 전체 상품 페이징 조회
//   - JpaRepository 기본 제공 메서드, 별도 선언 불필요

// findByStatusAndNameContaining(ProductStatus status, String keyword, Pageable pageable):
//   - 상태 + 상품명 키워드 복합 검색 (페이징 포함)

// findByPriceBetween(Integer minPrice, Integer maxPrice, Pageable pageable):
//   - 가격 범위 검색 (페이징 포함)

// @Query를 사용하여 가격 범위 + 상태 + 키워드를 동시에 적용하는 검색 메서드 추가:
//   - JPQL로 작성, 조건이 null이면 해당 조건을 무시하도록 처리
//   - 메서드명 예시: searchProducts(String keyword, ProductStatus status,
//                                   Integer minPrice, Integer maxPrice, Pageable pageable)
```

#### `repository/OrderRepository.java` (수정)

```
// 기존 메서드는 유지하고 아래 메서드를 추가한다

// findByUserId(Long userId, Pageable pageable):
//   - 사용자별 주문 목록 페이징 조회
//   - Page<Order> 반환

// findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable):
//   - 사용자별 + 상태별 주문 목록 페이징 조회
```

#### `controller/ProductController.java` (수정)

```
// 기존 엔드포인트는 유지하고 아래 엔드포인트를 추가한다

// GET /api/products/search
//   - 요청 파라미터: keyword(선택), status(선택), minPrice(선택), maxPrice(선택)
//   - 페이징 파라미터: page(기본값 0), size(기본값 20), sort(선택)
//   - Page<ProductResponse> 를 ApiResponse로 감싸서 반환
```

#### `controller/OrderController.java` (수정)

```
// 기존 엔드포인트는 유지하고 아래 엔드포인트를 추가한다

// GET /api/orders/users/{userId}
//   - 사용자별 주문 목록 페이징 조회
//   - 요청 파라미터: status(선택), page(기본값 0), size(기본값 10)
//   - Page<OrderResponse> 를 ApiResponse로 감싸서 반환
```

---

### [실습 5] 비동기 이벤트 처리

---

#### `event/OrderCompletedEvent.java`

```
// 주문 완료(CONFIRMED 전환) 시 발행되는 도메인 이벤트 클래스
// orderId (Long): 완료된 주문 ID
// userId (Long): 주문자 ID
// userName (String): 주문자명
// totalPrice (Integer): 주문 총액
// orderedAt (LocalDateTime): 주문 시각
//
// 이 클래스는 ApplicationEvent를 상속하거나 POJO 클래스로 작성할 수 있다
// (Spring 4.2 이후 POJO 이벤트를 권장)
```

#### `event/OrderEventListener.java`

```
// @Component 로 등록되는 이벤트 리스너
// @Slf4j 로 이벤트 수신 로그를 기록한다

// onOrderCompleted(OrderCompletedEvent event):
//   - @EventListener(OrderCompletedEvent.class) 로 이벤트를 구독한다
//   - @Async 를 추가하여 메인 트랜잭션과 분리된 스레드에서 실행한다
//   - 실제 처리 내용: 주문 완료 알림 발송 시뮬레이션 (로그 출력으로 대체)
//     예) "주문 완료 알림 발송: userId={}, orderId={}, 금액={}"
//   - 처리 중 예외가 발생해도 주문 트랜잭션에 영향을 주지 않아야 한다
//     (별도 스레드이므로 @Transactional 전파가 끊어진 상태임에 유의)
```

#### `service/OrderService.java` (수정)

```
// 기존 OrderService에 ApplicationEventPublisher를 주입한다
// (생성자 주입 방식 사용)

// updateOrderStatus() 메서드 내 수정:
//   - 상태가 CONFIRMED로 전환될 때 OrderCompletedEvent를 발행한다
//   - applicationEventPublisher.publishEvent(new OrderCompletedEvent(...))
//   - 이벤트 발행은 트랜잭션 커밋 전에 이루어지므로
//     @TransactionalEventListener(phase=AFTER_COMMIT) 사용을 고려할 수 있다
```

---

### [실습 6] Actuator 커스텀 메트릭

---

#### `actuator/OrderMetrics.java`

```
// @Component 로 등록되어 애플리케이션 시작 시 메트릭을 등록한다
// MeterRegistry를 생성자 주입으로 받는다

// 등록할 메트릭:
//   Counter - "shop.orders.total"
//     - 태그: status (PENDING / CONFIRMED / SHIPPED / DELIVERED / CANCELLED)
//     - 용도: 상태별 누적 주문 건수 카운팅
//     - 주문 생성, 상태 변경, 취소 시점에 OrderMetrics를 호출하여 증가
//
//   Gauge - "shop.products.on_sale.count"
//     - 현재 ON_SALE 상태인 상품 수를 실시간으로 반영
//     - ProductRepository를 참조하여 카운트 반환하는 람다 등록
//
// 메트릭 증가를 위한 public 메서드를 제공한다:
//   incrementOrder(OrderStatus status): 해당 상태 Counter 증가
```

#### `actuator/ShopHealthIndicator.java`

```
// HealthIndicator 인터페이스를 구현한다
// @Component 로 등록하면 /actuator/health 에 "shop" 항목으로 자동 포함된다

// health() 메서드 구현:
//   - ProductRepository, OrderRepository를 주입받아 현재 상태를 점검한다
//   - 점검 항목:
//     1. 판매 중(ON_SALE) 상품이 1개 이상 존재하는지 확인
//     2. PENDING 상태 주문이 특정 임계값(예: 100건) 이하인지 확인
//   - 모든 항목 정상: Health.up() 반환, 상태 정보를 withDetail()로 포함
//   - 이상 항목 존재: Health.down() 반환, 어떤 항목이 이상인지 withDetail()에 기록
```

#### `resources/application.yaml` (수정)

```
// management.metrics 하위에 아래 설정을 추가한다

// management.metrics.tags.application: ${spring.application.name}
//   - 모든 메트릭에 애플리케이션 이름 태그를 자동 부착

// management.endpoint.health.show-details: always 는 이미 설정되어 있음 (변경 불필요)

// 커스텀 메트릭 접근 확인:
//   GET /actuator/metrics/shop.orders.total
//   GET /actuator/metrics/shop.products.on_sale.count
//   GET /actuator/health (shop 항목 포함 여부 확인)
```

---

## 실습 진행 순서 권장

```
실습 3 (예외 처리 기반 구축)
  → 실습 1 (Category 도메인)
    → 실습 2 (Order 도메인) ← 핵심, 가장 많은 시간 소요
      → 실습 4 (페이징/검색)
        → 실습 5 (비동기 이벤트)
          → 실습 6 (Actuator 메트릭)
```

실습 3을 먼저 완료하면 이후 모든 서비스에서 표준화된 예외 처리와 응답 포맷을 바로 활용할 수 있습니다.  
실습 2의 `OrderService`는 이 실습에서 가장 복잡한 트랜잭션 로직을 포함하므로 충분한 시간을 배분합니다.
