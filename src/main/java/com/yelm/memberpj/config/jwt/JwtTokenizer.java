package com.yelm.memberpj.config.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yelm.memberpj.config.dto.TokenResponseDto;
import com.yelm.memberpj.config.refreshtoken.RefreshToken;
import com.yelm.memberpj.config.refreshtoken.RefreshTokenRepository;
import com.yelm.memberpj.config.refreshtoken.RefreshTokenService;
import com.yelm.memberpj.user.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenizer {
    @Getter
    @Value("${jwt.key}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-token-expiration-minutes}")
    private int accessTokenExpirationMinutes;

    @Getter
    @Value("${jwt.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public String encodeBase64SecretKey(String secretKey){
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Map<String, Object> claims,
                                      String subject,
                                      Date expiration,
                                      String base64EncodedSecretKey){
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public String generateMemberRefreshToken(String subject, Date expiration, String base64EncodedSecretKey){
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);
        return Jwts.builder()
                .setIssuer("member")
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }
    public String generateAdminRefreshToken(String subject, Date expiration, String base64EncodedSecretKey){
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);
        return Jwts.builder()
                .setIssuer("admin")
                .setSubject(subject)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    public TokenResponseDto createTokenByLoginUser(UserDto.UserResponseDto memberResponseDto) throws JsonProcessingException{
        String atk = delegateAccessTokenUser(memberResponseDto);
        String rtk = delegateRefreshTokenUser(memberResponseDto);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(rtk);
        refreshTokenEntity.setMemberId(memberResponseDto.getMemberId());
        refreshTokenService.addRefreshToken(refreshTokenEntity);
        return new TokenResponseDto(atk, rtk);
    }

    public Jws<Claims> getClaims(String jws){
        Key key = getKeyFromBase64EncodedKey(encodeBase64SecretKey(secretKey));
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
        return claims;
    }

    public Jws<Claims> getClaims(String jws, String base64EncodedSecretKey){
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
        return claims;
    }

    public void verifySignature(String jws, String base64EncodedSecretKey){
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jws);
    }

    private String delegateRefreshTokenUser(UserDto.UserResponseDto memberResponseDto){
        String subject = memberResponseDto.getEmail();
        Date expiration = getTokenExpiration(refreshTokenExpirationMinutes);
        String base64EncodedSecretKey = encodeBase64SecretKey(secretKey);
        return generateAdminRefreshToken(subject, expiration, base64EncodedSecretKey);
    }

    public Date getTokenExpiration(int expirationMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationMinutes);
        Date expiration = calendar.getTime();

        return expiration;
    }

    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey){
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return key;
    }

    private String delegateAccessTokenUser(UserDto.UserResponseDto memberResponseDto){
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", memberResponseDto.getEmail());
        claims.put("memberId", memberResponseDto.getMemberId());

        String subject = memberResponseDto.getEmail();
        Date expiration = getTokenExpiration(accessTokenExpirationMinutes);
        String base64EncodedSecretKey = encodeBase64SecretKey(secretKey);

        return generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);
    }
}
