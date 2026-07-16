package com.sk.skala.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private int productCount;    // 해당 카테고리에 속한 상품 수 (products.size()로 계산)
}
