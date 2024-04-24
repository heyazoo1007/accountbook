package com.zerobase.accountbook.controller.index;

import com.zerobase.accountbook.controller.dailypayments.dto.response.GetDailyPaymentsResponseDto;
import com.zerobase.accountbook.service.dailypaymetns.DailyPaymentsService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
@RequiredArgsConstructor
public class AccountBookController {

    private final DailyPaymentsService dailyPaymentsService;

    @GetMapping("/my-accountbook/{memberId}")
    public String myAccountBook(@PathVariable long memberId) {
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
    public String monthlyPage() {
        return "monthly";
    }

    @GetMapping("/yearly")
    public String yearlyPage() {
        return "yearly";
    }
}
