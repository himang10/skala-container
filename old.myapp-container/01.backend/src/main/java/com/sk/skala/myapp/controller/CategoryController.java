package com.sk.skala.myapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.myapp.common.ApiResponse;
import com.sk.skala.myapp.domain.Category;
import com.sk.skala.myapp.dto.CategoryRequest;
import com.sk.skala.myapp.dto.CategoryResponse;
import com.sk.skala.myapp.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // Category 엔티티 → CategoryResponse 변환
    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getProducts().size()
        );
    }

    // GET /api/categories — 전체 카테고리 목록 조회
    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> result = categoryService.getAllCategories().stream()
                .map(this::toResponse)
                .toList();
        return ApiResponse.of(result);
    }

    // GET /api/categories/{id} — 카테고리 단건 조회
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ApiResponse.of(toResponse(categoryService.getCategoryById(id)));
    }

    // POST /api/categories — 카테고리 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category saved = categoryService.createCategory(request);
        log.info("카테고리 생성: id={}, name={}", saved.getId(), saved.getName());
        return ApiResponse.of(toResponse(saved), "카테고리가 생성되었습니다");
    }

    // PUT /api/categories/{id} — 카테고리 수정
    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id,
                                                        @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.of(toResponse(categoryService.updateCategory(id, request)), "카테고리가 수정되었습니다");
    }

    // DELETE /api/categories/{id} — 카테고리 삭제 (속한 상품이 있으면 400)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.of(null, "카테고리가 삭제되었습니다");
    }
}
