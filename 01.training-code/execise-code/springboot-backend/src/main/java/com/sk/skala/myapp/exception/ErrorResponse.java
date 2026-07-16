package com.sk.skala.myapp.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * API 에러 응답 표준 포맷
 * - code: ErrorCode의 name() 값 (예: "PRODUCT_NOT_FOUND")
 * - message: 사람이 읽을 수 있는 에러 설명
 * - status: HTTP 상태 코드 숫자값
 * - timestamp: 에러 발생 시각
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    private String code;
    private String message;
    private int status;
    private LocalDateTime timestamp;

    // ErrorCode로부터 ErrorResponse 생성
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.name(),
                errorCode.getMessage(),
                errorCode.getStatus().value(),
                LocalDateTime.now()
        );
    }

    // 상세 메시지 포함 생성
    public static ErrorResponse of(ErrorCode errorCode, String detail) {
        return new ErrorResponse(
                errorCode.name(),
                errorCode.getMessage() + " (" + detail + ")",
                errorCode.getStatus().value(),
                LocalDateTime.now()
        );
    }
}
