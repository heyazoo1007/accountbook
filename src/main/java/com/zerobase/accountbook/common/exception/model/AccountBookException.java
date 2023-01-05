package com.zerobase.accountbook.common.exception.model;

import com.zerobase.accountbook.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AccountBookException extends RuntimeException {

    private final ErrorCode errorCode;

    public AccountBookException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getStatus() {
        return errorCode.getStatus();
    }
}
