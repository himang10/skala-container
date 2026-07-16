package com.sk.skala.myapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.sk.skala.myapp.domain.User;
import com.sk.skala.myapp.repository.UserRepository;

import jakarta.validation.Valid;

@Service
@Validated
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 모든 사용자 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 특정 사용자 조회
    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    // 사용자 추가 — @Valid로 User 엔티티 재검증
    @Transactional
    public User createUser(@Valid User user) {
        return userRepository.save(user);
    }

    // 사용자 삭제
    @Transactional
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    // 사용자 정보 수정 — @Valid로 User 엔티티 재검증
    @Transactional
    public Optional<User> updateUser(long id, @Valid User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }
        User user = optionalUser.get();
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        User savedUser = userRepository.save(user);
        return Optional.of(savedUser);
    }
}
