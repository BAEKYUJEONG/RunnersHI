package com.ssafy.runnershi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.runnershi.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

  public User findByUserId(String userId);

  public User findByUserName(String userName);

}
