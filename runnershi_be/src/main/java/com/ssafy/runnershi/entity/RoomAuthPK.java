package com.ssafy.runnershi.entity;

import java.io.Serializable;
import javax.persistence.Column;

public class RoomAuthPK implements Serializable {
  @Column
  private String roomId;
  @Column
  private String authUserId;
}
