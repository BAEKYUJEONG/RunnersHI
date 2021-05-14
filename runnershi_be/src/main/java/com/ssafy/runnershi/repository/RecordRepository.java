package com.ssafy.runnershi.repository;

import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.runnershi.entity.Record;
import com.ssafy.runnershi.entity.RecordResult;

public interface RecordRepository extends JpaRepository<Record, Long> {
  public Record findByRecordIdAndUser_UserId_UserId(long recordId, String userId);

  public ArrayList<RecordResult> findByUser_UserId_UserId(String userId);
}
