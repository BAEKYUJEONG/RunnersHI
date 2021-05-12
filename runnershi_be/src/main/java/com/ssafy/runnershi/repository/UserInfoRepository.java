package com.ssafy.runnershi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.runnershi.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

  public UserInfo findByUserId_UserId(String userId);

  public UserInfo findByUserName_UserName(String userName);

}
