# 미니 쇼핑몰 주문 관리 API 서버 구축

Spring Boot 기반의 User, Product 코드를 확장하여 주문(Order) 기능을 직접 구현합니다.

---

## 1. 실습 목표

- 기존 `User`, `Product` 엔티티와 연관된 새로운 도메인(`Order`, `OrderItem`)을 설계하고 구현한다.
- `@ManyToOne`, `@OneToMany`, `CascadeType.ALL`, `orphanRemoval` 등 JPA 연관관계를 실전에 적용한다.
- 주문 생성 시 재고 감소, 주문 취소 시 재고 복원 등 트랜잭션 기반의 비즈니스 로직을 `@Transactional`로 처리한다.
- 기존 엔티티의 `@Version` 낙관적 락이 동시 재고 차감 충돌 방지에 어떻게 활용되는지 이해한다.
- `@ControllerAdvice`와 `@ExceptionHandler`를 통해 전역 예외 처리 구조를 구현한다.

---

## 2. 작성 파일 목록 및 설명

아래 파일을 순서대로 작성합니다. 기존 코드(`User`, `Product`, `UserService`, `ProductService` 등)는 수정하지 않습니다.

| 순서 | 파일 경로 | 설명 |
|------|-----------|------|
| 1 | `domain/OrderStatus.java` | 주문 상태 Enum |
| 2 | `domain/Order.java` | 주문 JPA 엔티티 |
| 3 | `domain/OrderItem.java` | 주문 항목 JPA 엔티티 |
| 4 | `dto/OrderItemRequest.java` | 주문 항목 요청 DTO |
| 5 | `dto/OrderItemResponse.java` | 주문 항목 응답 DTO |
| 6 | `dto/OrderRequest.java` | 주문 생성 요청 DTO |
| 7 | `dto/OrderResponse.java` | 주문 응답 DTO |
| 8 | `repository/OrderRepository.java` | 주문 JPA Repository |
| 9 | `service/OrderService.java` | 주문 비즈니스 로직 |
| 10 | `controller/OrderController.java` | 주문 REST 컨트롤러 |
| 11 | `exception/GlobalExceptionHandler.java` | 전역 예외 처리기 |

---

## 3. 파일별 요구 사항 및 채워야 할 코드

---

### 파일 1. `domain/OrderStatus.java`

주문 상태를 나타내는 Enum입니다.

```java
package com.sk.skala.myapp.domain;

public enum OrderStatus {
    // PENDING   : 주문 접수 (결제 대기)
    // CONFIRMED : 주문 확정 (결제 완료)
    // SHIPPED   : 배송 중
    // DELIVERED : 배송 완료
    // CANCELLED : 주문 취소
    //
    // 위 5가지 상태를 Enum 값으로 선언한다
}
```

---

### 파일 2. `domain/Order.java`

`Order`는 `User`와 N:1 관계이며, `OrderItem` 목록을 1:N으로 보유합니다.

```java
package com.sk.skala.myapp.domain;

// 필요한 Jakarta Persistence, Lombok 어노테이션을 import한다

@Entity
@Table(name = "orders")   // "order"는 SQL 예약어이므로 테이블명을 반드시 명시한다
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    // @Id, @GeneratedValue(IDENTITY) 로 Long 타입 PK 필드를 선언한다

    // @ManyToOne(fetch=LAZY), @JoinColumn(name="user_id", nullable=false) 로
    // User 필드를 선언한다 (주문자)

    // @OneToMany(mappedBy="order", cascade=ALL, orphanRemoval=true, fetch=LAZY) 로
    // List<OrderItem> 필드를 선언한다
    // - new ArrayList<>() 로 초기화한다
    // - @ToString.Exclude 를 추가하여 순환참조를 방지한다

    // @Enumerated(EnumType.STRING), @Column(nullable=false) 로
    // OrderStatus 타입 status 필드를 선언한다

    // @Column(nullable=false) 로 Integer 타입 totalPrice 필드를 선언한다

    // @Column(nullable=false) 로 LocalDateTime 타입 orderedAt 필드를 선언한다
}
```

---

### 파일 3. `domain/OrderItem.java`

