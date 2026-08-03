package com.sk.skala.myapp.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 모든 API 응답을 감싸는 표준 래퍼 클래스
 * - success: 처리 성공 여부
 * - message: 응답 메시지
 * - data: 실제 페이로드 (제네릭)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // 성공 응답 - 데이터만 반환
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(true, "success", data);
    }

    // 성공 응답 - 메시지 포함
    public static <T> ApiResponse<T> of(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    // 실패 응답
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
