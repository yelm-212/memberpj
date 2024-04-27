package com.yelm.memberpj.user.repository;

import static com.yelm.memberpj.user.QUser.user;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yelm.memberpj.user.QUserDto_UserResponseDto;
import com.yelm.memberpj.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<UserDto.UserResponseDto> getUsers(Pageable pageable, String keyword) {
        OrderSpecifier orderSpecifiers = createOrderSpecifier(keyword);

        List<UserDto.UserResponseDto> responses = jpaQueryFactory
                .select(new QUserDto_UserResponseDto(
                        user.memberId,
                        user.username,
                        user.nickname,
                        user.name,
                        user.phonenumber,
                        user.email,
                        user.createAt
                ))
                .from(user)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = jpaQueryFactory
                .select(user.count())
                .from(user)
                .fetchOne();

        return new PageImpl<>(responses,pageable,count);
    }

    public Page<UserDto.UserResponseDto> getUsers(Pageable pageable) {
        List<UserDto.UserResponseDto> responses = jpaQueryFactory
                .select(new QUserDto_UserResponseDto(
                user.memberId,
                user.username,
                user.nickname,
                user.name,
                user.phonenumber,
                user.email,
                user.createAt
        ))
                .from(user)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = jpaQueryFactory
                .select(user.count())
                .from(user)
                .fetchOne();

        return new PageImpl<>(responses,pageable,count);
    }

    private OrderSpecifier createOrderSpecifier(String keyword) {

        OrderSpecifier orderSpecifiers;

        if(keyword.equals("oldest")){
            orderSpecifiers = new OrderSpecifier(Order.DESC, user.createAt);
        }else if(keyword.equals("name")){
            orderSpecifiers = new OrderSpecifier(Order.ASC, user.name);
        }else if(keyword.equals("latest")){
            orderSpecifiers = new OrderSpecifier(Order.ASC, user.createAt);
        }else{
            // 기본 : 최신순
            orderSpecifiers = new OrderSpecifier(Order.ASC, user.createAt);
        }
        return orderSpecifiers;
    }
}
