package com.yelm.memberpj.user.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yelm.memberpj.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    // TODO: 조건에 맞게 Query 작성
    public Page<UserDto.UserResponseDto> getUsers(PageRequest of, String sort) {
        return null;
    }

    public Page<UserDto.UserResponseDto> getUsers(PageRequest of) {
        return null;
    }
}
