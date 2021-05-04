package com.ssafy.runnershi.service;

public interface FriendService {

  public String addFriend(String userId, String friendName);

  public String deleteFriend(String userId, String friendUserId);

  public String acceptFriend(String userId, String friendUserId, Integer alarmId);

  public String rejectFriend(String userId, Integer alarmId);

}
