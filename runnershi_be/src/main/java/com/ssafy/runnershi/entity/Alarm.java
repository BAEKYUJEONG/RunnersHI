package com.ssafy.runnershi.entity;

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
public class Alarm {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer alarmId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserInfo user;

  private String content;
  private Integer type;
}
