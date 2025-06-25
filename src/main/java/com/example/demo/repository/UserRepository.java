package com.example.demo.repository; // Kendi paket adınızı kullanın

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetPasswordToken(String token);

    List<User> findAllByResetPasswordTokenIsNotNullAndResetPasswordTokenExpiryBefore(LocalDateTime now);
}