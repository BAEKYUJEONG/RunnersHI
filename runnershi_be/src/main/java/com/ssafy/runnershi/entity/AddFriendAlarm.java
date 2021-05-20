package com.ssafy.runnershi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddFriendAlarm {
  private long alarmId;
  private String fromUserId;
  private String fromUserName;
  private String content;
}
