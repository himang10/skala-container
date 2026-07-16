package com.sk.skala.myapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.sk.skala.myapp.domain.User;
import com.sk.skala.myapp.dto.UserRequest;
import com.sk.skala.myapp.dto.UserResponse;
import com.sk.skala.myapp.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // UserRequest → User 엔티티 변환
    private User toEntity(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return user;
    }

    // User 엔티티 → UserResponse 변환
    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    // 모든 사용자 조회
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(this::toResponse)
                .toList();
    }

    // GET: 특정 사용자 가져오기
    @GetMapping("/users/{id}")
    public UserResponse getUserById(@PathVariable long id) {
        return userService.getUserById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    // POST: 사용자 추가 — @Valid로 UserRequest 검증
    @PostMapping("/users")
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        User saved = userService.createUser(toEntity(request));
        return toResponse(saved);
    }

    // DELETE: 사용자 삭제
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }

    // PUT: 사용자 정보 수정 — @Valid로 UserRequest 검증
    @PutMapping("/users/{id}")
    public UserResponse updateUser(@PathVariable long id,
                                   @Valid @RequestBody UserRequest request) {
        return userService.updateUser(id, toEntity(request))
                .map(this::toResponse)
                .orElse(null);
    }
}