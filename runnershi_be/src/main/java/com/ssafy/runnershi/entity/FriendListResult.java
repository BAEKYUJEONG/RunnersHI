package com.ssafy.runnershi.entity;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendListResult {
  private int friendNum;
  private ArrayList<FriendIdName> friendList;
}
