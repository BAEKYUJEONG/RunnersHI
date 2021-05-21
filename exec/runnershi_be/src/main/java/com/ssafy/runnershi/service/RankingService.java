package com.ssafy.runnershi.service;

public interface RankingService {
  public void reEnrollRanks();

  public Object totalAll(String type, int offset);

  public Object weeklyAll(String type, int offset);

  public Object totalFriend(String userId, String type, int offset);

  public Object weeklyFriend(String userId, String type, int offset);

}
