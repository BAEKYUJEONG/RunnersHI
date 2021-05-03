package com.ssafy.runnershi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.runnershi.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {
  public Friend findByUser_UserId_UserIdAndFriendUser_UserId_UserId(String user, String friendUser);
}
