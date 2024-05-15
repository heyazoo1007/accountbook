package com.zerobase.accountbook.controller.index;

import com.zerobase.accountbook.controller.dailypayments.dto.response.GetDailyPaymentsResponseDto;
import com.zerobase.accountbook.service.dailypaymetns.DailyPaymentsService;
import com.zerobase.accountbook.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class AccountBookController {

    private final DailyPaymentsService dailyPaymentsService;
    private final MemberService memberService;

    @GetMapping("/my-accountbook/{memberId}")
    public String myAccountBook(@AuthenticationPrincipal UserDetails user,
                                @PathVariable long memberId) {
        dailyPaymentsService.forbiddenMember(user.getUsername(), memberId);
        return "my-accountbook";
    }

    @GetMapping("/expenditure")
    public String expenditure() {
        return "expenditure";
    }

    @GetMapping("/expenditure/{paymentId}")
    public String modifyExpenditure(@PathVariable long paymentId, Model model) {
        GetDailyPaymentsResponseDto response = dailyPaymentsService
                .getDailyPayment(paymentId);
        model.addAttribute("payment", response);
        return "edit-expenditure";
    }

    @GetMapping("/monthly")
    public String getMonthly() {
        return "monthly";
    }

    @GetMapping("/yearly")
    public String getYearly() {
        return "yearly";
    }

    @GetMapping("/my-page/{memberId}")
    public String getMyPage(@PathVariable long memberId, Model model) {
        model.addAttribute("member", memberService.getMemberInfoById(memberId));
        return "my-page";
    }
}
