package com.zerobase.accountbook.domain.dailypayments;

import com.zerobase.accountbook.domain.member.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class DailyPayments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private Integer paidAmount;

    private String paidWhere;

    private String methodOfPayment;

    private String categoryName;

    private String memo;

    @CreatedDate
    private String createdAt;

    @LastModifiedDate
    private String updatedAt;
}