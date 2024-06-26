package com.zerobase.accountbook.domain.member;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String email;

    private String memberName;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private Integer monthlyBudget;

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(MemberRole role) {
        this.role = role;
    }

    public void setMonthlyBudget(Integer monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
