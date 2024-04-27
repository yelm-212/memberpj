package com.yelm.memberpj.user;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class UserDto {
    // TODO: DTO Implementation

    @Getter
    @Builder
    public static class UserJoinDto{
        @NotBlank(message = "공백이 아니어야 합니다.")
        @Size(min=1, max=30, message = "ID 길이는 최대 30입니다.")
        private String username;
        @NotBlank(message = "공백이 아니어야 합니다.")
        @Pattern(regexp = "^[A-Za-z\\d!@#$%^&*()_+~\\-=]{8,40}$")
        private String password;
        @NotBlank(message = "공백이 아니어야 합니다.")
        @Size(min=1, max=30, message = "닉네임 길이는 최대 30입니다.")
        private String nickname;

        @Pattern(regexp = "^010-\\d{3,4}-\\d{4}",message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
        private String phonenumber;

        @NotBlank(message = "공백이 아니어야 합니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
    }

    @Getter
    @Builder
    public static class PatchDto{

        @Pattern(regexp = "^[A-Za-z\\d!@#$%^&*()_+~\\-=]{8,40}$")
        private String password;

        @Size(min=1, max=30, message = "닉네임 길이는 최대 30입니다.")
        private String nickname;

        @Pattern(regexp = "^010-\\d{3,4}-\\d{4}",message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
        private String phonenumber;

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
    }

    @Getter
    @Builder
    public static class UserResponseDto{
        private Long memberId;
        private String username;
        private String nickname;
        private String phonenumber;
        private String email;
        private LocalDateTime createAt;

        @Builder
        @QueryProjection
        public UserResponseDto(Long memberId, String username, String nickname, String phonenumber, String email, LocalDateTime createAt) {
            this.memberId = memberId;
            this.username = username;
            this.nickname = nickname;
            this.phonenumber = phonenumber;
            this.email = email;
            this.createAt = createAt;
        }

    }

    @Getter
    @Builder
    public static class LoginDto{
        private String username;
        private String password;
    }
}
