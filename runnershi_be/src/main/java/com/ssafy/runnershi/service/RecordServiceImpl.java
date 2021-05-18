package com.ssafy.runnershi.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import com.ssafy.runnershi.entity.AllRecord;
import com.ssafy.runnershi.entity.Record;
import com.ssafy.runnershi.entity.RecordDetail;
import com.ssafy.runnershi.entity.RecordResult;
import com.ssafy.runnershi.entity.UserInfo;
import com.ssafy.runnershi.repository.FriendRepository;
import com.ssafy.runnershi.repository.RecordRepository;
import com.ssafy.runnershi.repository.UserInfoRepository;

@Service
public class RecordServiceImpl implements RecordService {

  @Autowired
  private RecordRepository recordRepo;

  @Autowired
  private UserInfoRepository userInfoRepo;

  @Autowired
  private FriendRepository friendRepo;

  @Autowired
  private ZSetOperations<String, String> zset;

  @Override
  public String createRecord(String userId, Record record) throws ParseException {
    UserInfo userInfo = userInfoRepo.findByUserId_UserId(userId);
    if (userInfo == null)
      return "invalid token";

    if (record.getEndTime() == null || record.getTitle() == null
        || "".contentEquals(record.getTitle()))
      return "invalid data";
    // if (record.getEndTime() == null) {
    // record.setEndTime(new Date());
    // }

    // 유저 정보 업데이트
    double pace = record.getRunningTime() / record.getDistance();
    userInfo.setBestPace(Math.max(userInfo.getBestPace(), pace));
    userInfo.setTotalDistance(userInfo.getTotalDistance() + record.getDistance());
    userInfo.setTotalTime(userInfo.getTotalTime() + record.getRunningTime());

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String date = format.format(new Date());
    // Date today = format.parse(date);
    // System.out.println(today);
    if (recordRepo.searchTodayRecord(userId, date) == null) {
      userInfo.setTotalDay(userInfo.getTotalDay() + 1);
    }

    userInfo.setThisWeekDistance(userInfo.getThisWeekDistance() + record.getDistance());
    userInfo.setThisWeekPace(Math.max(userInfo.getThisWeekPace(), pace));
    userInfo.setThisWeekTime(userInfo.getThisWeekTime() + record.getRunningTime());

    userInfoRepo.save(userInfo);

    // 기록 업데이트
    record.setUser(userInfo);
    recordRepo.save(record);

    // 랭킹 업데이트
    String value = userInfo.getUserId().getUserId() + ";" + userInfo.getUserName().getUserName();
    zset.add("totalDistanceRank", value, userInfo.getTotalDistance());
    zset.add("totalTimeRank", value, userInfo.getTotalTime());
    zset.add("totalPaceRank", value, userInfo.getBestPace());

    return "SUCCESS";
  }

