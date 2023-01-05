package com.zerobase.accountbook.service.auth;

import com.zerobase.accountbook.common.exception.ErrorCode;
import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.common.repository.RedisRepository;
import com.zerobase.accountbook.controller.auth.dto.request.CompleteAuthEmailRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.SendAuthEmailRequestDto;
import com.zerobase.accountbook.controller.auth.dto.response.ValidateEmailResponseDto;
import com.zerobase.accountbook.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.Random;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final long AuthEmailRequestWillExpireIn = 60 * 60 * 24L;
    public static final long AuthKeyExpiration = 60 * 3L;
    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final RedisRepository redisRepository;

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
        String data = redisRepository.getData("EMAIL-AUTH:${" + email + "}");
        if (data != null) {
            throw new AccountBookException(
                    String.format("(%s) 해당 이메일로 인증 메일이 전송되었습니다.", email),
                    VALIDATION_AUTH_EMAIL_HAS_ALREADY_BEEN_SENT
            );
        }
        
        String authKey = getAuthKey();

        String subject = "짠짠이 가입을 위한 인증 이메일입니다.";
        String text = "회원가입을 위한 인증번호는 " + authKey + "입니다.<br/>";

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
        redisRepository.setDataExpire(authKey, email, AuthKeyExpiration);
        redisRepository.setDataExpire(
                "EMAIL-AUTH:${" + email + "}",
                "이메일 인증 신청",
                AuthEmailRequestWillExpireIn
        );
    }

    public void completeAuthEmail(CompleteAuthEmailRequestDto request) {

        String email = redisRepository.getData(request.getAuthKey());

        validateEmail(email);

        try {
            if (!email.equals(request.getEmail())) { // 인증키로 가져온 이메일과 입력한 이메일이 다른 경우
                throw new AccountBookException(
                        "잘못된 이메일 입니다.",
                        VALIDATION_WRONG_EMAIL_PASSWORD_EXCEPTION
                );
            }
        } catch (NullPointerException e) { // 이메일에 해당하는 인증키가 없음
            throw new AccountBookException(
                    "잘못된 이메일 인증번호입니다.",
                    VALIDATION_EMAIL_AUTH_KEY_EXCEPTION
            );
        }
        // 인증 완료 후 하루안에 회원가입해야함
        redisRepository.setDataExpire(
                "EMAIL-AUTH:${" + email + "}",
                "이메일 인증 완료",
                AuthEmailRequestWillExpireIn);
    }

    private String getAuthKey() {
        String authKey;

        do {
            Random random = new Random();
            authKey = String.valueOf(random.nextInt(999999));
        } while (redisRepository.existKey(authKey));

        return authKey;
    }
}
