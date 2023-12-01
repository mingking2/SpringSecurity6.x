package com.example.securitytest.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupForm {
    private String username;
    private String password;
    private String email;
    private String adminToken= "";
}
