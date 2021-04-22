package com.ssafy.runnershi.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserInfo {
  @Id
  private String userId;
  private Double totalDistance;
  private Integer totalTime;
  private Integer totalDay;
  private Integer bestPace;
}
