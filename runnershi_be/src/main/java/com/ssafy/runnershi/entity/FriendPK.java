package com.ssafy.runnershi.entity;

import java.io.Serializable;
import javax.persistence.Column;
import lombok.Data;

@Data
public class FriendPK implements Serializable {

  @Column
  private String user; // Friend.userId 매핑
  @Column
  private String friendUser; // Friend.friendUserId 매핑

}
