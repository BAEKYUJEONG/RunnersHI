package com.ssafy.runnershi.service;

import java.util.List;
import com.ssafy.runnershi.entity.AddFriendAlarm;

public interface AlarmService {

  public List<AddFriendAlarm> getAddFriendList(String userId);

}
