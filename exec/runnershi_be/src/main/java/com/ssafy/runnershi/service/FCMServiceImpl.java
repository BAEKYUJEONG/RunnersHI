package com.ssafy.runnershi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ssafy.runnershi.entity.UserInfo;
import com.ssafy.runnershi.repository.UserInfoRepository;

@Service
public class FCMServiceImpl implements FCMService {

  @Autowired
  UserInfoRepository userInfoRepo;

  @Override
  public String addToken(String userId, String msgToken) {

    UserInfo userInfo = userInfoRepo.findByUserId_UserId(userId);
    if (userInfo == null) {
      return "invalid token";
    }

    userInfo.setMsgToken(msgToken);
    userInfoRepo.save(userInfo);

    return "SUCCESS";
  }

}
