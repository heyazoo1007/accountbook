package com.zerobase.accountbook.controller.auth.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ValidateEmailResponseDto {

    private String email;
}
