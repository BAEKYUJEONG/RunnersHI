package com.ssafy.runnershi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ssafy.runnershi.entity.Custom;

public interface CustomRepository extends JpaRepository<Custom, Integer> {

  public Custom findByUser_UserId_UserId(String userId);

}
