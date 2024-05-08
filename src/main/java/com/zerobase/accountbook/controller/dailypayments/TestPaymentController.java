package com.zerobase.accountbook.controller.dailypayments;

import com.zerobase.accountbook.service.monthlytotalamount.MonthlyTotalAmountService;
import com.zerobase.accountbook.service.totalamountpercategory.TotalAmountPerCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/daily-payments")
@RequiredArgsConstructor
public class TestPaymentController {

    private final MonthlyTotalAmountService monthlyTotalAmountService;
    private final TotalAmountPerCategoryService totalAmountPerCategoryService;

    @PostMapping("/test-monthly")
    public void updateMonthlyTotal() {
        monthlyTotalAmountService.saveMonthlyTotalAmount();
    }

    @PostMapping("/test-category")
    public void updateCategoryTotal() {
        totalAmountPerCategoryService.saveCategoryPayments();
        System.out.println("OK");
    }
}
