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

    @CreatedDate
    private String createdAt;

    @LastModifiedDate
    private String updatedAt;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private Integer paidAmount;

    private String payLocation;

    private String methodOfPayment;

    private long categoryId;

    private String memo;

    private String date;
}

