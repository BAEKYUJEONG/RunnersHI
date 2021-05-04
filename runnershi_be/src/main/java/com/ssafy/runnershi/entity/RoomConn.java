package com.ssafy.runnershi.entity;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RoomConn {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long roomConnId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id")
  private Room room;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conn_user_id", referencedColumnName = "user_id")
  private UserInfo connUser;
}
