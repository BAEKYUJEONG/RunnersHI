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
import com.ssafy.runnershi.service.FCMService;
import com.ssafy.runnershi.service.JwtService;

@RestController
@RequestMapping("/fcm")
public class FCMController {
  @Autowired
  JwtService jwtService;

  @Autowired
  FCMService fcmService;

  @PostMapping("/token")
  public ResponseEntity<String> addToken(@RequestBody HashMap<String, String> map,
      HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<String>("invalid token", HttpStatus.OK);
    }
    String msgToken = map.get("msgToken");
    String result = fcmService.addToken(userId, msgToken);
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }
}
