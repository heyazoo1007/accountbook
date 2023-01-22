package com.zerobase.accountbook.service.dailypaymetns;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.dailypayments.dto.request.CreateDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.DeleteDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.ModifyDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.CreateDailyPaymentsResponseDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.GetDailyPaymentsResponseDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.ModifyDailyPaymentsResponseDto;
import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.service.dailypaymetns.querydsl.DailyPaymentsQueryDsl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
        // captor 에 id가 없다. 왜지?
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
}