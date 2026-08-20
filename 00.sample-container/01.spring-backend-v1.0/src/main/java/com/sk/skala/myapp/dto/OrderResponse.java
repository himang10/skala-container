package com.sk.skala.myapp.dto;

import java.time.LocalDateTime;

import com.sk.skala.myapp.domain.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private Long userId;
    private String userName;        // 주문한 사용자 이름
    private Long productId;
    private String productName;     // 주문한 상품 이름
    private Integer quantity;
    private Integer totalAmount;    // 결제 금액 (상품 가격 × 수량)
    private OrderStatus status;
    private String statusLabel;     // 상태의 한글 표시명
    private String paymentStatus;   // 결제 상태 한글 표시명
    private String deliveryStatus;  // 배송 상태 한글 표시명
    private LocalDateTime orderedAt;
}
