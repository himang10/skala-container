package com.sk.skala.myapp.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 주문 이벤트 리스너
 *
 * - @Async: 메인 트랜잭션과 분리된 별도 스레드에서 실행
 *   → 알림 처리 중 예외가 발생해도 주문 트랜잭션에 영향 없음
 *   → AsyncConfig에 설정된 "taskExecutor" 스레드 풀을 재사용
 */
@Slf4j
@Component
public class OrderEventListener {

    /**
     * 주문 완료 이벤트 수신 - 비동기 실행
     * 실제 환경에서는 이 메서드 내에서 이메일/SMS/푸시 알림 등을 발송한다.
     * 이 실습에서는 로그 출력으로 대체한다.
     */
    @Async("taskExecutor")
    @EventListener(OrderCompletedEvent.class)
    public void onOrderCompleted(OrderCompletedEvent event) {
        log.info("[OrderEventListener] 주문 완료 알림 발송 시작 - Thread: {}", Thread.currentThread().getName());
        log.info("[OrderEventListener] 주문자: {} (userId={}), 주문ID: {}, 결제금액: {}원",
                event.getUserName(), event.getUserId(), event.getOrderId(), event.getTotalPrice());

        // TODO: 실제 알림 발송 로직 구현 (이메일, SMS, 푸시 등)
        try {
            Thread.sleep(500); // 알림 발송 시뮬레이션 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("[OrderEventListener] 주문 완료 알림 발송 완료 - orderId={}", event.getOrderId());
    }
}
