package com.zerobase.accountbook.service.dailypaymetns;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.dailypayments.dto.request.CreateDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.DeleteDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.ModifyDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.*;
import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmount;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmountRepository;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategory;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategoryRepository;
import com.zerobase.accountbook.service.dailypaymetns.dto.DailyPaymentsCategoryDto;
import com.zerobase.accountbook.service.dailypaymetns.querydsl.DailyPaymentsQueryDsl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DailyPaymentsServiceTest {

    @Mock
    private MonthlyTotalAmountRepository monthlyTotalAmountRepository;

    @Mock
    private TotalAmountPerCategoryRepository totalAmountPerCategoryRepository;

    @Mock
    private DailyPaymentsQueryDsl dailyPaymentsQueryDsl;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private DailyPaymentsRepository dailyPaymentsRepository;

    @InjectMocks
    private DailyPaymentsService dailyPaymentsService;

    @Test
    void success_createDailyPayments() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder()
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        DailyPayments dailyPayments = DailyPayments.builder()
                .paidAmount(1000)
                .paidWhere("place1")
                .methodOfPayment("card1")
                .categoryName("category1")
                .memo("memo1")
                .build();
        given(dailyPaymentsRepository.save(any()))
                .willReturn(dailyPayments);

        CreateDailyPaymentsRequestDto requestDto =
                CreateDailyPaymentsRequestDto.builder()
                        .paidAmount(1000)
                        .paidWhere("place1")
                        .methodOfPayment("card1")
                        .categoryName("category1")
                        .memo("memo1")
                        .build();

        ArgumentCaptor<DailyPayments> captor =
                ArgumentCaptor.forClass(DailyPayments.class);

        //when
        CreateDailyPaymentsResponseDto responseDto =
                dailyPaymentsService.createDailyPayments(
                        requestEmail,
                        requestDto
                );

        //then
        verify(dailyPaymentsRepository, times(1))
                .save(captor.capture());
        // captor ??? id??? ??????. ???????
        assertEquals(
                captor.getValue().getId(),
                responseDto.getDailyPaymentsId()
        );
        assertEquals(
                captor.getValue().getPaidAmount(),
                responseDto.getPaidAmount()
        );
        assertEquals(
                captor.getValue().getPaidWhere(),
                responseDto.getPaidWhere()
        );
        assertEquals(
                captor.getValue().getCategoryName(),
                responseDto.getCategoryName()
        );
        assertEquals(
                captor.getValue().getMethodOfPayment(),
                responseDto.getMethodOfPayment()
        );
        assertEquals(
                captor.getValue().getMemo(),
                responseDto.getMemo()
        );
    }

    @Test
    void fail_createDailyPayments_????????????_??????_??????() {
        //given
        String requestEmail = "hello@abc.com";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        CreateDailyPaymentsRequestDto requestDto =
                CreateDailyPaymentsRequestDto.builder()
                        .paidAmount(1000)
                        .paidWhere("place1")
                        .methodOfPayment("card1")
                        .categoryName("category1")
                        .memo("memo1")
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.createDailyPayments(
                        requestEmail, requestDto
                )
        );
    }

    @Test
    void success_modifyDailyPayments() {
        //given
        String requestEmail = "hello@abc.com";
        Member owner = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(owner));

        DailyPayments dailyPayments = DailyPayments.builder()
                .id(1L)
                .member(owner)
                .paidAmount(1000)
                .paidWhere("place1")
                .methodOfPayment("card1")
                .categoryName("category1")
                .memo("memo1")
                .build();
        given(dailyPaymentsRepository.save(any()))
                .willReturn(dailyPayments);
        given(dailyPaymentsRepository.findById(anyLong()))
                .willReturn(Optional.of(dailyPayments));

        ModifyDailyPaymentsRequestDto requestDto =
                ModifyDailyPaymentsRequestDto.builder()
                    .dailyPaymentsId(1L)
                    .paidAmount(2000)
                    .paidWhere("place2")
                    .methodOfPayment("card2")
                    .categoryName("category2")
                    .memo("memo2")
                    .build();

        ArgumentCaptor<DailyPayments> captor =
                ArgumentCaptor.forClass(DailyPayments.class);

        //when
        ModifyDailyPaymentsResponseDto responseDto =
                dailyPaymentsService.modifyDailyPayments(
                        requestEmail,
                        requestDto
                );

        //then
        verify(dailyPaymentsRepository, times(1))
                .save(captor.capture());
        assertEquals(
                captor.getValue().getId(),
                responseDto.getDailyPaymentsId()
        );
        assertEquals(
                captor.getValue().getPaidAmount(),
                responseDto.getPaidAmount()
        );
        assertEquals(
                captor.getValue().getPaidWhere(),
                responseDto.getPaidWhere()
        );
        assertEquals(
                captor.getValue().getMethodOfPayment(),
                responseDto.getMethodOfPayment()
        );
        assertEquals(
                captor.getValue().getCategoryName(),
                responseDto.getCategoryName()
        );
        assertEquals(
                captor.getValue().getMemo(),
                responseDto.getMemo()
        );
    }

    @Test
    void fail_modifyDailyPayments_????????????_??????_??????() {
        //given
        String requestEmail = "hello@abc.com";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        ModifyDailyPaymentsRequestDto requestDto =
                ModifyDailyPaymentsRequestDto.builder()
                        .dailyPaymentsId(1L)
                        .paidAmount(2000)
                        .paidWhere("place2")
                        .methodOfPayment("card2")
                        .categoryName("category2")
                        .memo("memo2")
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.modifyDailyPayments(
                        requestEmail,
                        requestDto
                )
        );
    }

    @Test
    void fail_modifyDailyPayments_????????????_??????_????????????() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        given(dailyPaymentsRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        ModifyDailyPaymentsRequestDto requestDto =
                ModifyDailyPaymentsRequestDto.builder()
                        .dailyPaymentsId(1L)
                        .paidAmount(2000)
                        .paidWhere("place2")
                        .methodOfPayment("card2")
                        .categoryName("category2")
                        .memo("memo2")
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.modifyDailyPayments(
                        requestEmail,
                        requestDto
                )
        );
    }

    @Test
    void fail_modifyDailyPayments_????????????_?????????_??????_??????() {
        //given
        String notOwnerEmail = "notOwner@abc.com";
        Member owner = Member.builder()
                .id(1L)
                .email("owner@abc.com")
                .build();
        Member notOwner = Member.builder()
                .id(2L)
                .email(notOwnerEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(notOwner));

        DailyPayments dailyPayments = DailyPayments.builder()
                .id(1L)
                .member(owner)
                .paidAmount(1000)
                .paidWhere("place1")
                .methodOfPayment("card1")
                .categoryName("category1")
                .memo("memo1")
                .build();

        given(dailyPaymentsRepository.findById(anyLong()))
                .willReturn(Optional.of(dailyPayments));

        ModifyDailyPaymentsRequestDto requestDto =
                ModifyDailyPaymentsRequestDto.builder()
                        .dailyPaymentsId(1L)
                        .paidAmount(2000)
                        .paidWhere("place2")
                        .methodOfPayment("card2")
                        .categoryName("category2")
                        .memo("memo2")
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.modifyDailyPayments(
                        notOwnerEmail,
                        requestDto
                )
        );
    }

    @Test
    void success_deleteDailyPayments() {
        //given
        String requestEmail = "hello@abc.com";
        Member owner = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(owner));

        DailyPayments dailyPayments = DailyPayments.builder()
                .id(1L)
                .member(owner)
                .paidAmount(1000)
                .paidWhere("place1")
                .methodOfPayment("card1")
                .categoryName("category1")
                .memo("memo1")
                .build();
        given(dailyPaymentsRepository.findById(anyLong()))
                .willReturn(Optional.of(dailyPayments));

        DeleteDailyPaymentsRequestDto requestDto =
                DeleteDailyPaymentsRequestDto.builder()
                        .dailyPaymentsId(1L)
                        .build();

        ArgumentCaptor<DailyPayments> captor =
                ArgumentCaptor.forClass(DailyPayments.class);

        //when
        dailyPaymentsService.deleteDailyPayments(requestEmail, requestDto);

        //then
        verify(dailyPaymentsRepository, times(1))
                .delete(captor.capture());
    }

    @Test
    void fail_deleteDailyPayments_????????????_??????_??????() {
        //given
        String requestEmail = "hello@abc.com";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        DeleteDailyPaymentsRequestDto requestDto =
                DeleteDailyPaymentsRequestDto.builder()
                        .dailyPaymentsId(1L)
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.deleteDailyPayments(
                        requestEmail,
                        requestDto
                )
        );
    }

    @Test
    void fail_deleteDailyPayments_????????????_??????_????????????() {
        //given
        String requestEmail = "hello@abc.com";
        Member owner = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(owner));

        given(dailyPaymentsRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        DeleteDailyPaymentsRequestDto requestDto =
                DeleteDailyPaymentsRequestDto.builder()
                        .dailyPaymentsId(1L)
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.deleteDailyPayments(
                        requestEmail,
                        requestDto
                )
        );
    }

    @Test
    void fail_deleteDailyPayments_????????????_?????????_??????_??????() {
        //given
        String notOwnerEmail = "notOwner@abc.com";
        Member owner = Member.builder()
                .id(1L)
                .email("owner@abc.com")
                .build();
        Member notOwner = Member.builder()
                .id(2L)
                .email(notOwnerEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(notOwner));

        DailyPayments dailyPayments = DailyPayments.builder()
                .id(1L)
                .member(owner)
                .paidAmount(1000)
                .paidWhere("place1")
                .methodOfPayment("card1")
                .categoryName("category1")
                .memo("memo1")
                .build();

        given(dailyPaymentsRepository.findById(anyLong()))
                .willReturn(Optional.of(dailyPayments));

        DeleteDailyPaymentsRequestDto requestDto =
                DeleteDailyPaymentsRequestDto.builder()
                        .dailyPaymentsId(1L)
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.deleteDailyPayments(
                        notOwnerEmail,
                        requestDto
                )
        );
    }

    @Test
    void success_getDailyPayment() {
        //given
        String requestEmail = "hello@abc.com";
        Member owner = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(owner));

        DailyPayments dailyPayments = DailyPayments.builder()
                .id(1L)
                .member(owner)
                .paidAmount(1000)
                .paidWhere("place1")
                .methodOfPayment("card1")
                .categoryName("category1")
                .memo("memo1")
                .build();
        given(dailyPaymentsRepository.findById(anyLong()))
                .willReturn(Optional.of(dailyPayments));


        //when
        GetDailyPaymentsResponseDto responseDto =
                dailyPaymentsService.getDailyPayment(
                        requestEmail,
                        dailyPayments.getId()
                );

        //then
        assertEquals(
                dailyPayments.getPaidAmount(),
                responseDto.getPaidAmount()
        );
        assertEquals(
                dailyPayments.getPaidWhere(),
                responseDto.getPaidWhere()
        );
        assertEquals(
                dailyPayments.getMethodOfPayment(),
                responseDto.getMethodOfPayment()
        );
        assertEquals(
                dailyPayments.getCategoryName(),
                responseDto.getCategoryName()
        );
        assertEquals(
                dailyPayments.getMemo(),
                responseDto.getMemo()
        );
    }

    @Test
    void fail_getDailyPayment_????????????_??????_??????() {
        //given
        String requestEmail = "hello@abc.com";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        Long requestDailyPaymentsId = 1L;

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.getDailyPayment(
                        requestEmail,
                        requestDailyPaymentsId
                )
        );
    }

    @Test
    void fail_getDailyPayment_????????????_??????_????????????() {
        //given
        String requestEmail = "hello@abc.com";
        Member owner = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(owner));

        given(dailyPaymentsRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        Long requestDailyPaymentsId = 1L;

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.getDailyPayment(
                        requestEmail,
                        requestDailyPaymentsId
                )
        );
    }

    @Test
    void fail_getDailyPayment_????????????_?????????_??????_??????() {
        //given
        String notOwnerEmail = "notOwner@abc.com";
        Member owner = Member.builder()
                .id(1L)
                .email("owner@abc.com")
                .build();
        Member notOwner = Member.builder()
                .id(2L)
                .email(notOwnerEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(notOwner));

        DailyPayments dailyPayments = DailyPayments.builder()
                .id(1L)
                .member(owner)
                .paidAmount(1000)
                .paidWhere("place1")
                .methodOfPayment("card1")
                .categoryName("category1")
                .memo("memo1")
                .build();

        given(dailyPaymentsRepository.findById(anyLong()))
                .willReturn(Optional.of(dailyPayments));

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.getDailyPayment(
                        notOwnerEmail,
                        dailyPayments.getId()
                )
        );
    }

    @Test
    void success_getDailyPaymentsList() {
        //given
        String requestDate = "yyyy-MM";

        String requestEmail = "hello@abc.com";
        Member owner = Member.builder()
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(owner));

        DailyPayments dailyPayment1 = DailyPayments.builder()
                .id(1L)
                .member(owner)
                .paidAmount(1000)
                .paidWhere("place1")
                .methodOfPayment("card1")
                .categoryName("category1")
                .memo("memo1")
                .build();
        DailyPayments dailyPayment2 = DailyPayments.builder()
                .id(2L)
                .member(owner)
                .paidAmount(2000)
                .paidWhere("place2")
                .methodOfPayment("card2")
                .categoryName("category2")
                .memo("memo2")
                .build();
        List<DailyPayments> dailyPaymentsList = new ArrayList<>();
        dailyPaymentsList.add(dailyPayment1);
        dailyPaymentsList.add(dailyPayment2);
        given(dailyPaymentsRepository.findAllByMemberIdAndCreatedAtContaining(
                owner.getId(),
                requestDate
                )
        )
                .willReturn(dailyPaymentsList);

        //when
        List<GetDailyPaymentsResponseDto> responseDtoList =
                dailyPaymentsService.getDailyPaymentsList(requestEmail, requestDate);

        //then
        assertEquals(2, responseDtoList.size());
    }

    @Test
    void fail_getDailyPaymentsList_????????????_??????_??????() {
        //given
        String requestDate = "yyyy-MM";

        String requestEmail = "hello@abc.com";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.getDailyPaymentsList(
                        requestEmail,
                        requestDate)
        );
    }

    @Test
    void success_searchDailyPayments() { // ?????? ????????? ???????????? ??????
        //given
        String searchKeyword = "keyword";

        String requestEmail = "hello@abc.com";
        Member owner = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(owner));

        Member notOwner = Member.builder()
                .id(2L)
                .email(requestEmail)
                .build();

        DailyPayments notSearchNoSearchKeyword = DailyPayments.builder()
                .id(1L)
                .member(owner)
                .paidAmount(1000)
                .paidWhere("place1")
                .methodOfPayment("card1")
                .categoryName("category1")
                .memo("memo1")
                .build();
        DailyPayments notSearchNotOwner = DailyPayments.builder()
                .id(2L)
                .member(notOwner)
                .paidAmount(2000)
                .paidWhere("keyword2")
                .methodOfPayment("card2")
                .categoryName("category2")
                .memo("keyword2")
                .build();
        DailyPayments searchDailyPaymentByPaidWhere = DailyPayments.builder()
                .id(3L)
                .member(owner)
                .paidAmount(3000)
                .paidWhere("keyword2")
                .methodOfPayment("card2")
                .categoryName("category2")
                .memo("memo2")
                .build();
        DailyPayments searchDailyPaymentByMemo = DailyPayments.builder()
                .id(4L)
                .member(owner)
                .paidAmount(4000)
                .paidWhere("paidWhere2")
                .methodOfPayment("card2")
                .categoryName("category2")
                .memo("keyword2")
                .build();
        DailyPayments searchDailyPaymentBoth = DailyPayments.builder()
                .id(5L)
                .member(owner)
                .paidAmount(5000)
                .paidWhere("keyword2")
                .methodOfPayment("card2")
                .categoryName("category2")
                .memo("keyword2")
                .build();
        List<DailyPayments> list = new ArrayList<>();
        list.add(searchDailyPaymentByPaidWhere);
        list.add(searchDailyPaymentByMemo);
        list.add(searchDailyPaymentBoth);
        given(dailyPaymentsRepository.searchKeyword(anyLong(), anyString()))
                .willReturn(list);

        //when
        dailyPaymentsService.searchDailyPayments(requestEmail, searchKeyword);

        //then
        verify(dailyPaymentsRepository, times(1))
                .searchKeyword(owner.getId(), searchKeyword);
    }

    @Test
    void fail_searchDailyPayments_????????????_??????_??????() {
        //given
        String requestKeyword = "keyword";

        String requestEmail = "hello@abc.com";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.searchDailyPayments(
                        requestEmail,
                        requestKeyword)
        );
    }

    @Test
    @DisplayName("??????_????????????_??????_??????_?????????")
    void success_getMonthlyDailyPaymentsResult_getPastMonthlyResult() {
        //given
        String requestEmail = "hello@abc.com";
        String requestDate = "2022-12";
        Member member = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        MonthlyTotalAmount monthlyTotalAmount = MonthlyTotalAmount.builder()
                .totalAmount(1000000)
                .build();
        given(monthlyTotalAmountRepository.findByDateInfoAndMemberId(
                anyString(),
                anyLong())
        ).willReturn(Optional.of(monthlyTotalAmount));

        TotalAmountPerCategory totalAmountPerCategory =
                TotalAmountPerCategory.builder()
                    .member(member)
                    .categoryName("categoryName1")
                    .totalAmount(10000)
                    .build();
        List<TotalAmountPerCategory> list = new ArrayList<>();
        list.add(totalAmountPerCategory);
        given(totalAmountPerCategoryRepository.findByDateInfoAndMemberId(
                anyString(),
                anyLong()
                )
        ).willReturn(list);

        //when
        GetMonthlyResultResponseDto responseDto =
                dailyPaymentsService.getMonthlyDailyPaymentsResult(
                        requestEmail,
                        requestDate
                );

        //then
        assertEquals(
                monthlyTotalAmount.getTotalAmount(),
                responseDto.getTotalAmount()
        );
        assertEquals(1, responseDto.getList().size());
    }

    @Test
    @DisplayName("??????_????????????_??????_??????_?????????")
    void success_getMonthlyDailyPaymentsResult_getCurrentMonthlyResult() {
        //given
        String requestEmail = "hello@abc.com";
        String requestDate = "2023-01";
        Member member = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        DailyPaymentsCategoryDto categoryDto1 = DailyPaymentsCategoryDto.builder()
                .categoryName("category1")
                .totalAmount(12345)
                .build();
        DailyPaymentsCategoryDto categoryDto2 = DailyPaymentsCategoryDto.builder()
                .categoryName("category2")
                .totalAmount(67890)
                .build();
        List<DailyPaymentsCategoryDto> list = new ArrayList<>();
        list.add(categoryDto1);
        list.add(categoryDto2);
        given(dailyPaymentsQueryDsl
                .getTotalAmountPerCategoryByMemberId(anyString(), anyLong())
        ).willReturn(list);


        //when
        GetMonthlyResultResponseDto responseDto =
                dailyPaymentsService.getMonthlyDailyPaymentsResult(
                        requestEmail,
                        requestDate
                );

        //then
        assertEquals(
                categoryDto1.getTotalAmount() + categoryDto2.getTotalAmount(),
                responseDto.getTotalAmount()
        );
        assertEquals(list.size(), responseDto.getList().size());
    }

    @Test
    @DisplayName("??????_????????????_??????_??????_????????????_??????_?????????_???")
    void fail_getMonthlyDailyPaymentsResult_????????????_??????_??????() {
        //given
        String requestEmail = "hello@abc.com";
        String requestDate = "yyyy-MM";


        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.getMonthlyDailyPaymentsResult(
                        requestEmail,
                        requestDate
                )
        );
    }

    @Test
    @DisplayName("??????_?????????_????????????_??????_??????_???_?????????_????????????_??????_???")
    void fail_getPastMonthlyResult_???_?????????_????????????_??????_???() {
        //given
        String requestEmail = "hello@abc.com";
        String requestDate = "2022-12";

        Member member = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        given(monthlyTotalAmountRepository.findByDateInfoAndMemberId(
                anyString(), anyLong()
            )
        ).willReturn(Optional.empty());

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.getMonthlyDailyPaymentsResult(
                        requestEmail,
                        requestDate
                )
        );
    }

    @Test
    void success_getYearlyResult() {
        //given
        String requestEmail = "hello@abc.com";
        String requestYear = "2022";
        Member member = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        int totalAmountOfTheYear = 150000;
        given(monthlyTotalAmountRepository.sumByMemberIdAndDateInfoContainingYear(
                anyLong(), anyString()
            )
        ).willReturn(totalAmountOfTheYear);

        DailyPaymentsCategoryDto category1 = DailyPaymentsCategoryDto.builder()
                .categoryName("category1")
                .totalAmount(100000)
                .build();
        DailyPaymentsCategoryDto category2 = DailyPaymentsCategoryDto.builder()
                .categoryName("category2")
                .totalAmount(50000)
                .build();
        List<DailyPaymentsCategoryDto> list = new ArrayList<>();
        list.add(category1);
        list.add(category2);
        given(dailyPaymentsQueryDsl
                .getYearlyTotalAmountPerCategoryByMemberId(anyLong(), anyString())
        ).willReturn(list);

        //when
        GetYearlyResultResponseDto responseDto =
                dailyPaymentsService.getYearlyResult(requestYear, requestYear);

        //then
        assertEquals(
                totalAmountOfTheYear,
                responseDto.getYearlyTotalAmount()
        );
        assertEquals(list.size(), responseDto.getList().size());
    }

    @Test
    void fail_getYearlyResult_????????????_??????_??????() {
        //given
        String requestEmail = "hello@abc.com";
        String requestYear = "2023";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.getYearlyResult(
                        requestEmail,
                        requestYear
                )
        );
    }

    @Test
    void fail_getYearlyResult_?????????_???_??????_??????() {
        //given
        String requestEmail = "hello@abc.com";
        String requestYear = "2023";
        Member member = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));


        //when

        //then
        assertThrows(AccountBookException.class,
                () -> dailyPaymentsService.getYearlyResult(
                        requestEmail,
                        requestYear
                )
        );
    }
}