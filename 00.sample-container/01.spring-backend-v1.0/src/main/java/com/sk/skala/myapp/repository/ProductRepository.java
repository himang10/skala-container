package com.sk.skala.myapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.myapp.domain.Product;
import com.sk.skala.myapp.domain.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 상태별 상품 목록 조회 (쿼리 메서드)
    List<Product> findByStatus(ProductStatus status);

    // 사용자별 상품 목록 조회 (@ManyToOne 연관관계 활용)
    List<Product> findByUserId(Long userId);

    // 사용자 이름으로 상품 목록 조회 (연관 엔티티 필드 탐색)
    List<Product> findByUserName(String userName);

    // 상품명에 검색어가 포함된 상품 목록 조회 (화면 검색용)
    List<Product> findByNameContaining(String keyword);
}
