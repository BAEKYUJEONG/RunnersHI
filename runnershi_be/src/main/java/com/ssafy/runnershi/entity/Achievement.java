package com.ssafy.runnershi.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserInfo user;

  @Id
  @ManyToOne
  @JoinColumn(name = "achievement_info_name")
  private AchievementInfo achievementInfo;
}
