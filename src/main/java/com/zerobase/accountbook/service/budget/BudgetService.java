package com.zerobase.accountbook.service.budget;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.budget.dto.request.CreateBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.request.ModifyBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.response.CreateBudgetResponseDto;
import com.zerobase.accountbook.controller.budget.dto.response.GetBudgetResponseDto;
import com.zerobase.accountbook.controller.budget.dto.response.ModifyBudgetResponseDto;
import com.zerobase.accountbook.domain.budget.Budget;
import com.zerobase.accountbook.domain.budget.BudgetRepository;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.service.firebase.FirebaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;
import static com.zerobase.accountbook.common.exception.ErrorCode.FORBIDDEN_EXCEPTION;
import static com.zerobase.accountbook.common.exception.ErrorCode.NOT_FOUND_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    private final MemberRepository memberRepository;

    private final DailyPaymentsRepository dailyPaymentsRepository;

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    // TODO 매달 첫번째 날에 사용자가 로그인 하면 생성하라고 알람 보내기
    public CreateBudgetResponseDto createBudget(CreateBudgetRequestDto request
    ) {

        Member member = validateMember(request.getMemberEmail());

        // 이미 생성된 예산에 대해서는 생성할 수 없고, 따로 수정 버튼을 눌러야 합니다.
        Optional<Budget> optionalBudget =
                budgetRepository.findByBudgetYearMonth(YearMonth.now().toString());
        if (optionalBudget.isPresent()) {
            throw new AccountBookException(
                    "이미 해당 달에 예산이 설정 되어 있습니다.",
                    CONFLICT_MONTHLY_BUDGET_EXCEPTION
            );
        }

        member.setMonthlyBudget(request.getMonthlyBudget());
        memberRepository.save(member);

        return CreateBudgetResponseDto.of(
                budgetRepository.save(
                        Budget.builder()
                                .budgetYearMonth(YearMonth.now().toString())
                                .member(member)
                                .monthlyBudget(request.getMonthlyBudget())
                                .createdAt(LocalDateTime.now())
                                .build()
                ));
    }

    public ModifyBudgetResponseDto modifyBudget(ModifyBudgetRequestDto request
    ) {

        Member member = validateMember(request.getMemberEmail());

        Budget budget = validateBudget(member.getId(), request.getYearMonth());

        checkBudgetOwner(member, budget);

        budget.setMonthlyBudget(request.getModifyMonthlyBudget());

        return ModifyBudgetResponseDto.of(budgetRepository.save(budget));
    }

    public GetBudgetResponseDto getBudget(
            String memberEmail, String budgetYearMonth
    ) {
        Budget budget = validateBudget(
                validateMember(memberEmail).getId(),
                budgetYearMonth
        );

        return GetBudgetResponseDto.of(budget);
    }

    @Scheduled(cron = "0 0 9 * * * *") // 매일 아침 9시에 예산 알림 전송
    public void sendBudgetAlarm() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String budgetYearMonth = now.getYear() + "-" + now.getMonthValue();

        int totalMember = Math.toIntExact(memberRepository.countBy());
        for (int i = 0; i < totalMember / 100 + 1; i++) {
            Page<Member> members = memberRepository.findAll(
                    PageRequest.of(i, 100)
            );
            for (Member each : members) {

                // budgetService 에서 한 달 예산 가져오기
                int monthlyBudget = getMonthlyBudget(budgetYearMonth, each.getEmail());
                // dailyPaymentsService 에서 그 때까지 쓴 돈 가져오기
                int totalAmountSoFar = getTotalAmountSoFar(budgetYearMonth, each.getId());

                // 예산 - 총 지출 금액 남았다는 메시지 전달하면 됨
                int diffBetweenBudgetAndPaid = monthlyBudget - totalAmountSoFar;
                if (diffBetweenBudgetAndPaid >= 0) {
                    sendNormalBudgetAlarm("", diffBetweenBudgetAndPaid);
                }
                if (diffBetweenBudgetAndPaid < 0) {
                    sendExceedBudgetAlarm("");
                }
            }
        }
    }

    private void sendExceedBudgetAlarm(String targetToken) throws IOException {
        String message = "이번 달 예산을 초과했습니다! 예산을 다시 설정하시겠어요?";
        firebaseCloudMessageService.sendMessageTo(
                targetToken, "오늘의 예산", message
        );
    }

    private void sendNormalBudgetAlarm(
            String targetToken, int diffBetweenBudgetAndPaid
    ) throws IOException {
        String message =
                "이번 달 남은 예산은 " + diffBetweenBudgetAndPaid + "원 입니다.";
        firebaseCloudMessageService.sendMessageTo(
                targetToken, "오늘의 예산", message
        );
    }

    private int getTotalAmountSoFar(String budgetYearMonth, Long memberId) {
        int totalAmountSoFar =
                dailyPaymentsRepository.totalPaidAmountSoFarByMemberId(
                memberId, budgetYearMonth
        );

        return totalAmountSoFar;
    }

    private int getMonthlyBudget(String budgetYearMonth, String memberEmail) {
        int monthlyBudget =
                getBudget(memberEmail, budgetYearMonth).getMonthlyBudget();

        return monthlyBudget;
    }

    private static void checkBudgetOwner(Member member, Budget budget) {
        Member budgetOwner = budget.getMember();
        if (!budgetOwner.equals(member)) {
            throw new AccountBookException(
                    "접근할 수 없는 예산입니다.",
                    FORBIDDEN_EXCEPTION
            );
        }
    }

    private Budget validateBudget(Long memberId, String budgetYearMonth) {

        Budget budget = budgetRepository.findByBudgetYearMonth(budgetYearMonth)
                .orElseThrow(() -> new AccountBookException(
                        "존재하지 않는 예산입니다.",
                        NOT_FOUND_BUDGET_EXCEPTION));

        if (!budget.getMember().getId().equals(memberId)) {
            throw new AccountBookException(
                    "예산의 소유자가 아닙니다.",
                    FORBIDDEN_EXCEPTION
            );
        }
        return budget;
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
