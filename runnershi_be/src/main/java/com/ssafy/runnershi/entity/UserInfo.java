package com.ssafy.runnershi.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.DynamicUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
public class UserInfo implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userInfoId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_name", referencedColumnName = "userName", unique = true)
  private User userName;

  private double totalDistance;
  private int totalTime;
  private int totalDay;
  private double bestPace;

  private double weeklyDistance;
  private int weeklyTime;
  private double weeklyPace;

  private double thisWeekDistance;
  private int thisWeekTime;
  private double thisWeekPace;

}
