package com.ssafy.runnershi.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RoomMember {

  @Id @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "ROOM_ID")
  private Room room;

  @ManyToOne
  @JoinColumn(name = "USER_NAME")
  private User user;

  public RoomMember(Room room, User user) {
    this.room = room;
    this.user = user;
  }
}
