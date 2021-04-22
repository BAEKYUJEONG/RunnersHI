package com.ssafy.runnershi.entity;

import java.io.Serializable;
import javax.persistence.Column;
import lombok.Data;

@Data
public class FriendPK implements Serializable {

  @Column
  private String userId; // Friend.userId 매핑
  @Column
  private String friendUserId; // Friend.friendUserId 매핑


}
