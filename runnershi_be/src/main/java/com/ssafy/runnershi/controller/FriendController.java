package com.ssafy.runnershi.controller;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.runnershi.entity.FriendListResult;
import com.ssafy.runnershi.service.FriendService;
import com.ssafy.runnershi.service.JwtService;

@RestController
@RequestMapping("/friend")
public class FriendController {

  @Autowired
  FriendService friendService;

  @Autowired
  JwtService jwtService;

  @PostMapping("/add")
  public ResponseEntity<String> addFriend(@RequestBody HashMap<String, String> map,
      HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<String>("invalid token", HttpStatus.OK);
    }
    String friendName = map.get("friendUserName");
    String result = friendService.addFriend(userId, friendName);
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }

  @PostMapping("/accept")
  public ResponseEntity<String> acceptFriend(@RequestBody HashMap<String, Object> map,
      HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<String>("invalid token", HttpStatus.OK);
    }
    String friendUserId = (String) map.get("friendUserId");
    Long alarmId = Long.parseLong(String.valueOf(map.get("alarmId")));
    String result = friendService.acceptFriend(userId, friendUserId, alarmId);
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }

  @PostMapping("/reject")
  public ResponseEntity<String> rejectFriend(@RequestBody HashMap<String, Object> map,
      HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<String>("invalid token", HttpStatus.OK);
    }
    Integer alarmId = (Integer) map.get("alarmId");
    String result = friendService.rejectFriend(userId, alarmId);
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }

  @DeleteMapping("/delete")
  public ResponseEntity<String> deleteFriend(@RequestBody HashMap<String, String> map,
      HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<String>("invalid token", HttpStatus.OK);
    }
    String friendUserId = map.get("friendUserId");
    String result = friendService.deleteFriend(userId, friendUserId);
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }

  @GetMapping("/list")
  public ResponseEntity<FriendListResult> friendList(HttpServletRequest req) {
    FriendListResult result = null;
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<FriendListResult>(result, HttpStatus.OK);
    }
    result = friendService.friendList(userId);
    return new ResponseEntity<FriendListResult>(result, HttpStatus.OK);
  }

}
