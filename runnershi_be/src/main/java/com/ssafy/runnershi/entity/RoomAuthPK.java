package com.ssafy.runnershi.entity;

import java.io.Serializable;
import javax.persistence.Column;

public class RoomAuthPK implements Serializable {
  @Column
  private String room;
  @Column
  private String authUser;
}
