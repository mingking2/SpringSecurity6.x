package com.example.securitytest.config;

import com.example.securitytest.domain.dto.TokenDto;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class JwtUtil {
    @Value("${hidden.jwtSecret}")
    private String jwtSecret;
    private final long ACCESS_TOKEN_VALID_PERIOD = 1000L*60*30;
    private final long REFRESH_TOKEN_VALID_PERIOD = 1000L*60*60*24*7;
    private final Set<String> invalidatedTokens = new HashSet<>();

    public TokenDto generateToken(String name){
        Date now = new Date();
        Date accessTokenExpireIn = new Date(now.getTime() + ACCESS_TOKEN_VALID_PERIOD);


        String accessToken = Jwts.builder()
                .setClaims(Jwts.claims().setSubject(name))
                .setIssuedAt(now)
                .setExpiration(accessTokenExpireIn)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_PERIOD))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();


        return new TokenDto(accessToken, refreshToken, accessTokenExpireIn.getTime());
    }


    public String getUsername(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token){
        if (invalidatedTokens.contains(token)) {
            log.info("Token is invalidated");
            return false;
        }

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        if(StringUtils.hasText(headerAuth)&&headerAuth.startsWith("Bearer")){
            return headerAuth.substring(7);
        }
        return null;
    }

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }



}
