package com.ssafy.runnershi.entity;

import java.io.Serializable;
import javax.persistence.Column;
import lombok.Data;

@Data
public class AchievementPK implements Serializable {

  @Column
  private String user;
  @Column
  private String achievementInfo;

}
