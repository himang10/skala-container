package com.sk.skala.myapp.controller;

import com.sk.skala.myapp.service.AsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AsyncController {

    private final AsyncService asyncService;

    /**
     * 비동기 메서드 호출 - 반환값 없음
     * 
     * 예시: GET /api/void?message=test
     */
    @GetMapping("/void")
    public String callAsyncVoid(@RequestParam String message) {
        log.info("Controller 시작 - Thread: {}", Thread.currentThread().getName());
        
        asyncService.asyncMethodWithoutReturn(message);
        
        log.info("Controller 즉시 반환 - Thread: {}", Thread.currentThread().getName());
        return "비동기 작업이 시작되었습니다. (반환값 없음)";
    }

    /**
     * 비동기 메서드 호출 - 반환값 있음
     * 
     * 예시: GET /api/future?message=test
     */
    @GetMapping("/future")
    public CompletableFuture<String> callAsyncFuture(@RequestParam String message) {
        log.info("Controller 시작 - Thread: {}", Thread.currentThread().getName());
        
        CompletableFuture<String> future = asyncService.asyncMethodWithReturn(message);
        
        log.info("Controller 반환 - Thread: {}", Thread.currentThread().getName());
        return future;
    }

    /**
     * 동기 메서드 호출 - 비교용
     * 
     * 예시: GET /api/sync?message=test
     */
    @GetMapping("/sync")
    public String callSync(@RequestParam String message) {
        log.info("Controller 시작 (동기) - Thread: {}", Thread.currentThread().getName());
        
        String result = asyncService.syncMethod(message);
        
        log.info("Controller 완료 (동기) - Thread: {}", Thread.currentThread().getName());
        return result;
    }

    /**
     * 여러 비동기 작업 병렬 처리
     * 
     * 예시: GET /api/multiple
     */
    @GetMapping("/multiple")
    public CompletableFuture<String> callMultipleAsync() {
        log.info("Multiple Async 시작 - Thread: {}", Thread.currentThread().getName());
        
        CompletableFuture<String> future1 = asyncService.asyncMethodWithReturn("작업1");
        CompletableFuture<String> future2 = asyncService.asyncMethodWithReturn("작업2");
        CompletableFuture<String> future3 = asyncService.asyncMethodWithReturn("작업3");
        
        // 모든 비동기 작업이 완료될 때까지 대기하고 결과 조합
        return CompletableFuture.allOf(future1, future2, future3)
                .thenApply(v -> {
                    try {
                        String result = String.format("결과: [%s], [%s], [%s]",
                                future1.get(), future2.get(), future3.get());
                        log.info("Multiple Async 완료 - Thread: {}", Thread.currentThread().getName());
                        return result;
                    } catch (Exception e) {
                        return "에러 발생: " + e.getMessage();
                    }
                });
    }
}
