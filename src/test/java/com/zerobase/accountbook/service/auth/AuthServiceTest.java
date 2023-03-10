package com.zerobase.accountbook.service.auth;

import com.zerobase.accountbook.common.config.security.JwtTokenProvider;
import com.zerobase.accountbook.common.config.security.dto.TokenResponseDto;
import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.common.repository.RedisRepository;
import com.zerobase.accountbook.controller.auth.dto.request.CompleteAuthEmailRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.CreateMemberRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.SendAuthEmailRequestDto;
import com.zerobase.accountbook.controller.auth.dto.response.CreateMemberResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ValidateEmailResponseDto;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zerobase.accountbook.domain.Email.*;
import static com.zerobase.accountbook.domain.Email.AUTH_REQUEST;
import static com.zerobase.accountbook.domain.member.MemberRole.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RedisRepository redisRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void success_validateEmail() {
        //given
        String email = "hello@abc.com";

        // ?????? ???????????? ?????? ?????????
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when
        ValidateEmailResponseDto validateEmailResponseDto =
                authService.validateEmail(email);

        //then
        assertEquals(email, validateEmailResponseDto.getEmail());
    }

    @Test
    void fail_validateEmail_?????????_?????????() {
        //given
        String email = "hello@abc.com";
        Member member = Member.builder()
                .email(email)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        //when

        //then
        assertThrows(
                AccountBookException.class,
                () -> authService.validateEmail(email)
        );
    }

    // ?????? ????????? PostMan ?????? ?????? ???????????? ?????? ??????????????????.
    @Test
    void success_sendAuthEmail() {
        //given
        //when
        //then
    }

    @Test
    void fail_sendAuthEmail_??????_?????????_??????() {
        //given
        String email = "hello@abc.com";
        Member member = Member.builder()
                .email(email)
                .build();
        SendAuthEmailRequestDto requestDto = SendAuthEmailRequestDto.builder()
                .email(email)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> authService.sendAuthEmail(requestDto));
    }

    @Test
    void fail_sendAuthEmail_?????????_??????_??????_2???_??????_??????() {
        //given
        String email = "hello@abc.com";
        SendAuthEmailRequestDto requestDto = SendAuthEmailRequestDto.builder()
                .email(email)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
        given(redisRepository.getData(anyString()))
                .willReturn("auth already has been requested");
        //when

        //then
        assertThrows(AccountBookException.class,
                () -> authService.sendAuthEmail(requestDto));
    }

    @Test
    void success_completeAuthEmail() {
        //given
        String email = "hello@abc.com";
        String authKey = "123456";
        CompleteAuthEmailRequestDto requestDto =
                CompleteAuthEmailRequestDto.builder()
                        .email(email)
                        .authKey(authKey)
                        .build();
        // ???????????? ???????????? ????????? ????????????
        given(redisRepository.getData(anyString()))
                .willReturn(email);
        // ?????? ???????????? ??? ???
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when
        authService.completeAuthEmail(requestDto);

        //then
        //setDateExpire ??? 1??? ??????????????? ??????
        verify(redisRepository, times(1))
                .setDataExpire(
                        "EMAIL-AUTH:" + requestDto.getEmail(),
                        String.valueOf(AUTH_COMPLETED),
                        86400L
                );
    }

    @Test
    void fail_completeAuthEmail_?????????_????????????() {
        //given
        String email = "hello@abc.com";
        String authKey = "123456";
        CompleteAuthEmailRequestDto requestDto =
                CompleteAuthEmailRequestDto.builder()
                        .email(email)
                        .authKey(authKey)
                        .build();
        // ???????????? ???????????? ????????? ????????????
        given(redisRepository.getData(anyString()))
                .willReturn(null);

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> authService.completeAuthEmail(requestDto));
    }

    @Test
    void fail_completeAuthEmail_?????????_??????_??????() {
        //given
        String email = "hello@abc.com";
        String authKey = "123456";
        CompleteAuthEmailRequestDto requestDto =
                CompleteAuthEmailRequestDto.builder()
                        .email(email)
                        .authKey(authKey)
                        .build();
        // ???????????? ???????????? ????????? ????????????
        given(redisRepository.getData(anyString()))
                .willReturn("different email");

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> authService.completeAuthEmail(requestDto));
    }

    @Test
    void success_createMember() {
        //given
        String email = "hello@abc.com";
        CreateMemberRequestDto requestDto = CreateMemberRequestDto.builder()
                .memberName("member1")
                .email(email)
                .password("password")
                .build();

        String emailAuth = "EMAIL-AUTH:" + email;
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
        given(redisRepository.getData(emailAuth))
                .willReturn("AUTH_COMPLETED");

        Member member = Member.builder()
                .memberName(requestDto.getMemberName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(ROLE_MEMBER)
                .build();
        given(memberRepository.save(any()))
                .willReturn(member);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        //when
        CreateMemberResponseDto responseDto =
                authService.createMember(requestDto);

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
        assertEquals(passwordEncoder.encode(
                responseDto.getPassword()),
                captor.getValue().getPassword()
        );
    }

    @Test
    void fail_createMember_??????_??????_??????() {
        //given
        String email = "hello@abc.com";
        CreateMemberRequestDto requestDto = CreateMemberRequestDto.builder()
                .memberName("member1")
                .email(email)
                .password("password")
                .build();

        String emailAuth = "EMAIL-AUTH:" + email;
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
        given(redisRepository.getData(emailAuth))
                .willReturn(null);

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> authService.createMember(requestDto));
    }

    @Test
    void fail_createMember_?????????_?????????() {
        //given
        String email = "hello@abc.com";
        CreateMemberRequestDto requestDto = CreateMemberRequestDto.builder()
                .memberName("member1")
                .email(email)
                .password("password")
                .build();

        String emailAuth = "EMAIL-AUTH:" + email;
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
        given(redisRepository.getData(emailAuth))
                .willReturn(String.valueOf(AUTH_REQUEST));

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> authService.createMember(requestDto));
    }

    // jwtProvider ??? verify ??? ????????? ?????? ?????? ????????? ????????? ??????
    @Test
    void success_signIn() {
        //given
        String email = "hello@abc.com";
        String password = "password";
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(ROLE_MEMBER)
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));
        given(passwordEncoder.matches(password, member.getPassword()))
                .willReturn(true);

        TokenResponseDto token = TokenResponseDto.builder()
                .accessToken("token")
                .build();
        given(jwtTokenProvider.createToken(anyString(), anyList()))
                .willReturn(token);

        //when
        TokenResponseDto responseDto = authService.signIn(email, password);

        //then
        assertEquals(token.getAccessToken(), responseDto.getAccessToken());
    }

    @Test
    void fail_signIn_?????????_??????_????????????_?????????() {
        //given
        String email = "hello@abc.com";
        String password = "password";
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(ROLE_MEMBER)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

        given(passwordEncoder.matches(password, member.getPassword()))
                .willReturn(false);

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> authService.signIn(email, password)
        );
    }

    @Test
    void fail_signIn_????????????_??????_??????() {
        //given
        String email = "hello@abc.com";
        String password = "password";
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(ROLE_MEMBER)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
        //when

        //then
        assertThrows(AccountBookException.class,
                () -> authService.signIn(email, password));
    }
}