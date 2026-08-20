package com.sk.skala.myapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.myapp.domain.Delivery;
import com.sk.skala.myapp.domain.DeliveryStatus;
import com.sk.skala.myapp.domain.Order;
import com.sk.skala.myapp.domain.OrderStatus;
import com.sk.skala.myapp.domain.Payment;
import com.sk.skala.myapp.domain.PaymentStatus;
import com.sk.skala.myapp.domain.Product;
import com.sk.skala.myapp.domain.User;
import com.sk.skala.myapp.repository.OrderRepository;
import com.sk.skala.myapp.repository.ProductRepository;
import com.sk.skala.myapp.repository.UserRepository;

/**
 * 주문 서비스
 * - 주문 생성: 상품/사용자 확인 → 재고 차감 → 결제·배송 정보 생성 → 주문 저장
 * - 상태 전환: 주문 완료 → 결제 대기 → 결제 완료 → 배송 대기 → 배송 완료
 */
@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // 전체 주문 조회
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 주문 단건 조회
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // 사용자별 주문 목록 조회
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // 주문 생성
    @Transactional
    public Order createOrder(Long productId, Long userId, int quantity, String address) {
        // 1. 상품과 사용자 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + productId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id=" + userId));

        // 2. 재고 확인 후 차감
        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        if (stock < quantity) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고=" + stock);
        }
        product.setStockQuantity(stock - quantity);

        // 3. 결제 정보 생성 (결제 대기 상태)
        Payment payment = new Payment();
        payment.setAmount(product.getPrice() * quantity);
        payment.setStatus(PaymentStatus.PENDING);

        // 4. 배송 정보 생성 (배송 대기 상태)
        Delivery delivery = new Delivery();
        delivery.setAddress(address != null && !address.isBlank() ? address : "기본 배송지");
        delivery.setStatus(DeliveryStatus.PENDING);

        // 5. 주문 생성 (주문 완료 상태로 시작)
        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setStatus(OrderStatus.ORDER_COMPLETED);
        order.setOrderedAt(LocalDateTime.now());
        order.setPayment(payment);      // cascade = ALL 이므로 함께 저장됨
        order.setDelivery(delivery);    // cascade = ALL 이므로 함께 저장됨

        return orderRepository.save(order);
    }

    // 주문 상태를 다음 단계로 전환
    @Transactional
    public Order advanceStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. id=" + orderId));

        OrderStatus next = order.getStatus().next();
        order.setStatus(next);

        // 결제 완료 단계로 넘어가면 결제 엔티티도 완료 처리
        if (next == OrderStatus.PAYMENT_COMPLETED) {
            order.getPayment().complete();
        }
        // 배송 완료 단계로 넘어가면 배송 엔티티도 완료 처리
        if (next == OrderStatus.DELIVERY_COMPLETED) {
            order.getDelivery().complete();
        }

        return order;   // @Transactional 안이므로 변경 감지(dirty checking)로 자동 저장
    }

    // 주문 삭제 (cascade = ALL 이므로 결제·배송도 함께 삭제)
    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
