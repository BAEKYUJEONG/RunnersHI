package com.ssafy.runnershi.service;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AndroidPushNotificationService {

  @Value("${FCM.SERVER_KEY}")
  private String firebase_server_key;
  private String firebase_api_url = "https://fcm.googleapis.com/fcm/send";

  @Async
  public CompletableFuture<String> send(String token, String title, String content) {

    JSONObject body = new JSONObject();

    body.put("to", token);

    JSONObject data = new JSONObject();
    data.put("title", title);
    data.put("body", content);

    body.put("data", data);

    HttpEntity<String> entity = new HttpEntity<>(body.toString());

    RestTemplate restTemplate = new RestTemplate();

    ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

    interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + firebase_server_key));
    interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json; UTF-8 "));
    restTemplate.setInterceptors(interceptors);

    String firebaseResponse = restTemplate.postForObject(firebase_api_url, entity, String.class);

    return CompletableFuture.completedFuture(firebaseResponse);
  }

}
