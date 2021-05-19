package com.ssafy.runnershi.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ssafy.runnershi.entity.Record;
import com.ssafy.runnershi.entity.RecordDetail;
import com.ssafy.runnershi.entity.RecordResult;

public interface RecordRepository extends JpaRepository<Record, Long> {
  public RecordDetail findByRecordId(long recordId);

  public Record findByRecordIdAndUser_UserId_UserId(long recordId, String userId);

  public ArrayList<RecordResult> findByUser_UserId_UserId(String userId);

  public ArrayList<RecordResult> findByUser_UserId_UserIdAndEndTimeGreaterThanEqual(String userId,
      Date endTime);

  @Query(
      value = "SELECT COUNT(user_id) FROM record WHERE DATE_FORMAT(end_time, '%Y-%m-%d') = :today AND user_id = :userId",
      nativeQuery = true)
  public int searchTodayRecord(@Param("userId") String userId, @Param("today") String today);

  @Query(value = "SELECT * FROM record WHERE user_id in :friendList", nativeQuery = true)
  public ArrayList<Record> getFriendRecordList(@Param("friendList") List<String> friendList);

}
