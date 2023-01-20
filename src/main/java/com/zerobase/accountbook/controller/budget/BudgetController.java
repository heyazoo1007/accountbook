package com.zerobase.accountbook.controller.budget;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.budget.dto.request.CreateBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.request.ModifyBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.response.CreateBudgetResponseDto;
import com.zerobase.accountbook.controller.budget.dto.response.GetBudgetResponseDto;
import com.zerobase.accountbook.controller.budget.dto.response.ModifyBudgetResponseDto;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.service.budget.BudgetService;
import com.zerobase.accountbook.service.dailypaymetns.DailyPaymentsService;
import com.zerobase.accountbook.service.firebase.FirebaseCloudMessageService;
import com.zerobase.accountbook.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

@RequestMapping("/v1/budget")
@RestController
@RequiredArgsConstructor
public class BudgetController {

    private final MemberService memberService;
    private final BudgetService budgetService;

    private final DailyPaymentsService dailyPaymentsService;

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @PostMapping()
    public ApiResponse<CreateBudgetResponseDto> createBudget(
            @Valid @RequestBody CreateBudgetRequestDto request
    ) {
        CreateBudgetResponseDto response =
                budgetService.createBudget(request);
        return ApiResponse.success(response);
    }

    @PutMapping()
    public ApiResponse<ModifyBudgetResponseDto> modifyBudget(
            @Valid @RequestBody ModifyBudgetRequestDto request
    ) {
        ModifyBudgetResponseDto response =
                budgetService.modifyBudget(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{budgetYearMonth}")
    public ApiResponse<GetBudgetResponseDto> getBudget(
            @AuthenticationPrincipal UserDetails user,
            @DateTimeFormat(pattern = "yyyy-MM")
            @PathVariable String budgetYearMonth
    ) {
        GetBudgetResponseDto response =
                budgetService.getBudget(user.getUsername(), budgetYearMonth);
        return ApiResponse.success(response);
    }

    @Scheduled(cron = "0 0 9 * * * *") // 매일 아침 9시에 예산 알림 전송
    @PostMapping("/fcm")
    public ApiResponse<String> sendBudgetFcm() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String budgetYearMonth = now.getYear() + "-" + now.getMonthValue();

        int totalMember = memberService.getNumberOfTotalMember();
        for (int i = 0; i < totalMember / 100 + 1; i++) {
            Page<Member> members =
                    memberService.findAllMemberByPaging(i, 100);
            for (Member each : members) {
                String memberEmail = each.getEmail();

                // budgetService 에서 한 달 예산 가져오기
                int monthlyBudget = getMonthlyBudget(budgetYearMonth, memberEmail);
                // dailyPaymentsService 에서 그 때까지 쓴 돈 가져오기
                int totalAmountSoFar = getTotalAmountSoFar(budgetYearMonth, memberEmail);

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
        return ApiResponse.SUCCESS;
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

    private int getTotalAmountSoFar(String budgetYearMonth, String memberEmail) {
        int totalAmountSoFar = dailyPaymentsService.getPaidAmountOfTheMonth(
                        memberEmail, budgetYearMonth
        );

        return totalAmountSoFar;
    }

    private int getMonthlyBudget(String budgetYearMonth, String memberEmail) {
        int monthlyBudget = budgetService
                .getBudget(memberEmail, budgetYearMonth)
                .getMonthlyBudget();

        return monthlyBudget;
    }
}
