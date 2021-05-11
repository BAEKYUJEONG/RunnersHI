package com.ssafy.runnershi.repository;

import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.runnershi.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {
  public Friend findByUser_UserId_UserIdAndFriendUser_UserId_UserId(String user, String friendUser);

  public ArrayList<Friend> findByUser_UserId_UserId(String userId);
}
