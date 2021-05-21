package com.ssafy.runnershi.entity;

import java.io.Serializable;
import javax.persistence.Column;
import lombok.Data;

@Data
public class RoomConnPK implements Serializable {
  @Column
  private String room;
  @Column
  private String authUser;
}
