package com.ssafy.runnershi.entity;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public interface RecordDetail {
  long getRecordId();

  double getDistance();

  @Temporal(TemporalType.TIMESTAMP)
  Date getEndTime();

  int getRunningTime();

  String getTitle();
}
