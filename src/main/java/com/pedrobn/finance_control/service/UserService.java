package com.pedrobn.finance_control.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pedrobn.finance_control.dto.LoginResponse;
import com.pedrobn.finance_control.dto.UserRequest;
import com.pedrobn.finance_control.dto.UserResponse;
import com.pedrobn.finance_control.model.User;
import com.pedrobn.finance_control.repository.UserRepository;
import com.pedrobn.finance_control.security.TokenProvider;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByEmail(request.email()) != null) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String token = tokenProvider.generateToken(user.getEmail());
        return new LoginResponse(token);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
