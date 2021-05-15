package com.ssafy.runnershi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
  private String userId;
  private String userName;

  private double totalDistance;
  private int totalTime;
  private int totalDay;
  private double bestPace;

  private double weeklyDistance;
  private int weeklyTime;
  private double weeklyPace;
}
