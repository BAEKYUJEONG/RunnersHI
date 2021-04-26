package com.ssafy.runnershi.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.runnershi.entity.Custom;
import com.ssafy.runnershi.entity.User;
import com.ssafy.runnershi.entity.UserInfo;
import com.ssafy.runnershi.entity.UserResult;
import com.ssafy.runnershi.repository.CustomRepository;
import com.ssafy.runnershi.repository.UserInfoRepository;
import com.ssafy.runnershi.repository.UserRepository;


@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private UserInfoRepository userInfoRepo;

  @Autowired
  private CustomRepository customRepo;

  @Autowired
  private JwtService jwtService;

  @Override
  public UserResult signin(String accessToken) {
    String reqURL = "https://kapi.kakao.com/v2/user/me";
    try {
      URL url = new URL(reqURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");

      // 요청에 필요한 Header에 포함될 내용
      conn.setRequestProperty("Authorization", "Bearer " + accessToken);
      // int responseCode = conn.getResponseCode();
      // System.out.println("responseCode : " + responseCode);

      BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

      String line = "";
      String result = "";

      while ((line = br.readLine()) != null) {
        result += line;
      }

      ObjectMapper mapper = new ObjectMapper();
      JsonNode kakaoUserInfo = mapper.readTree(result);

      String userId = kakaoUserInfo.path("id").asText();
      User user = userRepo.findByUserId(userId);

      UserResult userResult = new UserResult();

      // 회원가입
      if (user == null) {

        userResult.setUserId(userId);
        return userResult;

      }

      // 로그인
      userResult.setToken(jwtService.create(user.getUserId()));
      userResult.setUserId(user.getUserId());
      userResult.setUserName(user.getUserName());
      Custom custom = customRepo.findByUser_UserId_UserId(user.getUserId());
      if (custom != null)
        userResult.setRunningType(custom.getRunningType());

      return userResult;

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }


  }

  @Override
  public UserResult enterName(User idName) {

    if (idName == null || idName.getUserId() == null || idName.getUserName() == null)
      return null;

    User user = new User();
    user.setUserId(idName.getUserId());
    user.setUserName(idName.getUserName());
    user.setHasCustom((byte) 0);
    user.setFlag((byte) 0);
    userRepo.save(user);

    UserInfo userInfo = new UserInfo();
    userInfo.setUserId(user);
    userInfo.setUserName(user);
    userInfoRepo.save(userInfo);

    UserResult userResult = new UserResult();
    userResult.setToken(jwtService.create(user.getUserId()));
    userResult.setUserId(user.getUserId());
    userResult.setUserName(user.getUserName());

    return userResult;
  }

  @Override
  public UserResult enterRunningType(UserResult userResult) {

    if (userResult == null || userResult.getRunningType() == null || userResult.getUserId() == null
        || userResult.getToken() == null || userResult.getUserName() == null)
      return null;

    UserInfo userInfo = userInfoRepo.findByUserId_UserId(userResult.getUserId());
    System.out.println(userInfo);
    Custom custom = new Custom();
    custom.setUser(userInfo);
    custom.setRunningType(userResult.getRunningType());
    customRepo.save(custom);

    return userResult;
  }

  @Override
  public String nameChk(String name) {

    User user = userRepo.findByUserId(name);
    if (user == null)
      return "valid";

    return "invalid";
  }

}
