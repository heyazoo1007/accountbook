package com.zerobase.accountbook.controller.index;

import com.zerobase.accountbook.controller.budget.dto.response.SendBudgetAlarmDto;
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

// 알림 보내는 페이지로 이동
//    @GetMapping("/v1/budget/alarm")
//    public String stompAlarm() {
//        return "test";
//    }

    /**
     * @DestinationVariable 은 @PathVariable 과 비슷
     * /ws-stomp 로 소켓 연결하면, 클라이언트에서 /sub/{memberId} 구독
     */
    @MessageMapping("/v1/budget/alarm/{memberId}")
    public void sendNotification(@DestinationVariable("memberId") Long memberId) {
        SendBudgetAlarmDto dto = alarmService.sendBudgetAlarm(memberId);
        messagingTemplate.convertAndSend("/sub/" + memberId, dto); // 구독한 곳으로 객체 전송
    }
}
