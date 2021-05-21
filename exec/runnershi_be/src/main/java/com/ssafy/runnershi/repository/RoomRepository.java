package com.ssafy.runnershi.repository;


import com.ssafy.runnershi.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RoomRepository extends JpaRepository<Room, Long> {

  @Transactional
  public void deleteByRoomId(Long roomId);
}
