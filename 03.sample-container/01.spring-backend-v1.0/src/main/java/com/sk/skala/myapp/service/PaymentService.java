package com.sk.skala.myapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.myapp.domain.Payment;
import com.sk.skala.myapp.repository.PaymentRepository;

/**
 * 결제 서비스
 * - 결제 정보 조회와 결제 완료 처리를 담당한다.
 * - 결제 생성은 주문 생성 시 OrderService에서 함께 이루어진다.
 */
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // 전체 결제 조회
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // 결제 단건 조회
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    // 결제 완료 처리
    @Transactional
    public Payment completePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제입니다. id=" + id));
        payment.complete();
        return payment;
    }
}
