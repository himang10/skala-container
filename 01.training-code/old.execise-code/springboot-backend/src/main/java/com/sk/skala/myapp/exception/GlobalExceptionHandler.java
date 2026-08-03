package com.sk.skala.myapp.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * 전역 예외 처리기
 * @RestControllerAdvice: 모든 컨트롤러에서 발생하는 예외를 일괄 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // BusinessException: 비즈니스 규칙 위반 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("비즈니스 예외 발생: {}", ex.getMessage());
        ErrorResponse response = ErrorResponse.of(ex.getErrorCode());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    // @Valid 검증 실패 처리 - 필드별 오류 메시지를 수집하여 반환
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String messages = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("유효성 검증 실패: {}", messages);
        ErrorResponse response = new ErrorResponse(
                "VALIDATION_FAILED", messages, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);
    }

    // 낙관적 락 충돌 처리 - @Version 기반 동시성 충돌 시 발생
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailureException(OptimisticLockingFailureException ex) {
        log.warn("낙관적 락 충돌: {}", ex.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.OPTIMISTIC_LOCK_CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // IllegalArgumentException: 기존 서비스 코드의 예외를 포용
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("잘못된 인수: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "ILLEGAL_ARGUMENT", ex.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);
    }

    // 정적 리소스 없음 (favicon.ico 등) - 404 반환, ERROR 로그 제외
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.debug("정적 리소스 없음: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 그 외 모든 예외 - 서버 내부 오류로 처리 (실제 오류는 로그에만 기록)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("서버 내부 오류 발생", ex);
        ErrorResponse response = new ErrorResponse(
                "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다",
                HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
