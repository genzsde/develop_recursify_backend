package com.recursify.recursify.service;


import com.recursify.recursify.dto.AuthResponseDto;
import com.recursify.recursify.dto.UserRequestDto;
import com.recursify.recursify.dto.UserResponseDto;

public interface UserService {
    AuthResponseDto login(UserRequestDto requestDto);
    UserResponseDto register(UserRequestDto requestDto);
}
