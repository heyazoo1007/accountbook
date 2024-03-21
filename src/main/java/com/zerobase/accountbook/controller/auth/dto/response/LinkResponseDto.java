package com.zerobase.accountbook.controller.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkResponseDto {
    private String redirectURL;

    public static LinkResponseDto of(String redirectURL) {
        return LinkResponseDto.builder()
                .redirectURL(redirectURL)
                .build();
    }
}
