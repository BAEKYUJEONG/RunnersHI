package com.ssafy.runnershi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.runnershi.entity.Profile2;
import com.ssafy.runnershi.entity.UserInfo;

public interface ProfileRepository extends JpaRepository<UserInfo, Long> {
  public Profile2 findByUserId_UserId(String userId);
}
