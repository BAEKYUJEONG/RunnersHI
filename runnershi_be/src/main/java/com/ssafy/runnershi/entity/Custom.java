package com.ssafy.runnershi.entity;

import java.io.Serializable;
import javax.persistence.Column;
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
public class Custom implements Serializable {
  @Id
  @OneToOne
  @JoinColumn(name = "user_id")
  private UserInfo user;

  @Column(nullable = true)
  private Integer goalTime;
  @Column(nullable = true)
  private Double goalDistance;
  private Byte runningType;
  private Byte alarmOn;
}
