package com.example.leavemanagementsystem.controller;

import com.example.leavemanagementsystem.dto.JwtResponseDto;
import com.example.leavemanagementsystem.dto.LoginRequestDto;
import com.example.leavemanagementsystem.dto.SignupRequestDto;
import com.example.leavemanagementsystem.model.User;
import com.example.leavemanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        JwtResponseDto jwt = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDto signupRequest) {
        User user = userService.registerUser(signupRequest);
        return ResponseEntity.ok("User registered successfully: " + user.getUsername());
    }
}
