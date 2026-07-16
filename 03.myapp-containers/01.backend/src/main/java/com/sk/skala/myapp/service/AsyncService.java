package com.sk.skala.myapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AsyncService {

    /**
     * 비동기 메서드 - 반환값 없음 (void)
     */
    @Async
    public void asyncMethodWithoutReturn(String message) {
        log.info("비동기 메서드 시작 - Thread: {}, Message: {}", 
                 Thread.currentThread().getName(), message);
        try {
            Thread.sleep(2000); // 2초 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("비동기 메서드 완료 - Thread: {}, Message: {}", 
                 Thread.currentThread().getName(), message);
    }

    /**
     * 비동기 메서드 - 반환값 있음 (CompletableFuture)
     */
    @Async("taskExecutor")
    public CompletableFuture<String> asyncMethodWithReturn(String message) {
        log.info("비동기 메서드(반환값) 시작 - Thread: {}, Message: {}", 
                 Thread.currentThread().getName(), message);
        try {
            Thread.sleep(3000); // 3초 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String result = "처리 완료: " + message;
        log.info("비동기 메서드(반환값) 완료 - Thread: {}, Result: {}", 
                 Thread.currentThread().getName(), result);
        return CompletableFuture.completedFuture(result);
    }

    /**
     * 동기 메서드 - 비교용
     */
    public String syncMethod(String message) {
        log.info("동기 메서드 시작 - Thread: {}, Message: {}", 
                 Thread.currentThread().getName(), message);
        try {
            Thread.sleep(2000); // 2초 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String result = "동기 처리 완료: " + message;
        log.info("동기 메서드 완료 - Thread: {}, Result: {}", 
                 Thread.currentThread().getName(), result);
        return result;
    }
}
