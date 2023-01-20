package com.zerobase.accountbook.service.totalamountpercategory;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategory;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategoryRepository;
import com.zerobase.accountbook.service.dailypaymetns.dto.DailyPaymentsCategoryDto;
import com.zerobase.accountbook.service.dailypaymetns.querydsl.DailyPaymentsQueryDsl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static com.zerobase.accountbook.common.exception.ErrorCode.NOT_FOUND_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class TotalAmountPerCategoryService {

    private final MemberRepository memberRepository;

    private final TotalAmountPerCategoryRepository totalAmountPerCategoryRepository;

    private final DailyPaymentsQueryDsl dailyPaymentsQueryDsl;

    @Scheduled(cron = "0 0 0 1 * * *") // 매달 1일 정각에 모든 사용자에 대해 실행
    public void saveMoneyPerCategory() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthBefore = now.minusMonths(1);


        // 100명 단위로 페이징한 멤버에 대해서 진행
        int totalMember = Math.toIntExact(memberRepository.countBy());
        for (int i = 0; i < totalMember / 100 + 1; i++) {
            Page<Member> members = memberRepository.findAll(
                    PageRequest.of(i, 100)
            );
            for (Member each : members) {
                Long memberId = each.getId();

                List<DailyPaymentsCategoryDto> all =
                        dailyPaymentsQueryDsl
                                .getTotalAmountPerCategoryByMemberId(
                                        oneMonthBefore.toString().substring(0, 7),
                                        memberId
                                );

                Member member = validateMemberById(memberId);

                for (DailyPaymentsCategoryDto dto : all) {
                    totalAmountPerCategoryRepository.save(
                            TotalAmountPerCategory.builder()
                                    .member(member)
                                    .dateInfo(String.valueOf(oneMonthBefore) // 2023-01 형태로 저장
                                            .substring(0, 7)
                                    )
                                    .categoryName(dto.getCategoryName())
                                    .totalAmount(dto.getTotalAmount())
                                    .createdAt(LocalDateTime.now())
                                    .build());
                }
            }
        }
    }

    private Member validateMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 회원입니다.",
                        NOT_FOUND_USER_EXCEPTION
                )
        );
    }
}
