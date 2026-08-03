package com.sk.skala.myapp.dto;

import com.sk.skala.myapp.domain.ProductStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "상품명은 필수입니다")
    private String name;

    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    private Integer price;

    @Min(value = 0, message = "재고는 0 이상이어야 합니다")
    private Integer stockQuantity;

    @NotNull(message = "상품 상태는 필수입니다")
    private ProductStatus status;

    private String description;   // @Lob 필드 — 대용량 텍스트 가능

    private Long userId;          // 상품을 등록한 사용자 ID (@ManyToOne 연관관계)
    private String userName;      // 상품을 등록한 사용자 이름 (조회용, @ManyToOne 연관관계)
}
