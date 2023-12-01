package com.example.securitytest.controller;

import com.example.securitytest.config.JwtUtil;
import com.example.securitytest.domain.dto.SigninForm;
import com.example.securitytest.domain.dto.SignupForm;
import com.example.securitytest.domain.dto.TokenDto;
import com.example.securitytest.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    JwtUtil jwtUtil;



    @PostMapping("/auth/signup")
    public ResponseEntity<User> registerUser(@RequestBody SignupForm form){
        return ResponseEntity.ok(userService.registerUser(form));
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<TokenDto> loginUser(@RequestBody SigninForm form){
        return ResponseEntity.ok(userService.loginUser(form));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        String token = jwtUtil.parseJwt(request);
        if (token != null) {
            // 무효화된 토큰인지 여부를 확인하고, 무효화되었으면 응답
            if (!jwtUtil.isTokenValid(token)) {
                return ResponseEntity.badRequest().body("Token is already invalidated");
            }

            // 토큰을 무효화시키는 메서드 호출
            jwtUtil.invalidateToken(token);

            // 무효화된 토큰인지 여부를 다시 확인
            if (!jwtUtil.isTokenValid(token)) {
                return ResponseEntity.ok().body("Logged out successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to invalidate the token");
            }
        } else {
            return ResponseEntity.badRequest().body("Token not found in the request");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok().body("test!");
    }

    @GetMapping("/user/search")
    public void searchUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("username: " +userDetails.getUsername());
        log.info("pwd: " +userDetails.getPassword());
    }

    @GetMapping("/get-header")
    public String getHeader(@RequestHeader("Authorization") String authorization) {
        return userService.getHeaders(authorization);
    }

}
