package com.zerobase.accountbook.controller.dailypayments;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountBookController {

    @GetMapping("/my-accountbook")
    public String myAccountBook() {
        return "/my-accountbook";
    }
}
