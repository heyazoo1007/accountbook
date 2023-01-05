package com.zerobase.accountbook.domain.member;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String memberName;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
