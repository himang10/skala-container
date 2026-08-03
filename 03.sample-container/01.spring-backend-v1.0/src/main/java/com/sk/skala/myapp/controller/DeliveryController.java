package com.sk.skala.myapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.myapp.domain.Delivery;
import com.sk.skala.myapp.service.DeliveryService;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // GET /api/deliveries — 전체 배송 조회
    @GetMapping
    public List<Delivery> getAllDeliveries() {
        return deliveryService.getAllDeliveries();
    }

    // GET /api/deliveries/{id} — 배송 단건 조회
    @GetMapping("/{id}")
    public Delivery getDeliveryById(@PathVariable Long id) {
        return deliveryService.getDeliveryById(id).orElse(null);
    }

    // POST /api/deliveries/{id}/complete — 배송 완료 처리
    @PostMapping("/{id}/complete")
    public Delivery completeDelivery(@PathVariable Long id) {
        return deliveryService.completeDelivery(id);
    }
}
