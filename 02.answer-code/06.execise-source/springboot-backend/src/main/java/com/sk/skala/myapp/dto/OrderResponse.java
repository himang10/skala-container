package com.sk.skala.myapp.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.sk.skala.myapp.domain.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private Long userId;
    private String userName;
    private OrderStatus status;
    private Integer totalPrice;
    private LocalDateTime orderedAt;
    private List<OrderItemResponse> items;
}
