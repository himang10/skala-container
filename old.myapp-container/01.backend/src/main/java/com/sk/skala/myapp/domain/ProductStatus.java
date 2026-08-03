package com.sk.skala.myapp.domain;

/**
 * 상품 상태를 나타내는 Enum
 * - @Enumerated(EnumType.STRING) 실습에 사용
 */
public enum ProductStatus {
    ON_SALE,    // 판매중
    SOLD_OUT,   // 품절
    DISCONTINUED // 단종
}
