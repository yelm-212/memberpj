package com.yelm.memberpj.user;


import com.yelm.memberpj.utils.dtos.MultiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    // Todo: Service Layer Implementation

    private final UserRepository repository;
//    private final BCryptPasswordEncoder encoder;

    public User postUser(UserDto.UserJoinDto userJoinDto){
        // Todo: 실제 로직 구현

        return User.builder().build();
    }

    public User loginUser(UserDto.LoginDto loginDto) {
        // Todo: 실제 로직 구현
        return User.builder().build();
    }

    public ResponseEntity getUsers(int page, int pageSize, String sort) {
        Page<UserDto.UserResponseDto> users;

        // Todo: 실제 로직 구현
        if (!sort.isEmpty()){

        }else {

        }

        List<UserDto.UserResponseDto> pageUser = users.getContent();
        MultiResponseDto<Object> body = new MultiResponseDto<>(users, pageUser);

        return ResponseEntity.ok(body);
    }
}
