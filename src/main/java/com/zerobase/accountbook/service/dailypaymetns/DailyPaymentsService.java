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
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmount;
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

        // ??????????????? ????????? ??????????????? ???????????? ?????? ??????
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

        // ?????? ?????? ????????? ????????? ???
        if (!currentDate.equals(requestDate)) {
            return getPastMonthlyResult(requestDate, memberId);
        }

        // ?????? ?????? ?????? ?????? ????????????
        return getCurrentMonthlyResult(memberId);
    }

    // ?????? ???????????? ????????? ??? ??????
    public GetYearlyResultResponseDto getYearlyResult(
            String memberEmail, String year
    ) {
        Long memberId = validateMember(memberEmail).getId();

        int currentYear = LocalDateTime.now().getYear();
        if (Integer.parseInt(year) - currentYear >= 0) {
            throw new AccountBookException(
                    "?????? ????????? ????????? ??? ????????????.",
                    Not_FOUND_YEARLY_RESULT_EXCEPTION
            );
        }

        // ????????? ??? ????????? ??? ????????? ??? ??? ????????????
        Integer totalAmountOfTheYear = monthlyTotalAmountRepository
                .sumByMemberIdAndDateInfoContainingYear(memberId, year);

        // ??????????????? ????????? ????????? ?????? ????????? ???????????? ??? ??? ????????????
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
        // ??? ?????? ?????? ????????????
        MonthlyTotalAmount monthlyTotalAmount = monthlyTotalAmountRepository
                .findByDateInfoAndMemberId(requestDate, memberId).orElseThrow(
                        () -> new AccountBookException(
                                "?????? ?????? ??? ????????? ???????????? ????????????.",
                                NOT_FOUND_MONTHLY_TOTAL_AMOUNT_EXCEPTION
                        )
                );

        // ??????, ???????????? ???????????? ????????? ????????? ????????? ?????? ??????????????? ?????? ????????? ??????
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

        return GetMonthlyResultResponseDto.of(
                monthlyTotalAmount.getTotalAmount(), list
        );
    }

    private GetMonthlyResultResponseDto getCurrentMonthlyResult(Long memberId) {

        int totalAmount = 0;

        // ????????? ???????????? ?????? ??? ??????????????? ??????????????? ?????????
        // (?????? : 15000, ?????? : 50000, )
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
                    "?????? ??????????????? ????????? ??? ????????????.",
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
                        "???????????? ?????? ???????????? ?????????.",
                        NOT_FOUND_DAILY_PAYMENTS_EXCEPTION
                )
        );
    }

    private Member validateMember(String memberEmail) {
        return memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new AccountBookException(
                        "???????????? ?????? ???????????????.",
                        NOT_FOUND_USER_EXCEPTION
                )
        );
    }
}
