package com.ssafy.runnershi.service;

import java.util.List;
import com.ssafy.runnershi.entity.Record;
import com.ssafy.runnershi.entity.RecordResult;

public interface RecordService {

  public String deleteRecord(String userId, long recordId);

  public List<RecordResult> recordList(String userId);

  public Record recordDetail(long recordId);

}
