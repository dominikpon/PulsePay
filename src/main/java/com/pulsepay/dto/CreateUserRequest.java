package com.pulsepay.dto;

public record CreateUserRequest(
        String username,
        String email,
        String rawPassword
) {}
