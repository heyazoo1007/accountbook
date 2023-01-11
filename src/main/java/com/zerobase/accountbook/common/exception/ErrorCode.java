package com.zerobase.accountbook.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    // 400 Bad Request
    VALIDATION_EXCEPTION(BAD_REQUEST, "잘못된 요청입니다"),
    VALIDATION_ENUM_VALUE_EXCEPTION(BAD_REQUEST, "잘못된 Enum 값 입니다"),
    VALIDATION_REQUEST_MISSING_EXCEPTION(BAD_REQUEST, "필수적인 요청 값이 입력되지 않았습니다"),
    VALIDATION_WRONG_TYPE_EXCEPTION(BAD_REQUEST, "잘못된 타입이 입력되었습니다."),
    VALIDATION_SOCIAL_TYPE_EXCEPTION(BAD_REQUEST, "잘못된 소셜 프로바이더 입니다."),
    VALIDATION_WRONG_EMAIL_PASSWORD_EXCEPTION(BAD_REQUEST, "잘못된 이메일 혹은 비밀번호입니다."),
    VALIDATION_WRONG_PASSWORD_EXCEPTION(BAD_REQUEST, "잘못된 비밀번호입니다."),
    VALIDATION_EMAIL_AUTH_KEY_EXCEPTION(BAD_REQUEST, "잘못된 이메일 인증번호입니다."),
    VALIDATION_AUTH_EMAIL_HAS_ALREADY_BEEN_SENT(BAD_REQUEST, "인증 이메일이 이미 전송되었습니다."),

    // 401 UnAuthorized
    UNAUTHORIZED_EXCEPTION(UNAUTHORIZED, "토큰이 만료되었습니다. 다시 로그인 해주세요"),
    UNAUTHORIZED_EMAIL_EXCEPTION(UNAUTHORIZED, "인증이 완료되지 않은 이메일입니다."),
    UNAUTHORIZED_AUTH_KEY_EXCEPTION(UNAUTHORIZED, "인증키를 입력해주세요."),

    // 403 Forbidden
    FORBIDDEN_EXCEPTION(FORBIDDEN, "허용하지 않는 요청입니다."),
    FORBIDDEN_FILE_TYPE_EXCEPTION(BAD_REQUEST, "허용되지 않은 파일 형식입니다"),
    FORBIDDEN_FILE_NAME_EXCEPTION(BAD_REQUEST, "허용되지 않은 파일 이름입니다"),

    // 404 Not Found
    NOT_FOUND_EXCEPTION(NOT_FOUND, "존재하지 않습니다"),
    NOT_FOUND_USER_EXCEPTION(NOT_FOUND, "탈퇴하거나 존재하지 않는 유저입니다"),
    NOT_FOUND_ROOM_EXCEPTION(NOT_FOUND, "삭제되었거나 존재하지 않는 방입니다"),
    NOT_FOUND_FEED_EXCEPTION(NOT_FOUND, "삭제되었거나 존재하지 않는 피드입니다"),
    NOT_FOUND_EMAIL_EXCEPTION(NOT_FOUND, "가입되지 않은 이메일입니다."),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED_EXCEPTION(METHOD_NOT_ALLOWED, "지원하지 않는 메소드 입니다"),

    // 406 Not Acceptable
    NOT_ACCEPTABLE_EXCEPTION(NOT_ACCEPTABLE, "Not Acceptable"),

    // 409 Conflict
    CONFLICT_EXCEPTION(CONFLICT, "이미 존재합니다"),
    CONFLICT_EMAIL_EXCEPTION(CONFLICT, "이미 사용중인 이메일입니다.\n다른 이메일을 이용해주세요"),
    CONFLICT_USER_EXCEPTION(CONFLICT, "이미 해당 계정으로 회원가입하셨습니다.\n로그인 해주세요"),

    // 415 Unsupported Media Type
    UNSUPPORTED_MEDIA_TYPE_EXCEPTION(UNSUPPORTED_MEDIA_TYPE, "해당하는 미디어 타입을 지원하지 않습니다."),

    // 500 Internal Server Exception
    INTERNAL_SERVER_EXCEPTION(INTERNAL_SERVER_ERROR, "예상치 못한 서버 에러가 발생하였습니다."),

    // 502 Bad Gateway
    BAD_GATEWAY_EXCEPTION(BAD_GATEWAY, "일시적인 에러가 발생하였습니다.\n잠시 후 다시 시도해주세요!"),

    // 503 Service UnAvailable
    SERVICE_UNAVAILABLE_EXCEPTION(SERVICE_UNAVAILABLE, "현재 점검 중입니다.\n잠시 후 다시 시도해주세요!"),
    ;

    private final HttpStatus statusCode;
    private final String message;

}
