package com.ssafy.runnershi.repository;

import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ssafy.runnershi.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {
  public Friend findByUser_UserId_UserIdAndFriendUser_UserId_UserId(String user, String friendUser);

  public ArrayList<Friend> findByUser_UserId_UserId(String userId);

  @Query(value = "SELECT friend_user_id as userId FROM friend WHERE user_id = :userId",
      nativeQuery = true)
  public ArrayList<String> getFriendsUserId(@Param("userId") String userId);
}
