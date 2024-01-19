package com.zerobase.accountbook.domain.category;

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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // YYYY-mm-dd HH:MM 형태, 사용자가 입력하는 문자열 날짜
    // 클라이언트로부터 데이터 받음
    private String date;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private String categoryName;
}
