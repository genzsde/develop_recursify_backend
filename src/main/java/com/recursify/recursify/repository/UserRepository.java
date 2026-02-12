package com.recursify.recursify.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.recursify.recursify.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
