package com.ssafy.runnershi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResult {
  private String result;
  private String token;
  private String userId;
  private String userName;
  private Byte RunningType;
}
