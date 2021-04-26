package com.ssafy.runnershi.service;

import com.ssafy.runnershi.entity.User;
import com.ssafy.runnershi.entity.UserResult;

public interface UserService {

  public UserResult signin(String accessToken);

  public UserResult enterName(User user);

  public UserResult enterRunningType(UserResult user);

  public String nameChk(String name);

}
