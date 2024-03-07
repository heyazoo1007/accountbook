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
import com.zerobase.accountbook.domain.member.MemberRole;
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

        // 해당 이메일의 회원 미존재
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when
        ValidateEmailResponseDto validateEmailResponseDto =
                authService.validateEmail(email);

        //then
        assertEquals(email, validateEmailResponseDto.getEmail());
    }

    @Test
    void fail_validateEmail_중복된_이메일() {
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

    // 해당 부분은 PostMan 으로 정상 동작하는 걸로 확인했습니다.
    @Test
    void success_sendAuthEmail() {
        //given
        //when
        //then
    }

    @Test
    void fail_sendAuthEmail_이미_가입한_회원() {
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
    void fail_sendAuthEmail_인증키_발급_요청_2번_이상_실행() {
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
        // 인증키에 해당하는 이메일 가져오기
        given(redisRepository.getData(anyString()))
                .willReturn(email);
        // 아직 회원가입 안 함
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        //when
        authService.completeAuthEmail(requestDto);

        //then
        //setDateExpire 가 1번 실행됐는지 확인
        verify(redisRepository, times(1))
                .setDataExpire(
                        "EMAIL-AUTH:" + requestDto.getEmail(),
                        String.valueOf(AUTH_COMPLETED),
                        86400L
                );
    }

    @Test
    void fail_completeAuthEmail_잘못된_인증번호() {
        //given
        String email = "hello@abc.com";
        String authKey = "123456";
        CompleteAuthEmailRequestDto requestDto =
                CompleteAuthEmailRequestDto.builder()
                        .email(email)
                        .authKey(authKey)
                        .build();
        // 인증키에 해당하는 이메일 가져오기
        given(redisRepository.getData(anyString()))
                .willReturn(null);

        //when

        //then
        assertThrows(AccountBookException.class,
                () -> authService.completeAuthEmail(requestDto));
    }

    @Test
    void fail_completeAuthEmail_이메일_잘못_입력() {
        //given
        String email = "hello@abc.com";
        String authKey = "123456";
        CompleteAuthEmailRequestDto requestDto =
                CompleteAuthEmailRequestDto.builder()
                        .email(email)
                        .authKey(authKey)
                        .build();
        // 인증키에 해당하는 이메일 가져오기
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
                .role(MEMBER)
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
    void fail_createMember_인증_요청_안함() {
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
    void fail_createMember_인증키_미입력() {
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

    // jwtProvider 를 verify 할 방법을 아직 찾지 못해서 테스트 미흡
    @Test
    void success_signIn() {
        //given
        String email = "hello@abc.com";
        String password = "password";
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(MEMBER)
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
    void fail_signIn_이메일_또는_비밀번호_불일치() {
        //given
        String email = "hello@abc.com";
        String password = "password";
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(MEMBER)
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
    void fail_signIn_가입하지_않은_회원() {
        //given
        String email = "hello@abc.com";
        String password = "password";
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(MEMBER)
                .build();

        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
        //when

        //then
        assertThrows(AccountBookException.class,
                () -> authService.signIn(email, password));
    }
}
