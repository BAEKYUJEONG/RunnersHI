package com.ssafy.runnershi.service;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import com.ssafy.runnershi.entity.Alarm;
import com.ssafy.runnershi.entity.Friend;
import com.ssafy.runnershi.entity.FriendIdName;
import com.ssafy.runnershi.entity.FriendListResult;
import com.ssafy.runnershi.entity.User;
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

  @Autowired
  private AndroidPushNotificationService androidPushNotificationService;

  @Autowired
  private SetOperations<String, String> set;

  @Autowired
  private ZSetOperations<String, String> zset;

  @Override
  public String addFriend(String userId, String friendName) {

    UserInfo fromUser = userInfoRepo.findByUserId_UserId(userId);
    UserInfo toUser = userInfoRepo.findByUserName_UserName(friendName);
    System.out.println(fromUser);
    System.out.println(toUser);
    if (fromUser == null || toUser == null || userId.equals(toUser.getUserId().getUserId()))
      return "FAIL";

    String content = "'" + fromUser.getUserName().getUserName() + "'님의 친구신청입니다.";

    // 알람 DB에 추가
    Alarm alarm = new Alarm();
    alarm.setContent(content);
    alarm.setType(1);
    alarm.setUser(toUser);
    alarm.setFromUser(fromUser);
    alarmRepo.save(alarm);


    // 알람 보내기
    // CompletableFuture<String> pushNotification =
    // androidPushNotificationService.send("토큰", "친구 신청", content);
    // CompletableFuture.allOf(pushNotification).join();
    // try {
    // String firebaseResponse = pushNotification.get();
    // return "SUCCESS";
    // } catch (Exception e) {
    // e.printStackTrace();
    // return "FAIL";
    // }

    return "SUCCESS";

  }


  @Override
  public String acceptFriend(String userId, String friendUserId, long alarmId) {

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

      set.add(fromUser.getUserId().getUserId() + ";" + fromUser.getUserName().getUserName(),
          toUser.getUserId().getUserId() + ";" + toUser.getUserName().getUserName());

    }

    friend = friendRepo.findByUser_UserId_UserIdAndFriendUser_UserId_UserId(friendUserId, userId);
    if (friend == null) {
      Friend friend2 = new Friend();
      friend2.setAlarm((byte) 1);
      friend2.setFriendUser(fromUser);
      friend2.setUser(toUser);
      friendRepo.save(friend2);

      set.add(toUser.getUserId().getUserId() + ";" + toUser.getUserName().getUserName(),
          fromUser.getUserId().getUserId() + ";" + fromUser.getUserName().getUserName());

    }

    alarmRepo.delete(alarm);

    return "SUCCESS";
  }


  @Override
  public String rejectFriend(String userId, long alarmId) {

    Alarm alarm = alarmRepo.findById(alarmId).orElse(null);
    if (alarm == null)
      return "FAIL";
    alarmRepo.delete(alarm);

    return "SUCCESS";
  }


  @Override
  public String deleteFriend(String userId, String friendUserId) {
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


  @Override
  public FriendListResult friendList(String userId) {
    User user = userRepo.findByUserId(userId);
    if (user == null)
      return null;

    FriendListResult result = new FriendListResult();
    ArrayList<Friend> list = friendRepo.findByUser_UserId_UserId(userId);
    result.setFriendNum(list.size());
    result.setFriendList(new ArrayList<FriendIdName>());
    for (Friend friend : list) {
      User friendUser = friend.getFriendUser().getUserId();
      FriendIdName friendIdName = new FriendIdName(friendUser.getUserId(), friendUser.getUserName(),
          zset.reverseRank("totalDistanceRank",
              friendUser.getUserId() + ";" + friendUser.getUserName()) + 1);
      result.getFriendList().add(friendIdName);
    }

    return result;
  }


}
