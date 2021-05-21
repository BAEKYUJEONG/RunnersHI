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
public class AchievementInfo {
  @Id
  private String achievementInfoName;
  private String content;
}
