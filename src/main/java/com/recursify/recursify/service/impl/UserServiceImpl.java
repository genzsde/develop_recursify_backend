package com.recursify.recursify.service.impl;

import org.springframework.stereotype.Service;

import com.recursify.recursify.dto.AuthResponseDto;
import com.recursify.recursify.dto.UserRequestDto;
import com.recursify.recursify.dto.UserResponseDto;
import com.recursify.recursify.model.User;
import com.recursify.recursify.repository.UserRepository;
import com.recursify.recursify.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.recursify.recursify.security.JwtUtil;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserResponseDto register(UserRequestDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .streakCount(0)
                .build();

        User saved = userRepository.save(user);

        return UserResponseDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .streakCount(saved.getStreakCount())
                .build();
    }

    @Override
    public AuthResponseDto login(UserRequestDto dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponseDto.builder()
            .token(token)
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }
}
