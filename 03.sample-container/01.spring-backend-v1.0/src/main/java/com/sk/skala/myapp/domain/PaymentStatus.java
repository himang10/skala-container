package com.sk.skala.myapp.domain;

/**
 * 결제 상태를 나타내는 Enum
 */
public enum PaymentStatus {
    PENDING("결제 대기"),
    COMPLETED("결제 완료");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
