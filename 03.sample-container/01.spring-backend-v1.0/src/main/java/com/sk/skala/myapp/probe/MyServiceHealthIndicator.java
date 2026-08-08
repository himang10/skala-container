package com.sk.skala.myapp.probe;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MyServiceHealthIndicator implements HealthIndicator {
    
    private final RestTemplate restTemplate;
    private final static String externalServiceUrl = "https://jsonplaceholder.typicode.com"; // 외부 서비스 URL (자기 자신의 actuator)
    
    public MyServiceHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Override
    public Health health() {
        try {
            long startTime = System.currentTimeMillis();
            boolean isHealthy = checkExternalService();
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (isHealthy) {
                return Health.up()
                    .withDetail("service", "External API")
                    .withDetail("responseTime", responseTime + "ms")
                    .withDetail("url", externalServiceUrl)
                    .build();
            } else {
                return Health.down()
                    .withDetail("reason", "External API returned non-200 status")
                    .withDetail("url", externalServiceUrl)
                    .build();
            }
        } catch (Exception e) {
            log.error("Health check failed", e);
            return Health.down()
                .withDetail("reason", "External API timeout or error")
                .withDetail("error", e.getMessage())
                .withDetail("url", externalServiceUrl)
                .build();
        }
    }
    
    private boolean checkExternalService() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                externalServiceUrl + "/posts/1", 
                String.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
