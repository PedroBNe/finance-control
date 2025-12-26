package com.pedrobn.finance_control.service;

import org.springframework.stereotype.Service;

import com.pedrobn.finance_control.dto.UserRequest;
import com.pedrobn.finance_control.dto.UserResponse;
import com.pedrobn.finance_control.model.User;
import com.pedrobn.finance_control.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    UserResponse createUser(UserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user = userRepository.save(user);
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    UserResponse login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password");
        }
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
