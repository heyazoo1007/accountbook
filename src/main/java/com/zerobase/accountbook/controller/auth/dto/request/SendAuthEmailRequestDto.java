package com.zerobase.accountbook.controller.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendAuthEmailRequestDto {

    @Email
    private String email;
}