하나의 주문에 속하는 개별 상품 항목입니다.  
`unitPrice`는 주문 시점의 상품 단가를 고정 저장하여, 이후 상품 가격 변경에 영향받지 않도록 합니다.

```java
package com.sk.skala.myapp.domain;

// 필요한 Jakarta Persistence, Lombok 어노테이션을 import한다

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    // @Id, @GeneratedValue(IDENTITY) 로 Long 타입 PK 필드를 선언한다

    // @ManyToOne(fetch=LAZY), @JoinColumn(name="order_id") 로
    // Order 필드를 선언한다
    // - @ToString.Exclude 를 추가하여 순환참조를 방지한다

    // @ManyToOne(fetch=LAZY), @JoinColumn(name="product_id") 로
    // Product 필드를 선언한다

    // @Column(nullable=false) 로 Integer 타입 quantity 필드를 선언한다 (주문 수량)

    // @Column(nullable=false) 로 Integer 타입 unitPrice 필드를 선언한다
    // (주문 시점의 상품 단가 - 상품 가격 변경에 영향받지 않도록 별도 저장)

    // @Column(nullable=false) 로 Integer 타입 subtotal 필드를 선언한다
    // (소계 = unitPrice × quantity)
}
```

---

### 파일 4. `dto/OrderItemRequest.java`

주문 항목 하나의 입력값입니다.

```java
package com.sk.skala.myapp.dto;

// 필요한 jakarta.validation, Lombok 어노테이션을 import한다

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    // @NotNull 로 Long 타입 productId 필드를 선언한다

    // @NotNull, @Min(1) 로 Integer 타입 quantity 필드를 선언한다
    // (1 이상의 값만 허용)
}
```

---

### 파일 5. `dto/OrderItemResponse.java`

주문 항목 하나의 응답값입니다.

```java
package com.sk.skala.myapp.dto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    // orderItemId (Long)   : 주문 항목 ID
    // productId   (Long)   : 상품 ID
    // productName (String) : 상품명
    // quantity    (Integer): 주문 수량
    // unitPrice   (Integer): 주문 시점 단가
    // subtotal    (Integer): 소계
    //
    // 위 6개 필드를 선언한다
}
```

---

### 파일 6. `dto/OrderRequest.java`

주문 생성 요청 DTO입니다. 항목 목록을 `@Valid`로 중첩 검증합니다.

```java
package com.sk.skala.myapp.dto;

// 필요한 import 추가

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    // @NotNull 로 Long 타입 userId 필드를 선언한다

    // @NotEmpty 로 List<OrderItemRequest> 타입 items 필드를 선언한다
    // - @Valid 를 함께 추가하여 중첩 객체의 유효성 검증이 전파되도록 한다
    // - 최소 1개 이상의 항목이 있어야 한다
}
```

---

### 파일 7. `dto/OrderResponse.java`

주문 조회 결과 응답 DTO입니다.

```java
package com.sk.skala.myapp.dto;

// 필요한 import 추가

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    // orderId     (Long)                 : 주문 ID
    // userId      (Long)                 : 주문자 ID
    // userName    (String)               : 주문자명
    // status      (OrderStatus)          : 주문 상태
    // totalPrice  (Integer)              : 총 주문 금액
    // orderedAt   (LocalDateTime)        : 주문 시각
    // items       (List<OrderItemResponse>): 주문 항목 목록
    //
    // 위 7개 필드를 선언한다
}
```

---

### 파일 8. `repository/OrderRepository.java`

```java
package com.sk.skala.myapp.repository;

// 필요한 import 추가

public interface OrderRepository extends JpaRepository<Order, Long> {

    // userId 로 해당 사용자의 전체 주문 목록을 조회하는 쿼리 메서드를 선언한다
    // 반환 타입: List<Order>

    // OrderStatus 로 해당 상태의 주문 목록을 조회하는 쿼리 메서드를 선언한다
    // 반환 타입: List<Order>
}
```

---

### 파일 9. `service/OrderService.java`

이 파일이 실습의 핵심입니다. `@Transactional` 범위 안에서 여러 엔티티를 함께 처리합니다.

