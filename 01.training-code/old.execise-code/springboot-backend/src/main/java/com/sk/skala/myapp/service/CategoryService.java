package com.sk.skala.myapp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.myapp.domain.Category;
import com.sk.skala.myapp.dto.CategoryRequest;
import com.sk.skala.myapp.exception.BusinessException;
import com.sk.skala.myapp.exception.ErrorCode;
import com.sk.skala.myapp.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 전체 카테고리 목록 조회
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 카테고리 단건 조회 - 없으면 BusinessException
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, "id=" + id));
    }

    // 카테고리 생성 - 카테고리명 중복 시 예외
    @Transactional
    public Category createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_DUPLICATE, request.getName());
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    // 카테고리 수정
    @Transactional
    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = getCategoryById(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    // 카테고리 삭제 - 속한 상품이 있으면 삭제 불가
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        if (!category.getProducts().isEmpty()) {
            throw new BusinessException(ErrorCode.CATEGORY_HAS_PRODUCTS, "id=" + id);
        }
        categoryRepository.delete(category);
    }
}
