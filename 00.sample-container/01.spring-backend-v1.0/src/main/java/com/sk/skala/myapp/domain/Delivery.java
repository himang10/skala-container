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
 * 배송 엔티티
 * - 주문이 생성될 때 함께 생성되며, 배송지와 배송 상태를 관리한다.
 * - Order 엔티티가 delivery_id FK로 배송을 참조한다. (주문 1건 : 배송 1건)
 */
@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String address;                 // 배송지 주소

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;          // 배송 상태 (PENDING / COMPLETED)

    private LocalDateTime deliveredAt;      // 배송 완료 시각 (완료 전에는 null)

    // 배송 완료 처리
    public void complete() {
        this.status = DeliveryStatus.COMPLETED;
        this.deliveredAt = LocalDateTime.now();
    }
}
