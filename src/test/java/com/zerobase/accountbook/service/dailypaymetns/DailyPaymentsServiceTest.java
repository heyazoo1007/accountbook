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
                .payLocation("place1")
                .methodOfPayment("card1")
                .categoryId(1)
                .memo("memo1")
                .build();
        given(dailyPaymentsRepository.save(any()))
                .willReturn(dailyPayments);

        CreateDailyPaymentsRequestDto requestDto =
                CreateDailyPaymentsRequestDto.builder()
                        .paidAmount(1000)
                        .paidWhere("place1")
                        .methodOfPayment("card1")
                        .categoryId(1)
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
        // captor 에 id가 없다. 왜지?
        assertEquals(
                captor.getValue().getId(),
                responseDto.getDailyPaymentsId()
        );
        assertEquals(
                captor.getValue().getPayLocation(),
                responseDto.getPaidWhere()
        );
        assertEquals(
                captor.getValue().getCategoryId(),
                responseDto.getCategoryId()
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
    void fail_createDailyPayments_존재하지_않는_회원() {
        //given
        String requestEmail = "hello@abc.com";
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        CreateDailyPaymentsRequestDto requestDto =
                CreateDailyPaymentsRequestDto.builder()
                        .paidAmount(1000)
                        .paidWhere("place1")
                        .methodOfPayment("card1")
                        .categoryId(1)
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
                .payLocation("place1")
                .methodOfPayment("card1")
                .categoryId(1)
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
                    .categoryId(1)
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
                captor.getValue().getPayLocation(),
                responseDto.getPayLocation()
        );
        assertEquals(
                captor.getValue().getMethodOfPayment(),
                responseDto.getMethodOfPayment()
        );
        assertEquals(
                captor.getValue().getCategoryId(),
                responseDto.getCategoryId()
        );
        assertEquals(
                captor.getValue().getMemo(),
                responseDto.getMemo()
        );
    }

    @Test
    void fail_modifyDailyPayments_존재하지_않는_회원() {
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
                        .categoryId(1)
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
    void fail_modifyDailyPayments_존재하지_않는_지출내역() {
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
                        .categoryId(1)
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
    void fail_modifyDailyPayments_지출내역_주인이_아닌_경우() {
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
                .payLocation("place1")
                .methodOfPayment("card1")
                .categoryId(1)
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
                        .categoryId(1)
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
                .payLocation("place1")
                .methodOfPayment("card1")
                .categoryId(1)
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
    void fail_deleteDailyPayments_존재하지_않는_회원() {
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
    void fail_deleteDailyPayments_존재하지_않는_지출내역() {
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
    void fail_deleteDailyPayments_지출내역_주인이_아닌_경우() {
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
                .payLocation("place1")
                .methodOfPayment("card1")
                .categoryId(1)
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
                .payLocation("place1")
                .methodOfPayment("card1")
                .categoryId(1)
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
                dailyPayments.getPayLocation(),
                responseDto.getPayLocation()
        );
        assertEquals(
                dailyPayments.getMethodOfPayment(),
                responseDto.getMethodOfPayment()
        );
        assertEquals(
                dailyPayments.getCategoryId(),
                responseDto.getCategoryId()
        );
        assertEquals(
                dailyPayments.getMemo(),
                responseDto.getMemo()
        );
    }

    @Test
    void fail_getDailyPayment_존재하지_않는_회원() {
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
    void fail_getDailyPayment_존재하지_않는_지출내역() {
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
    void fail_getDailyPayment_지출내역_주인이_아닌_경우() {
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
                .payLocation("place1")
                .methodOfPayment("card1")
                .categoryId(1)
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
                .payLocation("place1")
                .methodOfPayment("card1")
                .categoryId(1)
                .memo("memo1")
                .build();
        DailyPayments dailyPayment2 = DailyPayments.builder()
                .id(2L)
                .member(owner)
                .paidAmount(2000)
                .payLocation("place2")
                .methodOfPayment("card2")
                .categoryId(1)
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
    void fail_getDailyPaymentsList_존재하지_않는_회원() {
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
    void success_searchDailyPayments() { // 지출 장소와 메모에서 검색
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
                .payLocation("place1")
                .methodOfPayment("card1")
                .categoryId(1)
                .memo("memo1")
                .build();
        DailyPayments notSearchNotOwner = DailyPayments.builder()
                .id(2L)
                .member(notOwner)
                .paidAmount(2000)
                .payLocation("keyword2")
                .methodOfPayment("card2")
                .categoryId(1)
                .memo("keyword2")
                .build();
        DailyPayments searchDailyPaymentByPaidWhere = DailyPayments.builder()
                .id(3L)
                .member(owner)
                .paidAmount(3000)
                .payLocation("keyword2")
                .methodOfPayment("card2")
                .categoryId(1)
                .memo("memo2")
                .build();
        DailyPayments searchDailyPaymentByMemo = DailyPayments.builder()
                .id(4L)
                .member(owner)
                .paidAmount(4000)
                .payLocation("paidWhere2")
                .methodOfPayment("card2")
                .categoryId(1)
                .memo("keyword2")
                .build();
        DailyPayments searchDailyPaymentBoth = DailyPayments.builder()
                .id(5L)
                .member(owner)
                .paidAmount(5000)
                .payLocation("keyword2")
                .methodOfPayment("card2")
                .categoryId(1)
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
    void fail_searchDailyPayments_존재하지_않는_회원() {
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
    @DisplayName("성공_지출내역_통계_조회_지난달")
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
        given(monthlyTotalAmountRepository.findByDateAndMemberId(
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
        given(totalAmountPerCategoryRepository.findByDateAndMemberId(
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
    @DisplayName("성공_지출내역_통계_조회_이번달")
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
    @DisplayName("실패_지출내역_통계_조회_존재하지_않는_회원일_때")
    void fail_getMonthlyDailyPaymentsResult_존재하지_않는_회원() {
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
    @DisplayName("실패_지난달_지출내역_통계_조회_총_지출이_존재하지_않을_때")
    void fail_getPastMonthlyResult_총_지출이_존재하지_않을_때() {
        //given
        String requestEmail = "hello@abc.com";
        String requestDate = "2022-12";

        Member member = Member.builder()
                .id(1L)
                .email(requestEmail)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        given(monthlyTotalAmountRepository.findByDateAndMemberId(
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
    void fail_getYearlyResult_존재하지_않는_회원() {
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
    void fail_getYearlyResult_조회할_수_없는_년도() {
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
