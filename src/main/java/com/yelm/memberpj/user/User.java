package com.yelm.memberpj.user;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.stereotype.Repository;

import javax.persistence.*;

@Entity
@Getter
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;
    private String password;
    private String nickname;
    private String name;
    private String phonenumber;
    private String email;

}
