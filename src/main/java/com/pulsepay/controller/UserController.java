package com.pulsepay.controller;

import com.pulsepay.dto.CreateUserRequest;
import com.pulsepay.dto.UserResponse;
import com.pulsepay.entities.User;
import com.pulsepay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody CreateUserRequest request){
        //1. hand the data off to the Service
        User newUser = userService.createUser(
                request.username(),
                request.email(),
                request.rawPassword()
        );

        //2. convert the database Entity into a safe DTO
        UserResponse responseDto = UserResponse.fromEntity(newUser);

        //return a 200 OK HTTP status along with the JSON data
        return ResponseEntity.ok(responseDto);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable("id") Long id){
        //1. ask the service to find the user
        User user = userService.getUserById(id);

        //2. convert entity into DTO
        UserResponse response = UserResponse.fromEntity(user);

        return ResponseEntity.ok(response);
    }
}
