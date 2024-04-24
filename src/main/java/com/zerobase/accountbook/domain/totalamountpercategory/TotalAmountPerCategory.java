package com.zerobase.accountbook.domain.totalamountpercategory;

import com.zerobase.accountbook.domain.member.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class TotalAmountPerCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String date; // YYYY 형태의 문자열 연도

    @ManyToOne()
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private Long categoryId;

    private Integer totalAmount;
}
