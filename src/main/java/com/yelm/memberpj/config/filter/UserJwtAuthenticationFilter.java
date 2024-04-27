package com.yelm.memberpj.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yelm.memberpj.config.dto.UserLoginDto;
import com.yelm.memberpj.config.jwt.JwtTokenizer;
import com.yelm.memberpj.config.refreshtoken.RefreshToken;
import com.yelm.memberpj.config.refreshtoken.RefreshTokenRepository;
import com.yelm.memberpj.config.refreshtoken.RefreshTokenService;
import com.yelm.memberpj.user.User;
import com.yelm.memberpj.user.service.UserService;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class UserJwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService memberService;
    public UserJwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenizer jwtTokenizer,
                                       RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository,
                                       UserService memberService){
        this.authenticationManager = authenticationManager;
        this.jwtTokenizer = jwtTokenizer;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberService = memberService;
    }
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        ObjectMapper objectMapper = new ObjectMapper();
        UserLoginDto loginDto = objectMapper.readValue(request.getInputStream(), UserLoginDto.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws ServletException, IOException{
        User member = (User) authResult.getPrincipal();

        String accessToken = delegateAccessToken(member);
        String refreshToken = delegateRefreshToken(member);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh", refreshToken);
        response.setHeader("roles", "user");
        response.setHeader("memberId", String.valueOf(member.getMemberId()));

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        User fm = memberService.findVerifiedUser(member.getMemberId());
        response.getWriter().write(fm.getName());

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setMemberId(member.getMemberId());
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    private String delegateAccessToken(User member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", member.getUsername());
        claims.put("roles", member.getRoles());

        String subject = member.getUsername();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        return accessToken;
    }


    private String delegateRefreshToken(User member) {
        String subject = member.getUsername();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String refreshToken = jwtTokenizer.generateMemberRefreshToken(subject, expiration, base64EncodedSecretKey);

        return refreshToken;
    }
}
