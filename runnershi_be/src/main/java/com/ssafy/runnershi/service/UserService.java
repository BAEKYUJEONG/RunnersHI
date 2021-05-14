package com.ssafy.runnershi.service;

import java.util.List;
import java.util.Map;
import com.ssafy.runnershi.entity.Profile;
import com.ssafy.runnershi.entity.SearchResult;
import com.ssafy.runnershi.entity.User;
import com.ssafy.runnershi.entity.UserResult;

public interface UserService {

  public UserResult signInKakao(String accessToken);

  public UserResult signInNaver(String accessToken);

  public UserResult enterName(User user);

  public UserResult enterRunningType(UserResult user);

  public Map nameChk(String name);

  public Map emailChk(String email);

  public UserResult signUpRunHi(User user);

  public UserResult signInRunHi(User user);

  public String pwdChk(User user);

  public String leave(String userId);

  public List<SearchResult> searchUser(String userId, String word);

  public Profile getUserProfile(String userId);

}