```java
package com.sk.skala.myapp.service;

// 필요한 import 추가

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public Order createOrder(OrderRequest request) {
        // 1. userId 로 User 를 조회한다
        //    없으면 IllegalArgumentException("존재하지 않는 사용자") 을 던진다

        // 2. Order 객체를 생성하고 다음 값을 설정한다
        //    - user: 1번에서 조회한 User
        //    - status: OrderStatus.PENDING
        //    - orderedAt: LocalDateTime.now()

        // 3. request.getItems() 를 순회하며 각 항목을 처리한다
        //    (3-1) productId 로 Product 를 조회한다
        //          없으면 IllegalArgumentException("존재하지 않는 상품") 을 던진다
        //
        //    (3-2) product.getStatus() 가 ON_SALE 인지 확인한다
        //          아니면 IllegalArgumentException("판매 중인 상품이 아닙니다") 를 던진다
        //
        //    (3-3) product.getStockQuantity() 가 요청 수량 이상인지 확인한다
        //          부족하면 IllegalArgumentException("재고가 부족합니다") 를 던진다
        //
        //    (3-4) product.getStockQuantity() 에서 요청 수량을 차감하고 저장한다
        //          (Product 의 @Version 이 낙관적 락으로 동시 차감 충돌을 감지한다)
        //
        //    (3-5) OrderItem 을 생성하고 다음 값을 설정한다
        //          - order: 2번에서 생성한 Order
        //          - product: 조회한 Product
        //          - quantity: 요청 수량
        //          - unitPrice: product.getPrice() (주문 시점 단가 고정)
        //          - subtotal: unitPrice × quantity
        //
        //    (3-6) order.getOrderItems().add(orderItem) 으로 항목을 추가한다

        // 4. 모든 OrderItem 의 subtotal 합계를 계산하여 order.setTotalPrice() 로 설정한다

        // 5. orderRepository.save(order) 로 저장하고 반환한다
        return null; // 구현 후 이 줄을 삭제한다
    }

    /**
     * 주문 단건 조회
     */
    public Order getOrderById(Long id) {
        // orderRepository.findById(id) 로 조회한다
        // 없으면 IllegalArgumentException("존재하지 않는 주문") 을 던진다
        return null; // 구현 후 이 줄을 삭제한다
    }

    /**
     * 사용자별 주문 목록 조회
     */
    public List<Order> getOrdersByUserId(Long userId) {
        // userId 로 해당 사용자의 주문 목록을 조회하여 반환한다
        return null; // 구현 후 이 줄을 삭제한다
    }

    /**
     * 주문 취소
     * - PENDING 상태의 주문만 취소 가능하다
     * - 취소 시 각 OrderItem 의 상품 재고를 원복한다
     */
    @Transactional
    public Order cancelOrder(Long orderId) {
        // 1. orderId 로 Order 를 조회한다 (없으면 예외)

        // 2. order.getStatus() 가 PENDING 이 아니면
        //    IllegalArgumentException("접수 상태의 주문만 취소할 수 있습니다") 를 던진다

        // 3. order.getOrderItems() 를 순회하며 각 항목의 재고를 복원한다
        //    - product.setStockQuantity(현재 재고 + 주문 수량)
        //    - productRepository.save(product)

        // 4. order.setStatus(OrderStatus.CANCELLED) 로 상태를 변경한다

        // 5. 저장 후 반환한다
        return null; // 구현 후 이 줄을 삭제한다
    }

    /**
     * 주문 상태 변경
     * 허용된 전환: PENDING→CONFIRMED, CONFIRMED→SHIPPED, SHIPPED→DELIVERED
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        // 1. orderId 로 Order 를 조회한다 (없으면 예외)

        // 2. 유효한 상태 전환인지 확인한다
        //    - PENDING   → CONFIRMED 만 허용
        //    - CONFIRMED → SHIPPED   만 허용
        //    - SHIPPED   → DELIVERED 만 허용
        //    - 그 외 전환은 IllegalArgumentException 을 던진다

        // 3. order.setStatus(newStatus) 로 상태를 변경하고 저장 후 반환한다
        return null; // 구현 후 이 줄을 삭제한다
    }
}
```

---

### 파일 10. `controller/OrderController.java`

