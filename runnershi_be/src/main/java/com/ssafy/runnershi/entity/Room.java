package com.ssafy.runnershi.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Room {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long roomId;
  private String Title;
  private Integer roomType;
  private Integer runningType;
  private String session;
  @OneToMany(mappedBy = "room")
  private Set<RoomMember> roomMember = new HashSet<>();

  /*@Builder
  public Room(long roomId, String Title, int roomType, int runningType, int count) {
    this.roomId = roomId;
    this.roomType = roomType;
    this.Title = Title;
    this.runningType = runningType;
    this.count = count;
  }*/

  public Room(String Title, int roomType, int runningType, String session) {
    this.roomType = roomType;
    this.Title = Title;
    this.runningType = runningType;
    this.session = session;
  }
}
