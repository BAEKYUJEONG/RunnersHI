package com.ssafy.runnershi.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ssafy.runnershi.entity.AddFriendAlarm;
import com.ssafy.runnershi.entity.Alarm;
import com.ssafy.runnershi.entity.User;
import com.ssafy.runnershi.repository.AlarmRepository;
import com.ssafy.runnershi.repository.UserRepository;

@Service
public class AlarmServiceImpl implements AlarmService {

  @Autowired
  UserRepository userRepo;

  @Autowired
  AlarmRepository alarmRepo;

  @Override
  public List<AddFriendAlarm> getAddFriendList(String userId) {
    User user = userRepo.findByUserId(userId);
    if (user == null) {
      return null;
    }

    ArrayList<Alarm> alarmList = alarmRepo.findByUser_UserId_UserIdAndType(userId, 1);
    ArrayList<AddFriendAlarm> result = new ArrayList<AddFriendAlarm>();
    for (Alarm alarm : alarmList) {
      User friend = alarm.getFromUser().getUserId();
      result.add(new AddFriendAlarm(alarm.getAlarmId(), friend.getUserId(), friend.getUserName(),
          alarm.getContent()));
    }

    return result;
  }

}
