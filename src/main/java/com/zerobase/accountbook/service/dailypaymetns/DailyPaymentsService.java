package com.zerobase.accountbook.service.dailypaymetns;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.dailypayments.dto.request.CreateDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.DeleteDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.ModifyDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.*;
import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmountRepository;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategory;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategoryRepository;
import com.zerobase.accountbook.service.dailypaymetns.dto.DailyPaymentsCategoryDto;
import com.zerobase.accountbook.service.dailypaymetns.querydsl.DailyPaymentsQueryDsl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class DailyPaymentsService {

    private final DailyPaymentsQueryDsl dailyPaymentsQueryDsl;
    private final MemberRepository memberRepository;
    private final DailyPaymentsRepository dailyPaymentsRepository;
    private final TotalAmountPerCategoryRepository totalAmountPerCategoryRepository;
    private final MonthlyTotalAmountRepository monthlyTotalAmountRepository;

    public CreateDailyPaymentsResponseDto createDailyPayments(
            String memberEmail,
            CreateDailyPaymentsRequestDto request
    ) {

        Member member = validateMember(memberEmail);

        // 해시태그 부분 추후에 추가 예정
        return CreateDailyPaymentsResponseDto.of(
                dailyPaymentsRepository.save(DailyPayments.builder()
                .member(member)
                .paidAmount(request.getPaidAmount())
                .paidWhere(request.getPaidWhere())
                .methodOfPayment(request.getMethodOfPayment())
                .categoryName(request.getCategoryName())
                .memo(request.getMemo())
                .createdAt(getCurrentTimeUntilMinutes())
                .build()));
    }

    @CachePut(value = "dailyPayments", key = "#request.dailyPaymentsId")
    public ModifyDailyPaymentsResponseDto modifyDailyPayments(
            String memberEmail,
            ModifyDailyPaymentsRequestDto request
    ) {

        Member member = validateMember(memberEmail);

        DailyPayments dailyPayments =
                validateDailyPayments(request.getDailyPaymentsId());

        // 지출내역의 주인과 수정하려는 사용자가 다를 경우
        checkDailyPaymentsOwner(member, dailyPayments);

        dailyPayments.setPaidAmount(request.getPaidAmount());
        dailyPayments.setPaidWhere(request.getPaidWhere());
        dailyPayments.setMethodOfPayment(request.getMethodOfPayment());
        dailyPayments.setCategoryName(request.getCategoryName());
        dailyPayments.setMemo(request.getMemo());
        dailyPayments.setUpdatedAt(request.getCreatedAt());

        return ModifyDailyPaymentsResponseDto.of(
                dailyPaymentsRepository.save(dailyPayments)
        );
    }

    @CacheEvict(value = "dailyPayments", allEntries = true)
    public void deleteDailyPayments(
            String memberEmail, DeleteDailyPaymentsRequestDto request
    ) {
        Member member = validateMember(memberEmail);

        DailyPayments dailyPayments =
                validateDailyPayments(request.getDailyPaymentsId());

        checkDailyPaymentsOwner(member, dailyPayments);

        dailyPaymentsRepository.delete(dailyPayments);
    }

    @Transactional(readOnly = true)
    public GetDailyPaymentsResponseDto getDailyPayment(
            String memberEmail, Long dailyPaymentsId
    ) {

        Member member = validateMember(memberEmail);

        DailyPayments dailyPayments = validateDailyPayments(dailyPaymentsId);

        checkDailyPaymentsOwner(member, dailyPayments);

        return GetDailyPaymentsResponseDto.of(dailyPayments);
    }

    @Transactional(readOnly = true)
    @Cacheable("dailyPayments")
    public List<GetDailyPaymentsResponseDto> getDailyPaymentsList(
            String memberEmail,
            String date
    ) {
        Long memberId = validateMember(memberEmail).getId();

        List<DailyPayments> all =
                dailyPaymentsRepository
                        .findAllByMemberIdAndCreatedAtContaining(memberId, date);

        return all.stream()
                .map(GetDailyPaymentsResponseDto:: of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SearchDailyPaymentsResponseDto> searchDailyPayments(
            String memberEmail, String keyword
    ) {
        Member member = validateMember(memberEmail);

        return dailyPaymentsRepository
                .searchKeyword(member.getId(), keyword)
                .stream()
                .map(SearchDailyPaymentsResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GetMonthlyResultResponseDto
    getMonthlyDailyPaymentsResult(String memberEmail, String requestDate) {
        Long memberId = validateMember(memberEmail).getId();

        String currentDate = getCurrentTimeUntilMinutes().substring(0, 7);

        // 이전 달의 내역을 조회할 때
        if (!currentDate.equals(requestDate)) {
            return getPastMonthlyResult(requestDate, memberId);
        }

        // 이번 달의 지출 내역 가져오기
        return getCurrentMonthlyResult(memberId);
    }

    // 지난 년도들만 확인할 수 있음
    public GetYearlyResultResponseDto getYearlyResult(
            String memberEmail, String year
    ) {
        Long memberId = validateMember(memberEmail).getId();

        int currentYear = LocalDateTime.now().getYear();
        if (Integer.parseInt(year) - currentYear >= 0) {
            throw new AccountBookException(
                    "해당 년도는 조회할 수 없습니다.",
                    Not_FOUND_YEARLY_RESULT_EXCEPTION
            );
        }


        // 한달별 총 금액을 다 더하면 연 총 지출금액
        Integer totalAmountOfTheYear = monthlyTotalAmountRepository
                .sumByMemberIdAndDateInfoContainingYear(memberId, year);

        // 카테고리별 다달이 금액을 모두 더하면 카테고리 연 총 지출금액
        List<DailyPaymentsCategoryDto> totalAmountOfTheYearPerCategory =
                dailyPaymentsQueryDsl
                        .getYearlyTotalAmountPerCategoryByMemberId(
                                memberId, year
                        );

        return GetYearlyResultResponseDto.of(
                totalAmountOfTheYear, totalAmountOfTheYearPerCategory);
    }

    private GetMonthlyResultResponseDto getPastMonthlyResult(
            String requestDate, Long memberId
    ) {
        // 총 사용 금액 가져오기
        int totalAmount = monthlyTotalAmountRepository
                .findByDateInfoAndMemberId(requestDate, memberId).orElseThrow(
                        () -> new AccountBookException(
                                "해당 월에 총 지출이 존재하지 않습니다.",
                                NOT_FOUND_MONTHLY_TOTAL_AMOUNT_EXCEPTION
                        )
                ).getTotalAmount();

        // 날짜, 사용자를 기준으로 저장된 데이터 가져온 다음 카테고리랑 금액 넘겨서 진행
        List<TotalAmountPerCategory> all =
                totalAmountPerCategoryRepository
                        .findByDateInfoAndMemberId(requestDate, memberId);

        List<MonthlyResultDto> list = new ArrayList<>();
        for (TotalAmountPerCategory each : all) {
            list.add(MonthlyResultDto.of(
                    each.getCategoryName(),
                    each.getTotalAmount()
            ));
        }

        return GetMonthlyResultResponseDto.of(totalAmount, list);
    }

    private GetMonthlyResultResponseDto getCurrentMonthlyResult(
            Long memberId
    ) {
        int totalAmount = 0;

        // 요청한 사용자의 해당 월 지출내역을 카테고리로 가져옴
        // (카페 : 15000, 식당 : 50000, )
        List<DailyPaymentsCategoryDto> all =
                dailyPaymentsQueryDsl.getTotalAmountPerCategoryByMemberId(
                        getCurrentTimeUntilMinutes().substring(0, 7),
                        memberId
                );

        List<MonthlyResultDto> list = new ArrayList<>();
        for (DailyPaymentsCategoryDto each : all) {
            totalAmount += each.getTotalAmount();
            list.add(MonthlyResultDto.of(
                    each.getCategoryName(),
                    each.getTotalAmount()
            ));
        }

        return GetMonthlyResultResponseDto.of(totalAmount, list);
    }

    private static void checkDailyPaymentsOwner(
            Member member, DailyPayments dailyPayments
    ) {
        if (!dailyPayments.getMember().getId().equals(member.getId())) {
            throw new AccountBookException(
                    "해당 지출내역에 접근할 수 없습니다.",
                    FORBIDDEN_EXCEPTION
            );
        }
    }

    private static String getCurrentTimeUntilMinutes() {

        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();
        int day = LocalDateTime.now().getDayOfMonth();
        int hour = LocalDateTime.now().getHour();
        int minute = LocalDateTime.now().getMinute();

        return year + "-" + String.format("%02d",month) +
                "-" + String.format("%02d",day) +
                " " + String.format("%02d", hour) +
                ":" + String.format("%02d", minute);
    }

    private DailyPayments validateDailyPayments(Long dailyPaymentsId) {
        return dailyPaymentsRepository.findById(dailyPaymentsId).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 지출내역 입니다.",
                        NOT_FOUND_DAILY_PAYMENTS_EXCEPTION
                )
        );
    }

    private Member validateMember(String memberEmail) {
        return memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 회원입니다.",
                        NOT_FOUND_USER_EXCEPTION
                )
        );
    }
}
