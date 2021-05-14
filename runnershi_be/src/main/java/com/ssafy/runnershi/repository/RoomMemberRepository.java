package com.ssafy.runnershi.repository;

import com.ssafy.runnershi.entity.RoomMember;
import com.ssafy.runnershi.entity.User;
import com.ssafy.runnershi.entity.Room;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {



}
