package com.pedrobn.finance_control.security;

import com.pedrobn.finance_control.model.User;
import com.pedrobn.finance_control.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {

    private final UserRepository userRepository;

    public AuthContext(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByEmail(email);
        }
        throw new RuntimeException("User not authenticated");
    }
}
