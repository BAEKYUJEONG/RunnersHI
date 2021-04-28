package com.ssafy.runnershi.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserInfo implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer userInfoId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User userId;

  @ManyToOne
  @JoinColumn(name = "user_name", referencedColumnName = "userName")
  private User userName;

  private Double totalDistance;
  private Integer totalTime;
  private Integer totalDay;
  private Double bestPace;
}
