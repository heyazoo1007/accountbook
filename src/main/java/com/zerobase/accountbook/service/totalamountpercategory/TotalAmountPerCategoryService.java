package com.zerobase.accountbook.service.totalamountpercategory;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategory;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static com.zerobase.accountbook.common.exception.ErrorCode.NOT_FOUND_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class TotalAmountPerCategoryService {

    private final MemberRepository memberRepository;

    private final DailyPaymentsRepository dailyPaymentsRepository;

    private final TotalAmountPerCategoryRepository totalAmountPerCategoryRepository;

    @Scheduled(cron = "0 0 0 1 * * *") // 매달 1일 정각에 모든 사용자에 대해 실행
    private void saveMoneyPerCategory() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthBefore = now.minusMonths(1);

        List<Member> members = memberRepository.findAll();
        HashMap<String, Integer> totalAmountPerCategory = new HashMap<>();
        for (Member each : members) {
            Long memberId = each.getId();

            List<DailyPayments> all =
                    dailyPaymentsRepository.findByMemberIdAndCreatedAtBetween(
                            memberId,
                            oneMonthBefore.toString(),
                            now.toString()
                    );

            for (DailyPayments dailyPayments : all) {
                String categoryName = dailyPayments.getCategoryName();
                Integer paidAmount = dailyPayments.getPaidAmount();

                totalAmountPerCategory.put(
                        categoryName,
                        totalAmountPerCategory.getOrDefault(categoryName, 0)
                                + paidAmount);
            }

            Member member = validateMemberById(memberId);

            for (String key : totalAmountPerCategory.keySet()) {
                totalAmountPerCategoryRepository.save(TotalAmountPerCategory.builder()
                        .member(member)
                        .dateInfo(String.valueOf(oneMonthBefore).substring(0, 7)) // 2023-01 형태로 저장
                        .categoryName(key)
                        .totalAmount(totalAmountPerCategory.get(key))
                        .createdAt(LocalDateTime.now())
                        .build());
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
