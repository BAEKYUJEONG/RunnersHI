package com.ssafy.runnershi.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @IdClass(FriendPK.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Friend implements Serializable {
  // @Id
  // private String userId;
  // @Id
  // private String friendUserId;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long friendId;

  // @ManyToOne
  // @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  // private UserInfo user;

  // @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private UserInfo user;

  // @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "friend_user_id", referencedColumnName = "user_id")
  private UserInfo friendUser;

  private Byte alarm;
}
