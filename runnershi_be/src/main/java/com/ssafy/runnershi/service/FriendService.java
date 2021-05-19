package com.ssafy.runnershi.service;

import com.ssafy.runnershi.entity.FriendListResult;

public interface FriendService {

  public String addFriend(String userId, String friendName);

  public String deleteFriend(String userId, String friendUserId);

  public String acceptFriend(String userId, String friendUserId, long alarmId);

  public String rejectFriend(String userId, long alarmId);

  public FriendListResult friendList(String userId);

  public String addFriendTest(String userId1, String userId2);

}
