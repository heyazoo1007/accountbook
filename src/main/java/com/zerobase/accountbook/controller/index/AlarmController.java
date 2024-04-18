package com.zerobase.accountbook.controller.budget;

import com.zerobase.accountbook.service.budget.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AlarmController {

    private final SimpMessageSendingOperations messagingTemplate; // SimpMessageSendingOperation 인터페이스가 뭐지?
    private final AlarmService alarmService;

    @GetMapping("/v1/budget/alarm")
    public String stompAlarm() { // 알림 테스트를 위한 화면
        return "test";
    }

    /**
     * @DestinationVariable 은 @PathVariable 과 비슷
     * /ws-stomp 로 소켓 연결하면, 클라이언트에서 /sub/{userId} 구독
     */
    @MessageMapping("/v1/budget/alarm/{userId}")
    public void sendNotification(@DestinationVariable("userId") Long userId) {
        messagingTemplate.convertAndSend("/sub/" + userId, alarmService.sendBudgetAlarm()); // 구독한 곳으로 객체 전송
    }
}
