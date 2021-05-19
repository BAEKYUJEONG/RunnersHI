package com.ssafy.runnershi.repository;

import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.runnershi.entity.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
  public Alarm findByUser_UserId_UserIdAndFromUser_UserId_UserIdAndType(String userId,
      String fromUserId, int type);

  public ArrayList<Alarm> findByUser_UserId_UserIdAndType(String userId, int type);
}
