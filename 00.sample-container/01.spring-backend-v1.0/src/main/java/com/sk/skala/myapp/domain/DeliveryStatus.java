package com.sk.skala.myapp.domain;

/**
 * 배송 상태를 나타내는 Enum
 */
public enum DeliveryStatus {
    PENDING("배송 대기"),
    COMPLETED("배송 완료");

    private final String label;

    DeliveryStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
