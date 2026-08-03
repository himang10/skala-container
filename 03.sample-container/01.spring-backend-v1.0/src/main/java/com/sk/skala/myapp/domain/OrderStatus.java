package com.sk.skala.myapp.domain;

/**
 * 주문의 전체 진행 상태를 나타내는 Enum
 * 주문 완료 → 결제 대기 → 결제 완료 → 배송 대기 → 배송 완료 순서로 진행된다.
 */
public enum OrderStatus {
    ORDER_COMPLETED("주문 완료"),
    PAYMENT_PENDING("결제 대기"),
    PAYMENT_COMPLETED("결제 완료"),
    DELIVERY_PENDING("배송 대기"),
    DELIVERY_COMPLETED("배송 완료");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    // 화면에 표시할 한글 상태명
    public String getLabel() {
        return label;
    }

    // 다음 상태를 반환한다. 마지막 상태(배송 완료)면 그대로 유지한다.
    public OrderStatus next() {
        OrderStatus[] values = values();
        int nextIndex = ordinal() + 1;
        return nextIndex < values.length ? values[nextIndex] : this;
    }
}
