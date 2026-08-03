package com.sk.skala.myapp.event;

import java.time.LocalDateTime;

/**
 * 주문 확정(CONFIRMED) 전환 시 발행되는 도메인 이벤트
 * Spring 4.2 이후 POJO 이벤트 방식을 사용 (ApplicationEvent 상속 불필요)
 */
public class OrderCompletedEvent {

    private final Long orderId;
    private final Long userId;
    private final String userName;
    private final Integer totalPrice;
    private final LocalDateTime orderedAt;

    public OrderCompletedEvent(Long orderId, Long userId, String userName,
                               Integer totalPrice, LocalDateTime orderedAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.totalPrice = totalPrice;
        this.orderedAt = orderedAt;
    }

    public Long getOrderId() { return orderId; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public Integer getTotalPrice() { return totalPrice; }
    public LocalDateTime getOrderedAt() { return orderedAt; }
}
