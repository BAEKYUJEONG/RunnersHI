package com.ssafy.runnershi.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@IdClass(RoomAuthPK.class)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RoomAuth {
  @Id
  @ManyToOne
  @JoinColumn(name = "room_id")
  private Room room;

  @Id
  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserInfo authUser;

}
