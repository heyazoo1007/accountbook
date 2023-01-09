package com.zerobase.accountbook.service.dailypaymetns;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.dailypayments.dto.request.CreateDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.DeleteDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.ModifyDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.CreateDailyPaymentsResponseDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.GetDailyPaymentsResponseDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.ModifyDailyPaymentsResponseDto;
import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class DailyPaymentsService {

    private final DailyPaymentsRepository dailyPaymentsRepository;

    private final MemberRepository memberRepository;

    public CreateDailyPaymentsResponseDto createDailyPayments(CreateDailyPaymentsRequestDto request) {

        Member member = validateMember(request.getMemberEmail());

        // 해시태그 부분 추후에 추가 예정
        return CreateDailyPaymentsResponseDto.of(dailyPaymentsRepository.save(DailyPayments.builder()
                .member(member)
                .paidAmount(request.getPaidAmount())
                .paidWhere(request.getPaidWhere())
                .methodOfPayment(request.getMethodOfPayment())
                .categoryName(request.getCategoryName())
                .createdAt(getCurrentTimeUntilMinutes())
                .build()));
    }

    @CachePut(value = "dailyPayments", key = "#request.dailyPaymentsId")
    public ModifyDailyPaymentsResponseDto modifyDailyPayments(ModifyDailyPaymentsRequestDto request) {

        Member member = validateMember(request.getMemberEmail());

        DailyPayments dailyPayments = validateDailyPayments(request.getDailyPaymentsId());

        // 지출내역의 주인과 수정하려는 사용자가 다를 경우
        forbiddenMember(member, dailyPayments);

        // 해시태그 부분 추후에 수정 예정
        dailyPayments.setPaidAmount(request.getPaidAmount());
        dailyPayments.setPaidWhere(request.getPaidWhere());
        dailyPayments.setMethodOfPayment(request.getMethodOfPayment());
        dailyPayments.setCategoryName(request.getCategoryName());
        dailyPayments.setUpdatedAt(request.getCreatedAt());

        return ModifyDailyPaymentsResponseDto.of(dailyPaymentsRepository.save(dailyPayments));
    }

    @CacheEvict(value = "dailyPayments", allEntries = true)
    public void deleteDailyPayments(DeleteDailyPaymentsRequestDto request) {

        Member member = validateMember(request.getMemberEmail());

        DailyPayments dailyPayments = validateDailyPayments(request.getDailyPaymentsId());

        forbiddenMember(member, dailyPayments);

        dailyPaymentsRepository.delete(dailyPayments);
    }

    @Transactional(readOnly = true)
    public GetDailyPaymentsResponseDto getDailyPayments(Long dailyPaymentsId) {

        DailyPayments dailyPayments = validateDailyPayments(dailyPaymentsId);

        return GetDailyPaymentsResponseDto.of(dailyPayments);
    }

    @Transactional(readOnly = true)
    @Cacheable("dailyPayments")
    public List<GetDailyPaymentsResponseDto> getDailyPaymentsList() {
        List<DailyPayments> all = dailyPaymentsRepository.findAll();

        return all.stream()
                .map(DailyPayments -> GetDailyPaymentsResponseDto.of(DailyPayments))
                .collect(Collectors.toList());

    }

    private static void forbiddenMember(Member member, DailyPayments dailyPayments) {
        if (!dailyPayments.getMember().getId().equals(member.getId())) {
            throw new AccountBookException("해당 지출내역에 접근할 수 없습니다.", FORBIDDEN_EXCEPTION);
        }
    }

    private static String getCurrentTimeUntilMinutes() {

        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();
        int day = LocalDateTime.now().getDayOfMonth();
        int hour = LocalDateTime.now().getHour();
        int minute = LocalDateTime.now().getMinute();

        return year + "-" + month + "-" + day + " " + hour + ":" + minute;
    }

    private DailyPayments validateDailyPayments(Long dailyPaymentsId) {
        Optional<DailyPayments> optionalDailyPayments = dailyPaymentsRepository.findById(dailyPaymentsId);
        if (!optionalDailyPayments.isPresent()) {
            throw new AccountBookException("존재하지 않는 지출내역 입니다.", NOT_FOUND_DAILY_PAYMENTS_EXCEPTION);
        }
        return optionalDailyPayments.get();
    }

    private Member validateMember(String memberEmail) {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberEmail);
        if (!optionalMember.isPresent()) {
            throw new AccountBookException("존재하지 않는 회원입니다.", NOT_FOUND_USER_EXCEPTION);
        }
        Member member = optionalMember.get();
        return member;
    }
}
