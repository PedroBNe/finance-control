package com.pedrobn.finance_control.dto;

public record UserRequest(
    String name,
    String email,
    String password
) {
    
}
