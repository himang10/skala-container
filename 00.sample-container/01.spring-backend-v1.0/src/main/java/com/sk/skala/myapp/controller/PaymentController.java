package com.sk.skala.myapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.myapp.domain.Payment;
import com.sk.skala.myapp.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // GET /api/payments — 전체 결제 조회
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    // GET /api/payments/{id} — 결제 단건 조회
    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id).orElse(null);
    }

    // POST /api/payments/{id}/complete — 결제 완료 처리
    @PostMapping("/{id}/complete")
    public Payment completePayment(@PathVariable Long id) {
        return paymentService.completePayment(id);
    }
}
