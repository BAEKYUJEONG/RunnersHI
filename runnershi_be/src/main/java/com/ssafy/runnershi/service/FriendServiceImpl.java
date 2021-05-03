package com.ssafy.runnershi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ssafy.runnershi.entity.Alarm;
import com.ssafy.runnershi.entity.Friend;
import com.ssafy.runnershi.entity.UserInfo;
import com.ssafy.runnershi.repository.AlarmRepository;
import com.ssafy.runnershi.repository.FriendRepository;
import com.ssafy.runnershi.repository.UserInfoRepository;
import com.ssafy.runnershi.repository.UserRepository;

@Service
public class FriendServiceImpl implements FriendService {

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private UserInfoRepository userInfoRepo;

  @Autowired
  private FriendRepository friendRepo;

  @Autowired
  private AlarmRepository alarmRepo;

  @Override
  public String addFriend(String userId, String friendName) {

    UserInfo fromUser = userInfoRepo.findByUserId_UserId(userId);
    UserInfo toUser = userInfoRepo.findByUserName_UserName(friendName);
    if (fromUser == null || toUser == null || userId.equals(toUser.getUserId()))
      return "FAIL";

    // 알람 DB에 추가
    Alarm alarm = new Alarm();
    alarm.setContent("'" + fromUser.getUserName().getUserName() + "'님의 친구신청입니다.");
    alarm.setType(1);
    alarm.setUser(toUser);
    alarm.setFromUser(fromUser);
    alarmRepo.save(alarm);


    // 알람 보내기


    return "SUCCESS";
  }


  @Override
  public String acceptFriend(String userId, String friendUserId, Integer alarmId) {

    UserInfo fromUser = userInfoRepo.findByUserId_UserId(userId);
    UserInfo toUser = userInfoRepo.findByUserId_UserId(friendUserId);
    Alarm alarm = alarmRepo.findById(alarmId).orElse(null);

    if (fromUser == null || toUser == null || alarm == null)
      return "FAIL";

    Friend friend =
        friendRepo.findByUser_UserId_UserIdAndFriendUser_UserId_UserId(userId, friendUserId);
    if (friend == null) {
      Friend friend1 = new Friend();
      friend1.setAlarm((byte) 1);
      friend1.setFriendUser(toUser);
      friend1.setUser(fromUser);
      friendRepo.save(friend1);
    }

    friend = friendRepo.findByUser_UserId_UserIdAndFriendUser_UserId_UserId(friendUserId, userId);
    if (friend == null) {
      Friend friend2 = new Friend();
      friend2.setAlarm((byte) 1);
      friend2.setFriendUser(fromUser);
      friend2.setUser(toUser);
      friendRepo.save(friend2);
    }

    alarmRepo.delete(alarm);

    return "SUCCESS";
  }


  @Override
  public String rejectFriend(String userId, Integer alarmId) {

    Alarm alarm = alarmRepo.findById(alarmId).orElse(null);
    if (alarm == null)
      return "FAIL";
    alarmRepo.delete(alarm);

    return "SUCCESS";
  }


  @Override
  public String deleteFriend(String userId, String friendUserId) {
    System.out.println(userId);
    System.out.println(friendUserId);
    Friend friend =
        friendRepo.findByUser_UserId_UserIdAndFriendUser_UserId_UserId(userId, friendUserId);
    if (friend == null) {
      return "FAIL";
    }
    friendRepo.delete(friend);

    friend = friendRepo.findByUser_UserId_UserIdAndFriendUser_UserId_UserId(friendUserId, userId);
    if (friend == null) {
      return "FAIL";
    }
    friendRepo.delete(friend);

    return "SUCCESS";
  }


}
