package com.ssafy.runnershi.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@IdClass(FriendPK.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Friend {
  @Id
  private String userId;
  @Id
  private String friendUserId;
  private Byte alarm;
}
