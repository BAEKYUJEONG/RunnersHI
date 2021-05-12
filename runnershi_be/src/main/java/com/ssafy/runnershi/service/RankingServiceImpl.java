package com.ssafy.runnershi.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.ssafy.runnershi.entity.DistanceRank;
import com.ssafy.runnershi.entity.Friend;
import com.ssafy.runnershi.entity.PaceRank;
import com.ssafy.runnershi.entity.TimeRank;
import com.ssafy.runnershi.entity.User;
import com.ssafy.runnershi.entity.UserInfo;
import com.ssafy.runnershi.repository.FriendRepository;
import com.ssafy.runnershi.repository.UserInfoRepository;
import com.ssafy.runnershi.repository.UserRepository;

@Service
public class RankingServiceImpl implements RankingService {

  @Autowired
  private RedisTemplate<String, String> template;

  @Autowired
  private ZSetOperations<String, String> zset;

  @Autowired
  private SetOperations<String, String> set;

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private UserInfoRepository userInfoRepo;

  @Autowired
  private FriendRepository friendRepo;

  private int n = 50;

  @Override
  public void reEnrollRanks() {
    ArrayList<UserInfo> userList = (ArrayList<UserInfo>) userInfoRepo.findAll();
    ArrayList<Friend> friendList = null;

    for (UserInfo userInfo : userList) {
      String value = userInfo.getUserId().getUserId() + ";" + userInfo.getUserName().getUserName();
      zset.add("totalDistanceRank", value, userInfo.getTotalDistance());
      zset.add("totalTimeRank", value, userInfo.getTotalTime());
      zset.add("totalPaceRank", value, userInfo.getBestPace());

      zset.add("weeklyDistanceRank", value, userInfo.getWeeklyDistance());
      zset.add("weeklyTimeRank", value, userInfo.getWeeklyTime());
      zset.add("weeklyPaceRank", value, userInfo.getWeeklyPace());

      set.add(value, value);

      friendList = friendRepo.findByUser_UserId_UserId(userInfo.getUserId().getUserId());
      for (Friend friend : friendList) {
        User friendUser = friend.getFriendUser().getUserName();
        set.add(value, friendUser.getUserId() + ";" + friendUser.getUserName());
      }

    }

  }


  // 매주 월요일 00:00:00에 주간 순위 업데이트
  @Scheduled(cron = "0 0 0 ? * MON", zone = "Asia/Seoul")
  public void updateWeeklyRanking() {
    ArrayList<UserInfo> userList = (ArrayList<UserInfo>) userInfoRepo.findAll();
    ArrayList<UserInfo> updateUserList = new ArrayList<UserInfo>();
    for (UserInfo userInfo : userList) {
      String value = userInfo.getUserId().getUserId() + ";" + userInfo.getUserName().getUserName();

      userInfo.setWeeklyDistance(userInfo.getThisWeekDistance());
      userInfo.setWeeklyPace(userInfo.getThisWeekPace());
      userInfo.setWeeklyTime(userInfo.getThisWeekTime());
      userInfo.setThisWeekDistance(0);
      userInfo.setThisWeekPace(0);
      userInfo.setThisWeekTime(0);
      updateUserList.add(userInfo);

      zset.add("weeklyDistanceRank", value, userInfo.getWeeklyDistance());
      zset.add("weeklyTimeRank", value, userInfo.getWeeklyTime());
      zset.add("weeklyPaceRank", value, userInfo.getWeeklyPace());

    }

    userInfoRepo.saveAll(userList);
  }


