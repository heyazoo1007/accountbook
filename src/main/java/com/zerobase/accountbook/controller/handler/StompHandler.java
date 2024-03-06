package com.zerobase.accountbook.controller.handler;

import com.zerobase.accountbook.common.config.security.JwtTokenProvider;
import com.zerobase.accountbook.common.exception.ErrorCode;
import com.zerobase.accountbook.common.exception.model.AccountBookException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.CONNECT) { // CONNECT 인 상황에서 토큰 검증
            if (!jwtTokenProvider.isValidateToken(accessor.getFirstNativeHeader("token"))) {
                throw new AccountBookException("", ErrorCode.VALIDATION_EXCEPTION); // 유효하지 않은 토큰에서 예외 발생
            }
        }
        return message;
    }
}
