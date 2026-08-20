package com.sk.skala.myapp.domain;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 주문 엔티티
 * - 사용자(User)가 상품(Product)을 선택해서 만든 주문 1건을 표현한다.
 * - 결제(Payment), 배송(Delivery)은 주문과 1:1 관계이며,
 *   cascade = ALL 이므로 주문을 저장/삭제하면 결제·배송도 함께 저장/삭제된다.
 * - status 필드 하나로 전체 흐름(주문 완료 → 결제 대기/완료 → 배송 대기/완료)을 관리한다.
 */
@Entity
@Table(name = "orders")                     // order는 SQL 예약어라서 테이블명은 orders 사용
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)      // N:1 - 한 사용자가 여러 주문을 만들 수 있음
    @JoinColumn(name = "user_id")
    private User user;                      // 주문한 사용자

    @ManyToOne(fetch = FetchType.LAZY)      // N:1 - 한 상품이 여러 주문에 담길 수 있음
    @JoinColumn(name = "product_id")
    private Product product;                // 주문한 상품

    @Column(nullable = false)
    private Integer quantity;               // 주문 수량

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;             // 주문 진행 상태

    private LocalDateTime orderedAt;        // 주문 시각

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")        // orders 테이블에 payment_id FK 생성
    private Payment payment;                // 이 주문의 결제 정보

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")       // orders 테이블에 delivery_id FK 생성
    private Delivery delivery;              // 이 주문의 배송 정보
}
