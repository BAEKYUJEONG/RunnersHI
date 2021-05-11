package com.ssafy.runnershi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistanceRank {
  private String userId;
  private String userName;
  private double distance;
}
