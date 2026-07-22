package com.pulsepay.service;

import com.pulsepay.entities.User;
import com.pulsepay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String username, String email, String rawPassword){

        //logic validations
        if (userRepository.existsByEmail(email)){
            throw new IllegalArgumentException ("Email already exists");
        }
        if(userRepository.existsByUsername(username)){
            throw  new IllegalArgumentException("Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        //initialization using Builder Pattern
        User newUser = User.builder()
                .username(username)
                .email(email)
                .password(hashedPassword)
                .balance(BigDecimal.ZERO) // starting balance is 0 = enforcing business rules
                .build();
        //persistence
        return userRepository.save(newUser);
    }
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found" + id));
    }

}
