package com.recursify.recursify.controller;

import com.recursify.recursify.dto.AuthResponseDto;
import com.recursify.recursify.dto.UserRequestDto;
import com.recursify.recursify.dto.UserResponseDto;
import com.recursify.recursify.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // REGISTER
    @PostMapping("/register")
    public UserResponseDto register(@RequestBody UserRequestDto dto) {
        return userService.register(dto);
    }

    // LOGIN
    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody UserRequestDto dto) {
        return userService.login(dto);
    }
}