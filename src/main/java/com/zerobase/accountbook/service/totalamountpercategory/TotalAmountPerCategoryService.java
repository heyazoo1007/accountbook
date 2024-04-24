package com.zerobase.accountbook.service.totalamountpercategory;


import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategory;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategoryRepository;
import com.zerobase.accountbook.service.dailypaymetns.dto.DailyPaymentsCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.zerobase.accountbook.common.exception.ErrorCode.NOT_FOUND_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class TotalAmountPerCategoryService {

    private final MemberRepository memberRepository;
    private final DailyPaymentsRepository dailyPaymentsRepository;
    private final TotalAmountPerCategoryRepository totalAmountPerCategoryRepository;

    @Scheduled(cron = "0 0 0 1 * * *") // 매달 1일 정각에 모든 사용자에 지난 한 달 동안의 지출내역 카테고리별로 저장
    private void saveEachPayments() {
        LocalDateTime oneMonthBefore = LocalDateTime.now().minusMonths(1);
        String yearMonth = String.format("%04d", oneMonthBefore.getYear()) + "-" +
                String.format("%02d", oneMonthBefore.getMonthValue());

        // 100명 단위로 페이징한 멤버에 대해서 진행
        int totalMember = Math.toIntExact(memberRepository.countBy());
        for (int i = 0; i < totalMember / 100 + 1; i++) {
            Page<Member> members = memberRepository.findAll(PageRequest.of(i, 100));
            for (Member each : members) {
                Member member = validateMemberById(each.getId());

                // 월마다 카테고리별 지출정보 가져오기
                List<DailyPaymentsCategoryDto> all = dailyPaymentsRepository.
                        findMonthlyCategory(yearMonth, yearMonth, member.getId());

                for (DailyPaymentsCategoryDto dto : all) {
                    totalAmountPerCategoryRepository.save(
                            TotalAmountPerCategory.builder()
                                    .member(member)
                                    .date(yearMonth) // YYYY-mm 형태로 저장
                                    .categoryId(dto.getCategoryId())
                                    .totalAmount(dto.getTotalAmount())
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
