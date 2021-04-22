package com.ssafy.runnershi.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@IdClass(AchievementPK.class)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Achievement {
  @Id
  private String userId;
  @Id
  private String achievementInfoName;
}
