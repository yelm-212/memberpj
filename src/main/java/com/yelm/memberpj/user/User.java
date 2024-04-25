package com.yelm.memberpj.user;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.stereotype.Repository;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Repository
@Getter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private String password;
    @ColumnDefault("")
    private String nickname;
    private String name;
    private String phonenumber;
    private String email;

}
