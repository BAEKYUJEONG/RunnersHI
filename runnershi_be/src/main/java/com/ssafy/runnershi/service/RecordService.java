package com.ssafy.runnershi.service;

import java.text.ParseException;
import java.util.List;
import com.ssafy.runnershi.entity.AllRecord;
import com.ssafy.runnershi.entity.Record;
import com.ssafy.runnershi.entity.RecordDetail;
import com.ssafy.runnershi.entity.RecordResult;

public interface RecordService {

  public String deleteRecord(String userId, long recordId) throws ParseException;

  public List<RecordResult> recordList(String userId);

  public RecordDetail recordDetail(long recordId);

  public String createRecord(String userId, Record record) throws ParseException;

  public List<AllRecord> allRecordList(String userId);

  public RecordDetail recordTitleUpdate(Record record, String UserId);

}
