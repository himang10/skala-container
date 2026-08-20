package com.sk.skala.myapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sk.skala.myapp.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이름에 검색어가 포함된 사용자 목록 조회 (화면 검색용)
    List<User> findByNameContaining(String keyword);
}
