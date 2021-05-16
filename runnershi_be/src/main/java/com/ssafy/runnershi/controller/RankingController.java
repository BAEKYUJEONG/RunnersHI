package com.ssafy.runnershi.controller;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.runnershi.service.JwtService;
import com.ssafy.runnershi.service.RankingService;

@RestController
@RequestMapping("/ranking")
public class RankingController {

  @Autowired
  private RankingService rankingService;

  @Autowired
  private JwtService jwtService;

  @PostMapping("/total/all")
  public ResponseEntity<Object> totalAll(@RequestBody HashMap<String, Object> map,
      HttpServletRequest req) {
    String type = (String) map.get("type");
    int offset = (Integer) map.get("offset");
    Object result = rankingService.totalAll(type, offset);
    if (result == null)
      return new ResponseEntity<Object>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }

  @PostMapping("/weekly/all")
  public ResponseEntity<Object> weeklyAll(@RequestBody HashMap<String, Object> map,
      HttpServletRequest req) {
    String type = (String) map.get("type");
    int offset = (Integer) map.get("offset");
    Object result = rankingService.weeklyAll(type, offset);
    if (result == null)
      return new ResponseEntity<Object>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }

  @PostMapping("/total/friend")
  public ResponseEntity<Object> totalFriend(@RequestBody HashMap<String, Object> map,
      HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<Object>(null, HttpStatus.OK);
    }
    String type = (String) map.get("type");
    int offset = (Integer) map.get("offset");

    Object result = rankingService.totalFriend(userId, type, offset);
    if (result == null)
      return new ResponseEntity<Object>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }

  @PostMapping("/weekly/friend")
  public ResponseEntity<Object> weeklyFriend(@RequestBody HashMap<String, Object> map,
      HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<Object>(null, HttpStatus.OK);
    }
    String type = (String) map.get("type");
    int offset = (Integer) map.get("offset");

    Object result = rankingService.weeklyFriend(userId, type, offset);
    if (result == null)
      return new ResponseEntity<Object>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<Object>(result, HttpStatus.OK);
  }

}
