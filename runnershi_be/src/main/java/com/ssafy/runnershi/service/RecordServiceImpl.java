package com.ssafy.runnershi.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ssafy.runnershi.entity.Record;
import com.ssafy.runnershi.entity.RecordResult;
import com.ssafy.runnershi.repository.RecordRepository;

@Service
public class RecordServiceImpl implements RecordService {

  @Autowired
  private RecordRepository recordRepo;

  @Override
  public String deleteRecord(String userId, long recordId) {
    Record record = recordRepo.findByRecordIdAndUser_UserId_UserId(recordId, userId);
    if (record == null) {
      return "invalid recordId or token";
    }
    recordRepo.delete(record);
    return null;
  }

  @Override
  public List<RecordResult> recordList(String userId) {
    return recordRepo.findByUser_UserId_UserId(userId);
  }

  @Override
  public Record recordDetail(long recordId) {
    return recordRepo.findById(recordId).orElse(null);
  }

}
