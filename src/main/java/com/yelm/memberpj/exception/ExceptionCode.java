package com.yelm.memberpj.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    USER_NOT_FOUND(404, "Member not found"),
    USER_EXISTS(409, "Member exists"),
    USER_EMAIL_EXISTS(409, "Email exists"),
    UNAUTHORIZED(409, "Unauthorized"),
    PASSWORD_ERR(409, "Password doesnt matches"),
    REFRESH_NOT_FOUND(404, "Token doesnt exists");
    private final int status;
    private final String message;

    ExceptionCode(int statusCode, String message){
        this.status = statusCode;
        this.message = message;
    }
}
