package com.sk.skala.myapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.myapp.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 카테고리명으로 단건 조회 - 중복 확인에 활용
    Optional<Category> findByName(String name);

    // 카테고리명 존재 여부 확인 - createCategory 시 중복 검증
    boolean existsByName(String name);
}
