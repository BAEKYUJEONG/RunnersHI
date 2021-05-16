package com.ssafy.runnershi.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllRecord {
  private long recordId;
  private String userName;
  private double distance;
  private Date endTime;
  private int runningTime;
  private String title;
}
