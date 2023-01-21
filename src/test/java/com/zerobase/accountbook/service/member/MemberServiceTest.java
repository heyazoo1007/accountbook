package com.zerobase.accountbook.service.member;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberInfoRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberPasswordRequestDto;
import com.zerobase.accountbook.controller.auth.dto.response.GetMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberPasswordResponseDto;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    void success_getMemberInfo() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder().id(1L).email("hello@abc.com").build();
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

        //when
        GetMemberInfoResponseDto responseDto =
                memberService.getMemberInfo(requestEmail, member.getId());

        //then
        assertEquals(member.getEmail(), responseDto.getEmail());
    }

    @Test
    void fail_getMemberInfo_존재하지_않는_회원() {
        //given
        String requestEmail = "hello@abc.com";
        Member member = Member.builder().id(1L).email("hello@abc.com").build();
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> memberService.getMemberInfo(requestEmail, member.getId()));
    }

    @Test
    void fail_getMemberInfo_다른_회원_정보에_접근() {
        //given
        String requestEmail = "different@abc.com";
        Member member = Member.builder().id(1L).email("hello@abc.com").build();
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> memberService.getMemberInfo(requestEmail, member.getId()));
    }

    @Test
    void success_modifyMemberInfo() {
        //given
        Member member = Member.builder()
                .id(1L)
                .email("hello@abc.com")
                .memberName("hello")
                .monthlyBudget(0)
                .build();
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

        String requestEmail = "hello@abc.com";
        ModifyMemberInfoRequestDto requestDto =
                ModifyMemberInfoRequestDto.builder()
                        .memberId(1L)
                        .memberName("hello2")
                        .monthlyBudget(10000)
                        .build();

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        //when
        ModifyMemberInfoResponseDto responseDto =
                memberService.modifyMemberInfo(requestEmail, requestDto);

        //then
        verify(memberRepository, times(1))
                .save(captor.capture());
        assertEquals(
                responseDto.getMemberName(),
                captor.getValue().getMemberName()
        );
        assertEquals(
                responseDto.getEmail(),
                captor.getValue().getEmail()
        );
        assertEquals(
                responseDto.getMonthlyBudget(),
                captor.getValue().getMonthlyBudget()
        );
    }

    @Test
    void fail_modifyMemberInfo_존재하지않는_회원_조회() {
        //given
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        String requestEmail = "hello@abc.com";
        ModifyMemberInfoRequestDto requestDto =
                ModifyMemberInfoRequestDto.builder()
                        .memberId(1L)
                        .memberName("hello1")
                        .monthlyBudget(10000)
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> memberService.modifyMemberInfo(requestEmail, requestDto));
    }

    @Test
    void fail_modifyMemberInfo_다른_회원정보_조회() {
        //given
        Long memberId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .email("hello@abc.com")
                .memberName("hello")
                .monthlyBudget(0)
                .build();
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

        String requestEmail = "differentuser@abc.com";
        ModifyMemberInfoRequestDto requestDto =
                ModifyMemberInfoRequestDto.builder()
                        .memberId(memberId)
                        .memberName("hello1")
                        .monthlyBudget(10000)
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> memberService.modifyMemberInfo(requestEmail, requestDto));
    }

    @Test
    void success_modifyMemberPassword() {
        //given
        Long memberId = 1L;
        String sameEmail = "hello@abc.com";
        String beforePassword = "beforePassword";
        Member member = Member.builder()
                .id(memberId)
                .email(sameEmail)
                .password(passwordEncoder.encode(beforePassword))
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString()))
                .willReturn(true);

        String requestEmail = sameEmail;
        ModifyMemberPasswordRequestDto requestDto =
                ModifyMemberPasswordRequestDto.builder()
                        .memberId(memberId)
                        .beforePassword(beforePassword)
                        .afterPassword("afterPassword")
                        .build();

        given(memberRepository.save(any())).willReturn(member);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        //when
        ModifyMemberPasswordResponseDto responseDto =
                memberService.modifyMemberPassword(requestEmail, requestDto);

        //then
        verify(memberRepository, times(1))
                .save(captor.capture());
        assertEquals(responseDto.getMemberId(), captor.getValue().getId());
        assertTrue(passwordEncoder.matches(
                requestDto.getAfterPassword(),
                captor.getValue().getPassword()));
    }

    @Test
    void fail_modifyMemberPassword_존재하지않는_회원() {
        //given
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        String requestEmail = "hello@abc.com";
        ModifyMemberPasswordRequestDto requestDto =
                ModifyMemberPasswordRequestDto.builder()
                        .memberId(1L)
                        .beforePassword("beforePassword")
                        .afterPassword("afterPassword")
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> memberService.modifyMemberPassword(
                        requestEmail, requestDto
                )
        );
    }

    @Test
    void fail_modifyMemberPassword_다른_회원의_정보_조회() {
        //given
        Long memberId = 1L;
        String beforePassword = "beforePassword";
        Member member = Member.builder()
                .id(memberId)
                .password(beforePassword)
                .email("hello@abc.com")
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

        String requestEmail = "different@abc.com";
        ModifyMemberPasswordRequestDto requestDto =
                ModifyMemberPasswordRequestDto.builder()
                        .memberId(memberId)
                        .beforePassword(beforePassword)
                        .afterPassword("afterPassword")
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> memberService.modifyMemberPassword(
                        requestEmail, requestDto
                )
        );
    }

    @Test
    void fail_modifyMemberPassword_다른_비밀번호_입력시() {
        //given
        Long memberId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .password(passwordEncoder.encode("beforePassword"))
                .email("hello@abc.com")
                .build();

        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));

        String requestEmail = "hello@abc.com";
        ModifyMemberPasswordRequestDto requestDto =
                ModifyMemberPasswordRequestDto.builder()
                        .memberId(memberId)
                        .beforePassword("differentPassword")
                        .afterPassword("afterPassword")
                        .build();

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> memberService.modifyMemberPassword(
                        requestEmail, requestDto
                )
        );
    }
}