package com.sk.skala.myapp.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 애플리케이션 전체 에러 코드 중앙 관리 Enum
 * - status: HTTP 상태 코드
 * - message: 사용자에게 노출할 에러 메시지
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 사용자 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다"),

    // 상품 관련
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다"),
    PRODUCT_NOT_ON_SALE(HttpStatus.BAD_REQUEST, "판매 중인 상품이 아닙니다"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다"),

    // 카테고리 관련
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다"),
    CATEGORY_NAME_DUPLICATE(HttpStatus.CONFLICT, "이미 존재하는 카테고리명입니다"),
    CATEGORY_HAS_PRODUCTS(HttpStatus.BAD_REQUEST, "해당 카테고리에 속한 상품이 존재하여 삭제할 수 없습니다"),

    // 주문 관련
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 주문 상태 전환입니다"),
    ORDER_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "접수 상태(PENDING)의 주문만 취소할 수 있습니다"),

    // 동시성 관련
    OPTIMISTIC_LOCK_CONFLICT(HttpStatus.CONFLICT, "동시 요청으로 인한 충돌이 발생했습니다. 다시 시도해 주세요");

    private final HttpStatus status;
    private final String message;
}
