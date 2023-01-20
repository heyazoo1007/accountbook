package com.zerobase.accountbook.controller.firebase.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FcmMessage {

    private boolean validate_only;
    private Message message;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Message {

        private Notification notification;
        private String token;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }
}
