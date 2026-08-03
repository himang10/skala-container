package com.sk.skala.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long orderItemId;
    private Long productId;
    private String productName;    // 주문 시점 상품명
    private Integer quantity;      // 주문 수량
    private Integer unitPrice;     // 주문 시점 단가
    private Integer subtotal;      // 소계 = unitPrice × quantity
}
