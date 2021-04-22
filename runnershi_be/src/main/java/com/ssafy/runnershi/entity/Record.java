package com.ssafy.runnershi.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Record {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer recordId;
  private String userId;
  private String gpsPath;
  private Double distance;

  @Temporal(TemporalType.TIMESTAMP)
  private Date startTime;
  @Temporal(TemporalType.TIMESTAMP)
  private Date endTime;

  private Integer runningTime;
  private String title;
}
