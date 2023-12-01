package com.example.securitytest.controller;

import com.example.securitytest.config.JwtUtil;
import com.example.securitytest.domain.dto.SigninForm;
import com.example.securitytest.domain.dto.SignupForm;
import com.example.securitytest.domain.dto.TokenDto;
import com.example.securitytest.domain.entity.User;
import com.example.securitytest.domain.entity.UserRoleEnum;
import com.example.securitytest.domain.repository.UserRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(SignupForm form) {
        Optional<User> found = userRepository.findByUsername(form.getUsername());
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복중복");
        }

        UserRoleEnum role = UserRoleEnum.USER;

        User user = User.from(form, passwordEncoder.encode(form.getPassword()), role);
        return userRepository.save(user);
    }


    public TokenDto loginUser(SigninForm form) {

        User user = userRepository.findByUsername(form.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않음"));

        //유저가 존재하면
        if (passwordEncoder.matches(form.getPassword(), user.getPassword())) { //패스워드 확인 후 맞으면
            //토큰 발급

            return jwtUtil.generateToken(user.getUsername());
        }

        throw new IllegalArgumentException("패스워드가 다름");
    }
}
