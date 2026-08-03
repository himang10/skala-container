package com.sk.skala.myapp.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 결제 엔티티
 * - 주문이 생성될 때 함께 생성되며, 결제 금액과 결제 상태를 관리한다.
 * - Order 엔티티가 payment_id FK로 결제를 참조한다. (주문 1건 : 결제 1건)
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer amount;                 // 결제 금액 (상품 가격 × 수량)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;           // 결제 상태 (PENDING / COMPLETED)

    private LocalDateTime paidAt;           // 결제 완료 시각 (완료 전에는 null)

    // 결제 완료 처리
    public void complete() {
        this.status = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }
}
