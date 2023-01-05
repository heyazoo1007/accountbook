package com.zerobase.accountbook.controller.auth.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberPasswordRequestDto {

    private long memberId;

    private String beforePassword;

    private String afterPassword;
}