```java
package com.sk.skala.myapp.controller;

// 필요한 import 추가

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // OrderItem → OrderItemResponse 변환 메서드를 작성한다
    // - orderItemId, productId, productName, quantity, unitPrice, subtotal 을 매핑한다
    private OrderItemResponse toItemResponse(OrderItem item) {
        // 구현한다
        return null;
    }

    // Order → OrderResponse 변환 메서드를 작성한다
    // - orderId, userId, userName, status, totalPrice, orderedAt, items 를 매핑한다
    // - items 는 order.getOrderItems() 를 toItemResponse() 로 변환한 리스트를 사용한다
    private OrderResponse toResponse(Order order) {
        // 구현한다
        return null;
    }

    // POST /api/orders — 주문 생성
    // @Valid 로 OrderRequest 유효성 검증을 적용한다
    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        // orderService.createOrder(request) 를 호출하고 결과를 toResponse() 로 변환하여 반환한다
        return null;
    }

    // GET /api/orders/{id} — 주문 단건 조회
    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        // orderService.getOrderById(id) 를 호출하고 toResponse() 로 변환하여 반환한다
        return null;
    }

    // GET /api/orders?userId={userId} — 사용자별 주문 목록 조회
    @GetMapping
    public List<OrderResponse> getOrdersByUserId(@RequestParam Long userId) {
        // orderService.getOrdersByUserId(userId) 를 호출하고
        // 각 Order 를 toResponse() 로 변환한 List 를 반환한다
        return null;
    }

    // PUT /api/orders/{id}/cancel — 주문 취소
    @PutMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        // orderService.cancelOrder(id) 를 호출하고 toResponse() 로 변환하여 반환한다
        return null;
    }

    // PUT /api/orders/{id}/status — 주문 상태 변경
    // 요청 바디 예시: { "status": "CONFIRMED" }
    @PutMapping("/{id}/status")
    public OrderResponse updateOrderStatus(@PathVariable Long id,
                                           @RequestBody Map<String, String> body) {
        // body.get("status") 를 OrderStatus.valueOf() 로 변환한다
        // orderService.updateOrderStatus(id, newStatus) 를 호출하고
        // toResponse() 로 변환하여 반환한다
        return null;
    }
}
```

---

### 파일 11. `exception/GlobalExceptionHandler.java`

서비스에서 던진 `IllegalArgumentException`을 HTTP 400으로 응답합니다.

```java
package com.sk.skala.myapp.exception;

// 필요한 import 추가

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler(IllegalArgumentException.class) 로
    // IllegalArgumentException 을 잡아 HTTP 400 응답을 반환한다
    // 응답 바디에는 에러 메시지(ex.getMessage()) 를 포함한다

    // @ExceptionHandler(MethodArgumentNotValidException.class) 로
    // @Valid 검증 실패를 잡아 HTTP 400 응답을 반환한다
    // BindingResult 의 필드별 오류 메시지를 수집하여 응답 바디에 포함한다

    // @ExceptionHandler(Exception.class) 로
    // 그 외 모든 예외를 잡아 HTTP 500 응답을 반환한다
    // 실제 에러는 log.error() 로 기록하고, 응답에는 일반 메시지만 반환한다
}
```

---

## API 동작 확인

구현 후 Swagger UI(`http://localhost:8080/swagger-ui.html`) 또는 H2 Console(`http://localhost:8080/h2-console`)을 이용하여 아래 시나리오를 직접 실행해 봅니다.

```
1. 주문 생성
   POST /api/orders
   { "userId": 1, "items": [{ "productId": 1, "quantity": 2 }] }

2. 주문 조회
   GET /api/orders/1
   GET /api/orders?userId=1

3. 주문 상태 변경
   PUT /api/orders/1/status  →  { "status": "CONFIRMED" }
   PUT /api/orders/1/status  →  { "status": "SHIPPED" }

4. 주문 취소 (PENDING 상태에서만 가능)
   POST /api/orders  →  새 주문 생성 (PENDING)
   PUT /api/orders/{id}/cancel

5. 재고 확인
   GET /api/products/1  →  주문 수량만큼 stockQuantity 가 감소했는지 확인
```
