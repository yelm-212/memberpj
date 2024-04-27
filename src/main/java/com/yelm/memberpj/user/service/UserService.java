package com.yelm.memberpj.user.service;


import com.yelm.memberpj.config.dto.UserLoginDto;
import com.yelm.memberpj.exception.BusinessLogicException;
import com.yelm.memberpj.exception.ExceptionCode;
import com.yelm.memberpj.user.Role;
import com.yelm.memberpj.user.User;
import com.yelm.memberpj.user.UserDto;
import com.yelm.memberpj.user.repository.UserQueryRepository;
import com.yelm.memberpj.user.repository.UserRepository;
import com.yelm.memberpj.utils.dtos.MultiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    // Todo: Service Layer Implementation

    private final UserRepository repository;
    private final UserQueryRepository queryRepository;
    private final PasswordEncoder encoder;

    public User postUser(UserDto.UserJoinDto userJoinDto){
        // Todo: 실제 로직 구현
        return repository.save(
                User.builder()
                        .username(userJoinDto.getUsername())
                        .password(encoder.encode(userJoinDto.getPassword()))
                        .nickname(userJoinDto.getNickname())
                        .name(userJoinDto.getName())
                        .phonenumber(userJoinDto.getPhonenumber())
                        .email(userJoinDto.getEmail())
                        .createAt(LocalDateTime.now())
                        .role(Role.USER)
                        .build());
    }

    public User patchUser(String username, UserDto.PatchDto dto){
        // Todo: 실제 로직 구현
        Optional<User> optionalUser = repository.findByUsername(username);

        if (!optionalUser.isPresent()){
           throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
        }

        User user = optionalUser.get();

        return repository.save(
                User.builder()
                        .memberId(user.getMemberId())
                        .username(user.getUsername())
                        .password(encoder.encode(dto.getPassword()))
                        .name(dto.getName() != null ?
                                dto.getName() : user.getName())
                        .nickname(dto.getNickname() != null ?
                                dto.getNickname() : user.getNickname())
                        .phonenumber(dto.getPhonenumber() != null ?
                                dto.getPhonenumber() : user.getPhonenumber())
                        .email(dto.getEmail() != null ?
                                dto.getEmail() : user.getEmail())
                        .createAt(user.getCreateAt())
                        .role(user.getRole())
                        .build());
    }

    public ResponseEntity getUsers(int page, int pageSize, String sort) {
        Page<UserDto.UserResponseDto> pageusers;

        if (!sort.isEmpty()){
            pageusers = queryRepository.getUsers(PageRequest.of(page, pageSize));
            return ResponseEntity.ok(new MultiResponseDto<>(pageusers.getContent(), pageusers));
        }else {
            pageusers = queryRepository.getUsers(PageRequest.of(page, pageSize), sort);
            return ResponseEntity.ok(new MultiResponseDto<>(pageusers.getContent(), pageusers));
        }

    }

    public User findVerifiedUser(Long memberId) {
        Optional<User> user = repository.findById(memberId);
        if (user.isPresent()) return user.get();
        else throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }

    public User loginMember(UserLoginDto dto){
        User findMember = repository
                .findByUsername(dto.getUsername())
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        if(!encoder.matches(dto.getPassword(), findMember.getPassword())){
            // throw new BusinessLogicException(ExceptionCode.PASSWORD_ERROR);
        }

        return findMember;
    }
}
