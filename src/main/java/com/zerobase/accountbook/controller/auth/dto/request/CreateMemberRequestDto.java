package com.zerobase.accountbook.controller.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMemberRequestDto {

    @NotNull
    private String memberName;

    @Email
    private String email;

    @NotNull
    private String password;
}
