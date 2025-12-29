package com.pedrobn.finance_control.controller;

import org.springframework.web.bind.annotation.RestController;

import com.pedrobn.finance_control.dto.LoginResponse;
import com.pedrobn.finance_control.dto.UserRequest;
import com.pedrobn.finance_control.dto.UserResponse;
import com.pedrobn.finance_control.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.login(request.email(), request.password()));
    }
}
