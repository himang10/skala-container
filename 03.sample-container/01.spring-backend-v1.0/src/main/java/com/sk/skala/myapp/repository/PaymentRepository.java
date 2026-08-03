package com.sk.skala.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.myapp.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
