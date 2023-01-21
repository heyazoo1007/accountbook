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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BudgetService budgetService;

    @Test
    void success_createBudget() {
        //given
        String memberEmail = "hello@abc.com";
        Member member = Member.builder()
                .email(memberEmail)
                .build();
        Budget budget = Budget.builder()
                .monthlyBudget(10000)
                .member(member)
                .build();
        CreateBudgetRequestDto requestDto = CreateBudgetRequestDto.builder()
                .monthlyBudget(10000)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(budgetRepository.findByBudgetYearMonth(anyString()))
                .willReturn(Optional.empty());
        given(budgetRepository.save(any()))
                .willReturn(budget);

        ArgumentCaptor<Member> captorMember =
                ArgumentCaptor.forClass(Member.class);
        ArgumentCaptor<Budget> captorBudget =
                ArgumentCaptor.forClass(Budget.class);

        //when
        CreateBudgetResponseDto responseDto =
                budgetService.createBudget(memberEmail, requestDto);

        //then
        verify(budgetRepository, times(1))
                .save(captorBudget.capture());
        assertEquals(
                responseDto.getMonthlyBudget(),
                captorBudget.getValue().getMonthlyBudget()
        );
        verify(memberRepository, times(1))
                .save(captorMember.capture());
        assertEquals(
                responseDto.getMonthlyBudget(),
                captorMember.getValue().getMonthlyBudget()
        );
    }

    @Test
    void fail_createBudget_존재하지_않는_회원() {
        //given
        String memberEmail = "hello@abc.com";

        CreateBudgetRequestDto requestDto = CreateBudgetRequestDto.builder()
                .monthlyBudget(10000)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> budgetService.createBudget(memberEmail, requestDto)
        );
    }

    @Test
    void fail_createBudget_예산이_이미_생성됨() {
        //given
        String memberEmail = "hello@abc.com";
        Member member = Member.builder()
                .email(memberEmail)
                .build();
        Budget budget = Budget.builder()
                .monthlyBudget(10000)
                .member(member)
                .build();

        CreateBudgetRequestDto requestDto = CreateBudgetRequestDto.builder()
                .monthlyBudget(10000)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(budgetRepository.findByBudgetYearMonth(anyString()))
                .willReturn(Optional.of(budget));

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> budgetService.createBudget(memberEmail, requestDto)
        );
    }

    @Test
    void success_modifyBudget() {
        //given
        String memberEmail = "hello@abc.com";
        Member member = Member.builder()
                .email(memberEmail)
                .build();
        Budget budget = Budget.builder()
                .monthlyBudget(10000)
                .member(member)
                .build();
        ModifyBudgetRequestDto requestDto = ModifyBudgetRequestDto.builder()
                .yearMonth("yyyy-MM")
                .modifyMonthlyBudget(100000)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(budgetRepository.findByBudgetYearMonth(anyString()))
                .willReturn(Optional.of(budget));
        given(budgetRepository.save(any()))
                .willReturn(budget);

        ArgumentCaptor<Member> captorMember = ArgumentCaptor.forClass(Member.class);
        ArgumentCaptor<Budget> captorBudget = ArgumentCaptor.forClass(Budget.class);

        //when
        ModifyBudgetResponseDto responseDto =
                budgetService.modifyBudget(memberEmail, requestDto);

        //then
        verify(budgetRepository, times(1))
                .save(captorBudget.capture());
        assertEquals(
                responseDto.getModifyMonthlyBudget(),
                captorBudget.getValue().getMonthlyBudget()
        );
        verify(memberRepository, times(1))
                .save(captorMember.capture());
        assertEquals(
                responseDto.getModifyMonthlyBudget(),
                captorMember.getValue().getMonthlyBudget()
        );
    }

    @Test
    void fail_modifyBudget_존재하지_않는_회원() {
        //given
        String memberEmail = "hello@abc.com";

        ModifyBudgetRequestDto requestDto = ModifyBudgetRequestDto.builder()
                .yearMonth("yyyy-MM")
                .modifyMonthlyBudget(10000)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> budgetService.modifyBudget(memberEmail, requestDto)
        );
    }

    @Test
    void fail_modifyBudget_존재하지_않는_예산() {
        //given
        String memberEmail = "hello@abc.com";
        Member member = Member.builder()
                .email(memberEmail)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(budgetRepository.findByBudgetYearMonth(anyString()))
                .willReturn(Optional.empty());

        ModifyBudgetRequestDto requestDto = ModifyBudgetRequestDto.builder()
                .yearMonth("yyyy-MM")
                .modifyMonthlyBudget(10000)
                .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> budgetService.modifyBudget(memberEmail, requestDto)
        );
    }

    @Test
    void fail_modifyBudget_예산_주인이_아닐때() {
        //given
        String notOwnerEmail = "notOwner@abc.com";
        Member notOwner = Member.builder()
                .email(notOwnerEmail)
                .build();
        Member ownerOfBudget = Member.builder()
                .email("owner@abc.com")
                .build();
        Budget budget = Budget.builder()
                .member(ownerOfBudget)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(notOwner));
        given(budgetRepository.findByBudgetYearMonth(anyString()))
                .willReturn(Optional.of(budget));

        ModifyBudgetRequestDto requestDto = ModifyBudgetRequestDto.builder()
                .yearMonth("yyyy-MM")
                .modifyMonthlyBudget(10000)
                .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> budgetService.modifyBudget(notOwnerEmail, requestDto)
        );
    }

    @Test
    void success_getBudget() {
        //given
        String memberEmail = "hello@abc.com";
        String budgetYearMonth = "yyyy-MM";
        Member member = Member.builder()
                .email(memberEmail)
                .build();
        Budget budget = Budget.builder()
                .monthlyBudget(10000)
                .member(member)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(budgetRepository.findByBudgetYearMonth(anyString()))
                .willReturn(Optional.of(budget));

        //when
        GetBudgetResponseDto responseDto =
                budgetService.getBudget(memberEmail, budgetYearMonth);

        //then
        assertEquals(responseDto.getMonthlyBudget(), budget.getMonthlyBudget());
        assertEquals(responseDto.getMemberName(), member.getMemberName());
    }

    @Test
    void fail_getBudget_존재하지_않는_예산() {
        //given
        String memberEmail = "hello@abc.com";
        String budgetYearMonth = "yyyy-MM";

        given(budgetRepository.findByBudgetYearMonth(anyString()))
                .willReturn(Optional.empty());

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> budgetService.getBudget(memberEmail, budgetYearMonth)
        );
    }

    @Test
    void fail_getBudget_존재하지_않는_회원() {
        //given
        String memberEmail = "hello@abc.com";
        String budgetYearMonth = "yyyy-MM";

        Budget budget = Budget.builder()
                .budgetYearMonth(budgetYearMonth)
                .build();

        given(budgetRepository.findByBudgetYearMonth(anyString()))
                .willReturn(Optional.of(budget));
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> budgetService.getBudget(memberEmail, budgetYearMonth)
        );
    }

    @Test
    void fail_getBudget_예산_주인이_아닐때() {
        //given
        String notOwnerEmail = "notOwner@abc.com";
        String budgetYearMonth = "yyyy-MM";
        Member notOwner = Member.builder()
                .email(notOwnerEmail)
                .build();
        Member ownerOfBudget = Member.builder()
                .email("owner@abc.com")
                .build();
        Budget budget = Budget.builder()
                .member(ownerOfBudget)
                .budgetYearMonth(budgetYearMonth)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(notOwner));
        given(budgetRepository.findByBudgetYearMonth(anyString()))
                .willReturn(Optional.of(budget));

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> budgetService.getBudget(notOwnerEmail, budgetYearMonth)
        );
    }
}