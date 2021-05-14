package com.ssafy.runnershi.entity;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public interface RecordResult {
  long getRecordId();

  double getDistance();

  @Temporal(TemporalType.TIMESTAMP)
  Date getStartTime();

  int getRunningTime();

  String getTitle();

}
