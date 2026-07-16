package com.sk.skala.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sk.skala.myapp.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
