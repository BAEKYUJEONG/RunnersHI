package com.ssafy.runnershi.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserInfo implements Serializable {
  @Id
  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  private Double totalDistance;
  private Integer totalTime;
  private Integer totalDay;
  private Integer bestPace;
}
