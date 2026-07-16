package com.sk.skala.myapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.myapp.domain.Product;
import com.sk.skala.myapp.domain.ProductStatus;
import com.sk.skala.myapp.dto.ProductRequest;
import com.sk.skala.myapp.dto.ProductResponse;
import com.sk.skala.myapp.service.ProductService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ── 변환 헬퍼 ────────────────────────────────────────────

    private Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setStatus(request.getStatus());
        product.setDescription(request.getDescription());
        return product;
    }

    private ProductResponse toResponse(Product product) {
        Long userId = product.getUser() != null ? product.getUser().getId() : null;
        String userName = product.getUser() != null ? product.getUser().getName() : null;
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStatus(),
                product.getDescription(),
                product.getDisplayLabel(),  // @Transient 필드 — 런타임 계산값
                userId,
                userName,
                categoryId,
                categoryName
        );
    }

    // ── CRUD 엔드포인트 ───────────────────────────────────────

    // GET /api/products — 전체 상품 조회
    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(this::toResponse)
                .toList();
    }

    // GET /api/products/{id} — 상품 단건 조회
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    // GET /api/products/status?value=ON_SALE — 상태별 조회 (@Enumerated 실습)
    @GetMapping("/status")
    public List<ProductResponse> getProductsByStatus(@RequestParam ProductStatus value) {
        return productService.getProductsByStatus(value).stream()
                .map(this::toResponse)
                .toList();
    }

    // GET /api/products/user/{userId} — 사용자별 상품 목록 조회 (@ManyToOne 연관관계 활용)
    @GetMapping("/user/{userId}")
    public List<ProductResponse> getProductsByUserId(@PathVariable Long userId) {
        return productService.getProductsByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    // GET /api/products/user?name=홍길동 — 사용자 이름으로 상품 목록 조회
    @GetMapping("/user")
    public List<ProductResponse> getProductsByUserName(@RequestParam String name) {
        return productService.getProductsByUserName(name).stream()
                .map(this::toResponse)
                .toList();
    }

    // POST /api/products — 상품 등록
    @PostMapping
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        Product saved = productService.createProduct(toEntity(request), request.getUserId(), request.getCategoryId());
        log.info("상품 등록: {}", saved.getDisplayLabel());
        return toResponse(saved);
    }

    // PUT /api/products/{id} — 상품 수정
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id,
                                         @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, toEntity(request), request.getUserId(), request.getCategoryId())
                .map(this::toResponse)
                .orElse(null);
    }

    // DELETE /api/products/{id} — 상품 삭제
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    // ── 실습 4: 페이징 & 검색 엔드포인트 ─────────────────────────────────

    // GET /api/products/search — 다중 조건 동적 검색 (페이징)
    // 요청 예시: GET /api/products/search?keyword=노트북&status=ON_SALE&minPrice=100000&page=0&size=10
    @GetMapping("/search")
    public com.sk.skala.myapp.common.ApiResponse<org.springframework.data.domain.Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort) {

        org.springframework.data.domain.PageRequest pageable =
                org.springframework.data.domain.PageRequest.of(
                        page, size, org.springframework.data.domain.Sort.by(sort));

        org.springframework.data.domain.Page<ProductResponse> result =
                productService.searchProducts(keyword, status, minPrice, maxPrice, pageable)
                        .map(this::toResponse);
        return com.sk.skala.myapp.common.ApiResponse.of(result);
    }
}
