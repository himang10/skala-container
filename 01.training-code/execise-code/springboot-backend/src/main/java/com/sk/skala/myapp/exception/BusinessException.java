package com.sk.skala.myapp.exception;

import lombok.Getter;

/**
 * 비즈니스 규칙 위반 시 발생하는 애플리케이션 기본 예외 클래스
 * ErrorCode를 기반으로 HTTP 상태 코드와 메시지를 통합 관리한다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    // 기본 생성자 - ErrorCode 메시지만 사용
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 상세 정보 포함 생성자 - 추가 컨텍스트 정보를 메시지에 포함
    public BusinessException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + " (" + detail + ")");
        this.errorCode = errorCode;
    }
}
