package com.zerobase.accountbook.controller.dailypayments;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountBookController {

    @GetMapping("/my-accountbook")
    public String myAccountBook() {
        return "/my-accountbook";
    }

    @GetMapping("/expenditure") // 지출 입력하는 페이지로 이동하기
    public String expenditure() {
        return "/expenditure";
    }
}
