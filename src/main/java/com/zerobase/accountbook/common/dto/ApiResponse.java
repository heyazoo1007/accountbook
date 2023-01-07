package com.zerobase.accountbook.common.dto;

import com.zerobase.accountbook.common.exception.ErrorCode;
import lombok.*;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {

    public static final ApiResponse SUCCESS = success(null);

    private HttpStatus status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(OK, "OK", data);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getStatusCode(), errorCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(errorCode.getStatusCode(), message, null);
    }
}
