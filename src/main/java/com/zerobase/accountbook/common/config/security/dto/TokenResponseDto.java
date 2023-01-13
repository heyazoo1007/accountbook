package com.zerobase.accountbook.common.config.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenResponseDto { // 클라이언트에 토큰을 보내기 위한 DTO

    private String accessToken;
}
