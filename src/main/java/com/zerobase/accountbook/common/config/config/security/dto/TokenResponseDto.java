package com.zerobase.accountbook.common.config.config.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenResponseDto { // 클라이언트에 토큰을 보내기 위한 DTO

    private String grantType; // JWT에 대한 인증 타입으로, 여기서는 Bearer 사용
    private String accessToken;
    private String refreshToken;
}
