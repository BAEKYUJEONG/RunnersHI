package com.ssafy.runnershi.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.runnershi.entity.AddFriendAlarm;
import com.ssafy.runnershi.service.AlarmService;
import com.ssafy.runnershi.service.JwtService;

@RestController
@RequestMapping("/alarm")
public class AlarmController {

  @Autowired
  JwtService jwtService;

  @Autowired
  AlarmService alarmService;

  @GetMapping("/addfriendlist")
  public ResponseEntity<List<AddFriendAlarm>> getAddFriendList(HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<List<AddFriendAlarm>>((List<AddFriendAlarm>) null, HttpStatus.OK);
    }
    List<AddFriendAlarm> result = alarmService.getAddFriendList(userId);
    return new ResponseEntity<List<AddFriendAlarm>>(result, HttpStatus.OK);
  }
}
