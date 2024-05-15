package com.zerobase.accountbook.domain.dailypayments;

import com.zerobase.accountbook.domain.member.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

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

