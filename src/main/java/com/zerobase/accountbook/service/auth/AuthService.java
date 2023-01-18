package com.zerobase.accountbook.service.auth;

import com.zerobase.accountbook.common.config.security.JwtTokenProvider;
import com.zerobase.accountbook.common.config.security.dto.TokenResponseDto;
import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.common.repository.RedisRepository;
import com.zerobase.accountbook.controller.auth.dto.request.*;
import com.zerobase.accountbook.controller.auth.dto.response.*;
import com.zerobase.accountbook.domain.Email;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.member.MemberRole;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;
import static com.zerobase.accountbook.domain.Email.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final long AUTH_EMAIL_REQUEST_WILL_BE_EXPIRED_IN = 60 * 60 * 24L;
    public static final long AUTH_KEY_EXPIRATION = 60 * 3L;
    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final RedisRepository redisRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    public ValidateEmailResponseDto validateEmail(String email) {

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new AccountBookException(
                    String.format("이미 가입된 유저의 이메일 (%s) 입니다.", email),
                    CONFLICT_USER_EXCEPTION
            );
        }

        return ValidateEmailResponseDto.builder()
                .email(email)
                .build();
    }

    public void sendAuthEmail(SendAuthEmailRequestDto request) {

        String email = request.getEmail();

        validateEmail(email);

        // 인증 이메일 전송 버튼을 누르고 또 누르는 경우에 대한 예외처리
        String data = redisRepository.getData("EMAIL-AUTH:" + email);
        if (data != null) {
            throw new AccountBookException(
                    String.format("(%s) 해당 이메일로 인증 메일이 전송되었습니다.", email),
                    VALIDATION_AUTH_EMAIL_HAS_ALREADY_BEEN_SENT
            );
        }

        String authKey = getAuthKey();

        sendAuthKeyToEmail(email, authKey);

        setAuthKeyAuthCache(authKey, email, AUTH_KEY_EXPIRATION);
        setAuthKeyAuthCache(
                "EMAIL-AUTH:" + email,
                String.valueOf(AUTH_REQUEST),
                AUTH_EMAIL_REQUEST_WILL_BE_EXPIRED_IN
        );
    }

    public void completeAuthEmail(CompleteAuthEmailRequestDto request) {

        String email = getData(request.getAuthKey());

        validateEmail(email);

        if (email == null) {
            throw new AccountBookException(
                    "잘못된 이메일 인증번호입니다.",
                    VALIDATION_EMAIL_AUTH_KEY_EXCEPTION);
        }

        if (!email.equals(request.getEmail())) {
            throw new AccountBookException(
                    "잘못된 이메일 입니다.",
                    VALIDATION_WRONG_EMAIL_PASSWORD_EXCEPTION
            );
        }
        // 인증 완료 후 하루안에 회원가입해야함
        setAuthKeyAuthCache(
                "EMAIL-AUTH:" + email,
                String.valueOf(AUTH_COMPLETED),
                AUTH_EMAIL_REQUEST_WILL_BE_EXPIRED_IN
        );
    }

    public CreateMemberResponseDto createMember(CreateMemberRequestDto request) {

        String email = request.getEmail();
        String emailAuth = "EMAIL-AUTH:" + email;

        validateEmail(email);

        String valueOfEmailAuth = getData(emailAuth);

        // 인증 신청하지 않은 이메일인 경우
        if(valueOfEmailAuth == null) {
            throw new AccountBookException(
                    "이메일 인증 완료 후 회원가입 할 수 있습니다.",
                    UNAUTHORIZED_EMAIL_EXCEPTION
            );
        }

        // 이메일 인증 신청했지만, 인증키를 입력하지 않은 경우
        if (valueOfEmailAuth.equals(String.valueOf(AUTH_REQUEST))) {
            throw new AccountBookException(
                    "인증키를 입력 해주세요.",
                    UNAUTHORIZED_AUTH_KEY_EXCEPTION
            );
        }

        return CreateMemberResponseDto.of(memberRepository.save(Member.builder()
                .memberName(request.getMemberName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(MemberRole.ROLE_MEMBER)
                .createdAt(LocalDateTime.now())
                .build()));
    }

    public TokenResponseDto signIn(String email, String password) {

        // 해당 사용자가 존재하지 않는 경우
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (!optionalMember.isPresent()) {
            throw new AccountBookException(
                    "해당 이메일에 대한 사용자가 존재하지 않습니다.",
                    NOT_FOUND_EMAIL_EXCEPTION
            );
        }
        Member member = optionalMember.get();

        // 이메일에 비밀번호가 매치되지 않는 경우
        // 비밀번호가 틀렸는데 아이디 혹은 비밀번호로 출력하는 이뉴는 혹시 모를 개인 정보 유출 때문
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new AccountBookException(
                    "이메일 혹은 비밀번호가 틀렸습니다.",
                    VALIDATION_WRONG_EMAIL_PASSWORD_EXCEPTION
            );
        }

        List<String> roles = new ArrayStack<>();
        roles.add(member.getRole().toString());

        return jwtTokenProvider.createToken(email, roles);
    }

    private String getAuthKey() {
        String authKey;

        do {
            Random random = new Random();
            authKey = String.valueOf(random.nextInt(999999));
        } while (redisRepository.existKey(authKey));

        return authKey;
    }

    private void sendAuthKeyToEmail(String email, String authKey) {
        String subject = "짠짠이 가입을 위한 인증 이메일입니다.";
        String text = "회원가입을 위한 인증번호는 " + authKey + " 입니다.<br/>";

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    "utf-8"
            );
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new AccountBookException(
                    String.format(
                            "(%s) 이메일에 대한 인증 메일을 전송하는 중 에러가 발생했습니다.",
                            email),
                    INTERNAL_SERVER_EXCEPTION
            );
        }
    }

    private String getData(String authKey) {
        return redisRepository.getData(authKey);
    }

    private void setAuthKeyAuthCache(
            String authKey, String email, long authKeyExpiration
    ) {
        redisRepository.setDataExpire(authKey, email, authKeyExpiration);
    }
}