  @Override
  public String deleteRecord(String userId, long recordId) throws ParseException {
    UserInfo userInfo = userInfoRepo.findByUserId_UserId(userId);
    if (userInfo == null)
      return "invalid token";

    Record record = recordRepo.findByRecordIdAndUser_UserId_UserId(recordId, userId);
    if (record == null) {
      return "invalid recordId or token";
    }

    // 테이블에서 기록 삭제
    recordRepo.delete(record);

    // 유저 정보 업데이트
    // total 기록 수정
    double pace = record.getRunningTime() / record.getDistance();
    if (pace == userInfo.getBestPace()) {
      ArrayList<RecordResult> list = recordRepo.findByUser_UserId_UserId(userId);
      double bestPace = 0;
      for (RecordResult recordResult : list) {
        double tmpPace = recordResult.getRunningTime() / recordResult.getDistance();
        bestPace = Math.max(bestPace, tmpPace);
      }
      userInfo.setBestPace(bestPace);
    }
    userInfo.setTotalDistance(userInfo.getTotalDistance() - record.getDistance());
    userInfo.setTotalTime(userInfo.getTotalTime() - record.getRunningTime());


    // 이번 주 내의 기록일 경우 thisWeek 기록도 삭제
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    Calendar cal = Calendar.getInstance();
    String date = format.format(new Date());
    cal.setTime(format.parse(date));

    Calendar recordCal = Calendar.getInstance();
    date = format.format(record.getEndTime());
    recordCal.setTime(format.parse(date));

    long diffSec = (cal.getTimeInMillis() - recordCal.getTimeInMillis()) / 1000;
    long diffDays = diffSec / (24 * 60 * 60); // 일자수 차이
    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

    if (diffDays < 7) {
      if (dayOfWeek == 1 || (diffDays <= (dayOfWeek - 2))) {
        userInfo.setThisWeekDistance(userInfo.getThisWeekDistance() - record.getDistance());
        userInfo.setThisWeekTime(userInfo.getThisWeekTime() - record.getRunningTime());
        if (pace == userInfo.getThisWeekPace()) {
          if (dayOfWeek == 1) {
            cal.add(Calendar.DATE, -6);

          } else {
            cal.add(Calendar.DATE, -(dayOfWeek - 2));
          }

          ArrayList<RecordResult> list =
              recordRepo.findByUser_UserId_UserIdAndEndTimeGreaterThanEqual(userId, cal.getTime());
          double bestPace = 0;
          for (RecordResult recordResult : list) {
            double tmpPace = recordResult.getRunningTime() / recordResult.getDistance();
            bestPace = Math.max(bestPace, tmpPace);
          }
          userInfo.setThisWeekPace(bestPace);
        }
      }
    }


    if (recordRepo.searchTodayRecord(userId, date) == null) {
      userInfo.setTotalDay(userInfo.getTotalDay() - 1);
    }

    userInfoRepo.save(userInfo);

    // 랭킹 업데이트
    String value = userInfo.getUserId().getUserId() + ";" + userInfo.getUserName().getUserName();
    zset.add("totalDistanceRank", value, userInfo.getTotalDistance());
    zset.add("totalTimeRank", value, userInfo.getTotalTime());
    zset.add("totalPaceRank", value, userInfo.getBestPace());

    return "SUCCESS";
  }

  @Override
  public List<RecordResult> recordList(String userId) {
    return recordRepo.findByUser_UserId_UserId(userId);
  }

  @Override
  public RecordDetail recordDetail(long recordId) {
    return recordRepo.findByRecordId(recordId);
  }

  @Override
  public List<AllRecord> allRecordList(String userId) {
    UserInfo userInfo = userInfoRepo.findByUserId_UserId(userId);
    if (userInfo == null)
      return null;

    // ArrayList<Record> recordList = (ArrayList<Record>) recordRepo.findAll();
    // ArrayList<AllRecord> result = new ArrayList<AllRecord>();
    // for (Record record : recordList) {
    // result.add(new AllRecord(record.getRecordId(), record.getUser().getUserName().getUserName(),
    // record.getDistance(), record.getEndTime(), record.getRunningTime(), record.getTitle()));
    // }
    //
    // return result;

    ArrayList<Record> recordList =
        recordRepo.getFriendRecordList(friendRepo.getFriendsUserId(userId));
    ArrayList<AllRecord> result = new ArrayList<AllRecord>();
    for (Record record : recordList) {
      result.add(new AllRecord(record.getRecordId(), record.getUser().getUserName().getUserName(),
          record.getDistance(), record.getEndTime(), record.getRunningTime(), record.getTitle()));
    }

    return result;
  }

  @Override
  public RecordDetail recordTitleUpdate(Record r, String userId) {
    if (r.getTitle() == null || "".equals(r.getTitle()))
      return null;
    Record record = recordRepo.findById(r.getRecordId()).orElse(null);
    if (record == null || !record.getUser().getUserId().getUserId().equals(userId))
      return null;

    record.setTitle(r.getTitle());
    recordRepo.save(record);

    return recordRepo.findByRecordId(record.getRecordId());
  }

}
