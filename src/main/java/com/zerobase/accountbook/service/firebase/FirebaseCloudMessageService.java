package com.zerobase.accountbook.service.firebase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.sun.tools.javac.util.List;
import com.zerobase.accountbook.controller.firebase.dto.FcmMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static org.apache.http.HttpHeaders.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    private final String API_URL =
            "https://fcm.googleapis.com/v1/projects/[projectId]/messages:send";
    private final ObjectMapper objectMapper;

    private final OkHttpClient okHttpClient;

    // targetToken 의 경우 FCM 을 이용해 front 를 구현할 때 얻어낼 수 있다고 합니다.
    public void sendMessageTo(
            String targetToken, String title, String body
    ) throws IOException {
        String message = makeMessage(targetToken, title, body);

        RequestBody requestBody =
                RequestBody.create(
                        message,
                        MediaType.get("application/json; charset=utf-8")
                );
        Request request =
                new Request.Builder()
                        .url(API_URL)
                        .post(requestBody)
                        .addHeader(AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = okHttpClient.newCall(request).execute();

        log.info(response.body().string());
    }

    private String makeMessage(
            String targetToken, String title, String body
    ) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        )
                        .build()
                )
                .validate_only(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebase_service_key.json";

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                        new ClassPathResource(firebaseConfigPath)
                                .getInputStream()
                )
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
