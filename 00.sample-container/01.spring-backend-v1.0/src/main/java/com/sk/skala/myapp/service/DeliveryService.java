package com.sk.skala.myapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.myapp.domain.Delivery;
import com.sk.skala.myapp.repository.DeliveryRepository;

/**
 * 배송 서비스
 * - 배송 정보 조회와 배송 완료 처리를 담당한다.
 * - 배송 생성은 주문 생성 시 OrderService에서 함께 이루어진다.
 */
@Service
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    // 전체 배송 조회
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    // 배송 단건 조회
    public Optional<Delivery> getDeliveryById(Long id) {
        return deliveryRepository.findById(id);
    }

    // 배송 완료 처리
    @Transactional
    public Delivery completeDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배송입니다. id=" + id));
        delivery.complete();
        return delivery;
    }
}
