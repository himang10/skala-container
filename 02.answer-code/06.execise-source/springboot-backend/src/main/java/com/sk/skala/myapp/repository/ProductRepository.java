package com.sk.skala.myapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sk.skala.myapp.domain.Product;
import com.sk.skala.myapp.domain.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 상태별 상품 목록 조회 (쿼리 메서드)
    List<Product> findByStatus(ProductStatus status);

    // 사용자별 상품 목록 조회 (@ManyToOne 연관관계 활용)
    List<Product> findByUserId(Long userId);

    // 사용자 이름으로 상품 목록 조회 (연관 엔티티 필드 탐색)
    List<Product> findByUserName(String userName);

    // 카테고리별 상품 목록 조회 (실습 1 - @ManyToOne 연관관계 활용)
    List<Product> findByCategoryId(Long categoryId);

    // ── 실습 4: 페이징 & 검색 ─────────────────────────────────────────

    // 상태 + 상품명 키워드 복합 검색 (페이징)
    Page<Product> findByStatusAndNameContaining(ProductStatus status, String keyword, Pageable pageable);

    // 가격 범위 검색 (페이징)
    Page<Product> findByPriceBetween(Integer minPrice, Integer maxPrice, Pageable pageable);

    /**
     * 다중 조건 동적 검색 (JPQL)
     * - keyword, status, minPrice, maxPrice 가 null이면 해당 조건 무시
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR p.name LIKE %:keyword%) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("status") ProductStatus status,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable
    );
}

