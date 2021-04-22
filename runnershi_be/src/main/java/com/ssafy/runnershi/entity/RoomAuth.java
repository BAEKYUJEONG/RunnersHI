package com.ssafy.runnershi.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
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
  private String roomId;
  @Id
  private String authUserId;
}