  @Override
  public Object totalAll(String type, int offset) {

    if ("distance".equals(type)) {

      DistanceRank dRank = null;

      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("totalDistanceRank", 0 + (offset * n), n + (offset * n) - 1);
      List<DistanceRank> ranks = new ArrayList<DistanceRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        dRank = new DistanceRank(user_info[0], user_info[1], current.getScore());
        ranks.add(dRank);
      }
      return ranks;

    } else if ("time".equals(type)) {
      TimeRank tRank = null;

      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("totalTimeRank", 0 + (offset * n), n + (offset * n) - 1);
      List<TimeRank> ranks = new ArrayList<TimeRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        tRank = new TimeRank(user_info[0], user_info[1], current.getScore().intValue());
        ranks.add(tRank);
      }
      return ranks;

    } else if ("pace".equals(type)) {
      PaceRank pRank = null;

      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("totalPaceRank", 0 + (offset * n), n + (offset * n) - 1);
      List<PaceRank> ranks = new ArrayList<PaceRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        pRank = new PaceRank(user_info[0], user_info[1], current.getScore().intValue());
        ranks.add(pRank);
      }
      return ranks;
    }

    return null;
  }


  @Override
  public Object weeklyAll(String type, int offset) {

    if ("distance".equals(type)) {

      DistanceRank dRank = null;

      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("weeklyDistanceRank", 0 + (offset * n), n + (offset * n) - 1);
      List<DistanceRank> ranks = new ArrayList<DistanceRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        dRank = new DistanceRank(user_info[0], user_info[1], current.getScore());
        ranks.add(dRank);
      }
      return ranks;

    } else if ("time".equals(type)) {
      TimeRank tRank = null;

      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("weeklyTimeRank", 0 + (offset * n), n + (offset * n) - 1);
      List<TimeRank> ranks = new ArrayList<TimeRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        tRank = new TimeRank(user_info[0], user_info[1], current.getScore().intValue());
        ranks.add(tRank);
      }
      return ranks;

    } else if ("pace".equals(type)) {
      PaceRank pRank = null;

      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("weeklyPaceRank", 0 + (offset * n), n + (offset * n) - 1);
      List<PaceRank> ranks = new ArrayList<PaceRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        pRank = new PaceRank(user_info[0], user_info[1], current.getScore().intValue());
        ranks.add(pRank);
      }
      return ranks;
    }

    return null;
  }


  @Override
  public Object totalFriend(String userId, String type, int offset) {
    User user = userRepo.findByUserId(userId);
    if (user == null) {
      return null;
    }

    if ("distance".equals(type)) {

      DistanceRank dRank = null;
      List<String> otherkeys = new ArrayList<String>();
      otherkeys.add("totalDistanceRank");
      zset.intersectAndStore(user.getUserId() + ";" + user.getUserName(), otherkeys, "friendRank",
          null, RedisZSetCommands.Weights.of(0, 1));
      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("friendRank", 0 + (offset * n), n + (offset * n) - 1);
      List<DistanceRank> ranks = new ArrayList<DistanceRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        dRank = new DistanceRank(user_info[0], user_info[1], current.getScore());
        ranks.add(dRank);
      }
      return ranks;

    } else if ("time".equals(type)) {
      TimeRank tRank = null;

      List<String> otherkeys = new ArrayList<String>();
      otherkeys.add("totalTimeRank");
      zset.intersectAndStore(user.getUserId() + ";" + user.getUserName(), otherkeys, "friendRank",
          null, RedisZSetCommands.Weights.of(0, 1));
      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("friendRank", 0 + (offset * n), n + (offset * n) - 1);
      List<TimeRank> ranks = new ArrayList<TimeRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        tRank = new TimeRank(user_info[0], user_info[1], current.getScore().intValue());
        ranks.add(tRank);
      }
      return ranks;

    } else if ("pace".equals(type)) {
      PaceRank pRank = null;

      List<String> otherkeys = new ArrayList<String>();
      otherkeys.add("totalPaceRank");
      zset.intersectAndStore(user.getUserId() + ";" + user.getUserName(), otherkeys, "friendRank",
          null, RedisZSetCommands.Weights.of(0, 1));
      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("friendRank", 0 + (offset * n), n + (offset * n) - 1);
      List<PaceRank> ranks = new ArrayList<PaceRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        pRank = new PaceRank(user_info[0], user_info[1], current.getScore().intValue());
        ranks.add(pRank);
      }
      return ranks;
    }

    return null;
  }


  @Override
  public Object weeklyFriend(String userId, String type, int offset) {
    User user = userRepo.findByUserId(userId);
    if (user == null) {
      return null;
    }

    if ("distance".equals(type)) {

      DistanceRank dRank = null;
      List<String> otherkeys = new ArrayList<String>();
      otherkeys.add("weeklyDistanceRank");
      zset.intersectAndStore(user.getUserId() + ";" + user.getUserName(), otherkeys, "friendRank",
          null, RedisZSetCommands.Weights.of(0, 1));
      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("friendRank", 0 + (offset * n), n + (offset * n) - 1);
      List<DistanceRank> ranks = new ArrayList<DistanceRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        dRank = new DistanceRank(user_info[0], user_info[1], current.getScore());
        ranks.add(dRank);
      }
      return ranks;

    } else if ("time".equals(type)) {
      TimeRank tRank = null;

      List<String> otherkeys = new ArrayList<String>();
      otherkeys.add("weeklyTimeRank");
      zset.intersectAndStore(user.getUserId() + ";" + user.getUserName(), otherkeys, "friendRank",
          null, RedisZSetCommands.Weights.of(0, 1));
      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("friendRank", 0 + (offset * n), n + (offset * n) - 1);
      List<TimeRank> ranks = new ArrayList<TimeRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        tRank = new TimeRank(user_info[0], user_info[1], current.getScore().intValue());
        ranks.add(tRank);
      }
      return ranks;

    } else if ("pace".equals(type)) {
      PaceRank pRank = null;

      List<String> otherkeys = new ArrayList<String>();
      otherkeys.add("weeklyPaceRank");
      zset.intersectAndStore(user.getUserId() + ";" + user.getUserName(), otherkeys, "friendRank",
          null, RedisZSetCommands.Weights.of(0, 1));
      Set<ZSetOperations.TypedTuple<String>> rankSet =
          zset.reverseRangeWithScores("friendRank", 0 + (offset * n), n + (offset * n) - 1);
      List<PaceRank> ranks = new ArrayList<PaceRank>();
      Iterator<ZSetOperations.TypedTuple<String>> iterator = rankSet.iterator();

      while (iterator.hasNext()) {
        ZSetOperations.TypedTuple<String> current = iterator.next();

        String[] user_info = current.getValue().split(";");
        pRank = new PaceRank(user_info[0], user_info[1], current.getScore().intValue());
        ranks.add(pRank);
      }
      return ranks;
    }

    return null;
  }

}
