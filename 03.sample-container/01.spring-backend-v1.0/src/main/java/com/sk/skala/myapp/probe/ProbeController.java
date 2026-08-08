package com.sk.skala.myapp.probe;

import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class ProbeController {

    private final ApplicationEventPublisher publisher;
    private final ApplicationAvailability availability;

    public ProbeController(ApplicationEventPublisher publisher,
                           ApplicationAvailability availability) {
        this.publisher = publisher;
        this.availability = availability;
    }

    // 현재 상태 조회: GET /api/probe
    @GetMapping("/probe")
    public ProbeStatus getStatus() {
        String live = (availability.getLivenessState() == LivenessState.CORRECT) 
        ? LivenessState.CORRECT.name()
        : LivenessState.BROKEN.name();
        String ready = (availability.getReadinessState() == ReadinessState.ACCEPTING_TRAFFIC) 
        ? ReadinessState.ACCEPTING_TRAFFIC.name()
        : ReadinessState.REFUSING_TRAFFIC.name();
        return new ProbeStatus(live, ready);
    }

    // 상태 전환: POST /api/probe
    // body 예: { "liveness": "CORRECT" | "BROKEN", "readiness": "ACCEPTING_TRAFFIC" | "REFUSING_TRAFFIC" }
    @PostMapping("/probe")
    public ProbeStatus setStatus(@RequestBody ProbeStatus req) {

        log.info("req: {}", req);

        // liveness 처리
        String liveVal = req.getLiveness() == null ? "" : req.getLiveness().toUpperCase();
        LivenessState newLive = "CORRECT".equals(liveVal)
                ? LivenessState.CORRECT
                : LivenessState.BROKEN;
        AvailabilityChangeEvent.publish(publisher, this, newLive);
        log.info("newLive: {}", newLive);

        // readiness 처리
        String readyVal = req.getReadiness() == null ? "" : req.getReadiness().toUpperCase();
        ReadinessState newReady = "ACCEPTING_TRAFFIC".equals(readyVal)
                ? ReadinessState.ACCEPTING_TRAFFIC
                : ReadinessState.REFUSING_TRAFFIC;
        AvailabilityChangeEvent.publish(publisher, this, newReady);
        log.info("newReady: {}", newReady);

        // 변경 후 현재 상태 반환
        String live = (availability.getLivenessState() == LivenessState.CORRECT) 
            ? LivenessState.CORRECT.name()
            : LivenessState.BROKEN.name();
        String ready = (availability.getReadinessState() == ReadinessState.ACCEPTING_TRAFFIC) 
            ? ReadinessState.ACCEPTING_TRAFFIC.name()
            : ReadinessState.REFUSING_TRAFFIC.name();
        return new ProbeStatus(live, ready);
    }
}
