package com.ssafy.runnershi.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@IdClass(FriendPK.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Friend {
  // @Id
  // private String userId;
  // @Id
  // private String friendUserId;

  @Id
  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserInfo user;

  @Id
  @ManyToOne
  @JoinColumn(name = "friend_user_id")
  private UserInfo friendUser;

  private Byte alarm;
}
