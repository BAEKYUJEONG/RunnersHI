package com.ssafy.runnershi.repository;

import java.util.ArrayList;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.runnershi.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

  public User findByUserId(String userId);

  public User findByUserName(String userName);

  public User findByEmail(String email);

  public ArrayList<User> findByExpiryDateLessThanEqual(Date expiryDate);

}
