package com.sk.skala.myapp.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "주문자 ID는 필수입니다")
    private Long userId;

    @NotEmpty(message = "주문 항목은 1개 이상이어야 합니다")
    @Valid                          // 중첩 객체 유효성 검증 전파
    private List<OrderItemRequest> items;
}
