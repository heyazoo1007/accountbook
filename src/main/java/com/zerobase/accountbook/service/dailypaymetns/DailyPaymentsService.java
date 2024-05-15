package com.zerobase.accountbook.service.dailypaymetns;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.dailypayments.dto.request.CreateDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.DeleteDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.ModifyDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.*;
import com.zerobase.accountbook.domain.category.CategoryRepository;
import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmount;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmountRepository;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategory;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategoryRepository;
import com.zerobase.accountbook.service.dailypaymetns.dto.DailyPaymentsCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class DailyPaymentsService {

    private final MemberRepository memberRepository;
    private final DailyPaymentsRepository dailyPaymentsRepository;
    private final CategoryRepository categoryRepository;
    private final TotalAmountPerCategoryRepository totalAmountPerCategoryRepository;
    private final MonthlyTotalAmountRepository monthlyTotalAmountRepository;

    // 클라이언트에서 카테고리 기본값 = "미분류"로 만들어 보내기. 그럼 카테고리 선택 안해도 문제 없음
    public CreateDailyPaymentsResponseDto createDailyPayments(
            String memberEmail,
            CreateDailyPaymentsRequestDto request
    ) {
        Member member = validateMemberByEmail(memberEmail);

        return CreateDailyPaymentsResponseDto.of(
                dailyPaymentsRepository.save(DailyPayments.builder()
                .member(member)
                .paidAmount(request.getPaidAmount())
                .payLocation(request.getPayLocation())
                .methodOfPayment(request.getMethodOfPayment())
                .categoryId(request.getCategoryId())
                .memo(request.getMemo())
                .date(request.getDate())
                .createdAt(LocalDateTime.now())
                .build()));
    }

    public ModifyDailyPaymentsResponseDto modifyDailyPayments(
            String memberEmail,
            ModifyDailyPaymentsRequestDto request
    ) {
        Member member = validateMemberByEmail(memberEmail);

        DailyPayments dailyPayments =
                validateDailyPayments(request.getPaymentId());

        // 지출내역의 주인과 수정하려는 사용자가 다를 경우
        checkDailyPaymentsOwner(member, dailyPayments);

        dailyPayments.setPaidAmount(request.getPaidAmount());
        dailyPayments.setPayLocation(request.getPayLocation());
        dailyPayments.setMethodOfPayment(request.getMethodOfPayment());
        dailyPayments.setCategoryId(request.getCategoryId());
        dailyPayments.setMemo(request.getMemo());
        dailyPayments.setDate(request.getDate());
        dailyPayments.setUpdatedAt(LocalDateTime.now());

        return ModifyDailyPaymentsResponseDto.of(
                dailyPaymentsRepository.save(dailyPayments));
    }

    //@CacheEvict(value = "dailyPayments", allEntries = true)
    public void deleteDailyPayments(
            String memberEmail, DeleteDailyPaymentsRequestDto request
    ) {
        Member member = validateMemberByEmail(memberEmail);

        DailyPayments dailyPayments =
                validateDailyPayments(request.getDailyPaymentsId());

        checkDailyPaymentsOwner(member, dailyPayments);

        dailyPaymentsRepository.delete(dailyPayments);
    }

    public GetDailyPaymentsResponseDto getDailyPayment(Long dailyPaymentId) {
        DailyPayments dailyPayment = validateDailyPayments(dailyPaymentId);
        String categoryName = categoryRepository.
                findById(dailyPayment.getCategoryId()).get().getCategoryName();

        return GetDailyPaymentsResponseDto.of(dailyPayment, categoryName);
    }

    @Cacheable("dailyPayments")
    public List<GetDailyPaymentsResponseDto> getDailyPaymentsList(
            String memberEmail,
            String date
    ) {
        List<DailyPayments> dailyPayments = dailyPaymentsRepository
                        .findAllByMemberIdAndDateContaining(validateMemberByEmail(memberEmail).getId(), date);

        List<GetDailyPaymentsResponseDto> responseDtos = new ArrayList<>();
        for (DailyPayments dailyPayment : dailyPayments) {
            String categoryName = categoryRepository.
                    findById(dailyPayment.getCategoryId()).get().getCategoryName();
            responseDtos.add(GetDailyPaymentsResponseDto.of(dailyPayment, categoryName));
        }
        return responseDtos;
    }

    public List<SearchDailyPaymentsResponseDto> searchDailyPayments(
            String memberEmail, String keyword
    ) {
        Member member = validateMemberByEmail(memberEmail);

        return dailyPaymentsRepository
                .searchKeyword(member.getId(), keyword)
                .stream()
                .map(SearchDailyPaymentsResponseDto::of)
                .collect(Collectors.toList());
    }

    public GetMonthlyResultResponseDto getMonthlyDailyPaymentsResult(
            String memberEmail, String requestDate
    ) {
        Long memberId = validateMemberByEmail(memberEmail).getId();
        YearMonth currentDate = YearMonth.from(LocalDate.now());
        String yearMonth = getYearMonthString(currentDate);

        // 이전 달의 내역을 조회할 때 -> 이전에 저장 해놓은 데이터 가져오면 됨
        if (!yearMonth.equals(requestDate)) {
            return getPastMonthlyResult(requestDate, memberId);
        }

        // 이번 달의 지출 내역 가져오기 -> 이번 달 지출 따로 계산해서 진행
        return getCurrentMonthlyResult(currentDate, memberId);
    }

    // 데이터가 없는 미래 데이터는 기본값 담기
    public GetYearlyResultResponseDto getYearlyResult(
            String memberEmail, String year
    ) {
        Long memberId = validateMemberByEmail(memberEmail).getId();

        // 한달별 총 금액을 다 더하면 연 총 지출금액
        Integer totalAmountOfTheYear = monthlyTotalAmountRepository.sumOfTheYearByMemberId(year, memberId);
        if (totalAmountOfTheYear == null) { totalAmountOfTheYear = 0; }

        // 카테고리별 다달이 금액을 모두 더하면 카테고리 연 총 지출금액
        List<DailyPaymentsCategoryDto> totalAmountOfTheYearPerCategory =
                dailyPaymentsRepository.findYearlyCategory(year, memberId);

        return GetYearlyResultResponseDto.of(
                totalAmountOfTheYear, totalAmountOfTheYearPerCategory);
    }

    public void forbiddenMember(String memberEmail, Long requestMemberId) {
        if (!(validateMemberByEmail(memberEmail).getId()).equals(validateMemberById(requestMemberId).getId())) {
            throw new AccountBookException(
                    "접근할 수 없는 회원 정보입니다.",
                    FORBIDDEN_EXCEPTION
            );
        }
    }

    private GetMonthlyResultResponseDto getPastMonthlyResult(
            String requestDate, Long memberId
    ) {
        // 총 지출금액 가져오기 + 없으면 기본값 0으로 설정
        int monthlyTotalAmount = 0;
        Optional<MonthlyTotalAmount> monthly = monthlyTotalAmountRepository.findByDateAndMemberId(requestDate, memberId);
        if (monthly.isPresent()) {
            monthlyTotalAmount = monthly.get().getTotalAmount();
        }

        // 날짜, 사용자를 기준으로 저장된 데이터 가져온 다음 카테고리랑 금액 넘김
        List<TotalAmountPerCategory> all = totalAmountPerCategoryRepository
                        .findByDateAndMemberId(requestDate, memberId);

        List<MonthlyResultDto> list = new ArrayList<>();
        for (TotalAmountPerCategory each : all) {
            list.add(MonthlyResultDto.of(
                    categoryRepository.findById(each.getCategoryId()).get().getCategoryName(),
                    each.getTotalAmount())
            );
        }

        return GetMonthlyResultResponseDto.of(monthlyTotalAmount, list);
    }

    private GetMonthlyResultResponseDto getCurrentMonthlyResult(YearMonth currentDate, Long memberId) {
        String startDate = currentDate.atDay(1).toString();
        String endDate = currentDate.atEndOfMonth().toString();

        // 카테고리별 월 지출내역 조회
        List<DailyPaymentsCategoryDto> all = dailyPaymentsRepository.
                findMonthlyCategory(startDate, endDate, memberId);

        int totalAmount = 0;
        List<MonthlyResultDto> list = new ArrayList<>();
        for (DailyPaymentsCategoryDto each : all) {
            // 카테고리별 총 지출금액 list 에 저장
            list.add(MonthlyResultDto.of(each.getCategoryName(), each.getTotalAmount()));
            totalAmount += each.getTotalAmount();
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

    private static String getYearMonthString(YearMonth currentDate) {
        String year = String.format("%04d", currentDate.getYear());
        String month = String.format("%02d", currentDate.getMonthValue());
        return year + "-" + month;
    }

    private DailyPayments validateDailyPayments(Long dailyPaymentsId) {
        return dailyPaymentsRepository.findById(dailyPaymentsId).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 지출내역 입니다.",
                        NOT_FOUND_DAILY_PAYMENTS_EXCEPTION));
    }

    private Member validateMemberByEmail(String memberEmail) {
        return memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 회원입니다.",
                        NOT_FOUND_USER_EXCEPTION));
    }

    private Member validateMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 회원입니다.",
                        NOT_FOUND_USER_EXCEPTION));
    }
}
