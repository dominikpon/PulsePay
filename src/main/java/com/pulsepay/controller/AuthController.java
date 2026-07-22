package com.pulsepay.controller;

import com.pulsepay.dto.AuthResponse;
import com.pulsepay.dto.LoginRequest;
import com.pulsepay.dto.RegisterRequest;
import com.pulsepay.security.JwtUtil;
import com.pulsepay.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){

        //1. verify credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(),request.password())
        );

        //2.if password is correct
        String token = jwtUtil.generateToken(request.username());

        return ResponseEntity.ok(new AuthResponse(token));

    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request){

        //pass the request data down to the Service
        userService.createUser(request.username(), request.email(), request.password());

        return ResponseEntity.ok("User registered successfully");
    }
}
