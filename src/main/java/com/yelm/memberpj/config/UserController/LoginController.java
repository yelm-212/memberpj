package com.yelm.memberpj.config.UserController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yelm.memberpj.config.dto.TokenResponseDto;
import com.yelm.memberpj.config.dto.UserLoginDto;
import com.yelm.memberpj.config.jwt.JwtTokenizer;
import com.yelm.memberpj.config.refreshtoken.RefreshTokenRepository;
import com.yelm.memberpj.config.refreshtoken.RefreshTokenService;
import com.yelm.memberpj.user.User;
import com.yelm.memberpj.user.UserDto;
import com.yelm.memberpj.user.UserMapper;
import com.yelm.memberpj.user.service.UserService;
import com.yelm.memberpj.utils.dtos.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/user/login")
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final UserMapper mapper;
    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @PostMapping
    public ResponseEntity login(@Valid @RequestBody UserLoginDto loginDto) throws JsonProcessingException {

        User authorizedMember = userService.loginMember(loginDto);

        UserDto.UserResponseDto responseDto = mapper.userToUserResponseDto(authorizedMember);
        TokenResponseDto tokenResponseDto = jwtTokenizer.createTokenByLoginUser(responseDto);

        Map<String, Object> claims = jwtTokenizer.getClaims(tokenResponseDto.getAtk()).getBody();
        long memberId = authorizedMember.getMemberId();
        User findmember = userService.findVerifiedUser(memberId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokenResponseDto.getAtk());
        headers.add("Refresh", tokenResponseDto.getRtk());
        headers.add("roles", "user");
        headers.add("memberId", String.valueOf(findmember.getMemberId()));

        return new ResponseEntity<>(new SingleResponseDto<>(responseDto), headers, HttpStatus.OK);

    }
}
