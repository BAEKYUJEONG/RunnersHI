package com.ssafy.runnershi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Custom {
  @Id
  private String userId;
  @Column(nullable = true)
  private Integer goalTime;
  @Column(nullable = true)
  private Double goalDistance;
  private Byte runningType;
  private Byte alarmOn;
}
