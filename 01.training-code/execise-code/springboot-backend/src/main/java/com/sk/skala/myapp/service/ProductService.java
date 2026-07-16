package com.sk.skala.myapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.myapp.domain.Category;
import com.sk.skala.myapp.domain.Product;
import com.sk.skala.myapp.domain.ProductStatus;
import com.sk.skala.myapp.domain.User;
import com.sk.skala.myapp.exception.BusinessException;
import com.sk.skala.myapp.exception.ErrorCode;
import com.sk.skala.myapp.repository.CategoryRepository;
import com.sk.skala.myapp.repository.ProductRepository;
import com.sk.skala.myapp.repository.UserRepository;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    // 전체 상품 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 상품 단건 조회
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // 상태별 상품 조회 (@Enumerated 실습)
    public List<Product> getProductsByStatus(ProductStatus status) {
        return productRepository.findByStatus(status);
    }

    // 사용자별 상품 목록 조회 (@ManyToOne 연관관계 활용)
    public List<Product> getProductsByUserId(Long userId) {
        return productRepository.findByUserId(userId);
    }

    // 사용자 이름으로 상품 목록 조회
    public List<Product> getProductsByUserName(String userName) {
        return productRepository.findByUserName(userName);
    }

    // 상품 등록 (userId, categoryId로 연관 엔티티 조회 후 연결)
    @Transactional
    public Product createProduct(Product product, Long userId, Long categoryId) {
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "id=" + userId));
            product.setUser(user);
        }
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, "id=" + categoryId));
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    // 상품 수정 (userId, categoryId 변경 가능)
    @Transactional
    public Optional<Product> updateProduct(Long id, Product updated, Long userId, Long categoryId) {
        return productRepository.findById(id).map(product -> {
            product.setName(updated.getName());
            product.setPrice(updated.getPrice());
            product.setStockQuantity(updated.getStockQuantity());
            product.setStatus(updated.getStatus());
            product.setDescription(updated.getDescription());
            if (userId != null) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "id=" + userId));
                product.setUser(user);
            }
            if (categoryId != null) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, "id=" + categoryId));
                product.setCategory(category);
            }
            return productRepository.save(product);
        });
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // ── 실습 4: 페이징 & 검색 ─────────────────────────────────────────

    // 다중 조건 동적 검색 (JPQL @Query 활용)
    public Page<Product> searchProducts(String keyword, ProductStatus status,
                                        Integer minPrice, Integer maxPrice, Pageable pageable) {
        return productRepository.searchProducts(keyword, status, minPrice, maxPrice, pageable);
    }
}
