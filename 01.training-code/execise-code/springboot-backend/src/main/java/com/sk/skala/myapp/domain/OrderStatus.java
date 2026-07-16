package com.sk.skala.myapp.domain;

/**
 * 주문 상태 Enum
 *
 * 상태 전환 규칙:
 *   PENDING → CONFIRMED → SHIPPED → DELIVERED (정상 흐름)
 *   PENDING → CANCELLED (취소는 PENDING 상태에서만 허용)
 */
public enum OrderStatus {
    PENDING,    // 주문 접수 (결제 대기)
    CONFIRMED,  // 주문 확정 (결제 완료) → 이 시점에 OrderCompletedEvent 발행
    SHIPPED,    // 배송 중
    DELIVERED,  // 배송 완료
    CANCELLED   // 주문 취소
}
