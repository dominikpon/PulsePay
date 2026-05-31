package com.pulsepay.dto;

import com.pulsepay.entities.User;

import java.math.BigDecimal;

public record UserResponse(
        Long id,
        String username,
        String email,
        BigDecimal balance
) {
    //a helpful method to convert an Entity into this DTO
    public static UserResponse fromEntity(User user){
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBalance()
        );
    }
}
