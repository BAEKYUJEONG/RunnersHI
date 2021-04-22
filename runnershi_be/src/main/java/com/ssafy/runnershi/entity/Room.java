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
public class Room {
  @Id
  private String roomId;
  private String Title;
  private Integer roomType;
  private Integer runningType;
  private Integer count;
}
