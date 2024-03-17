package com.zerobase.accountbook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1")
public class IndexController {

    @GetMapping()
    public String index() {
        return "index";
    }

    @GetMapping("/signup")
    public String createMember() {
        return "signup";
    }

    @GetMapping("/login")
    public String logIn() {
        return "login";
    }
}
