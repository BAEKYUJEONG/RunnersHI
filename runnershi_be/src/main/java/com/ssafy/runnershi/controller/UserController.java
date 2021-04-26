package com.ssafy.runnershi.controller;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.runnershi.entity.User;
import com.ssafy.runnershi.entity.UserResult;
import com.ssafy.runnershi.service.JwtService;
import com.ssafy.runnershi.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  UserService userService;

  @Autowired
  JwtService jwtService;

  @PostMapping("/signin/kakao")
  public ResponseEntity<UserResult> signin(@RequestBody HashMap<String, String> map,
      HttpServletRequest req) {
    // System.out.println(code);
    String accessToken = map.get("accessToken");
    UserResult result = userService.signin(accessToken);
    if (result == null)
      return new ResponseEntity<UserResult>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<UserResult>(result, HttpStatus.OK);
  }

  @PostMapping("/signup/social")
  public ResponseEntity<UserResult> enterName(User user, HttpServletRequest req) {
    UserResult result = userService.enterName(user);
    if (result == null)
      return new ResponseEntity<UserResult>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<UserResult>(result, HttpStatus.OK);
  }

  @PostMapping("/runningtype")
  public ResponseEntity<UserResult> enterRunningType(UserResult user, HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null || !userId.equals(user.getUserId()))
      return new ResponseEntity<UserResult>((UserResult) null, HttpStatus.BAD_REQUEST);

    UserResult result = userService.enterRunningType(user);
    if (result == null)
      return new ResponseEntity<UserResult>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<UserResult>(result, HttpStatus.OK);
  }

  @GetMapping("/namechk/{name}")
  public ResponseEntity<String> nameChk(@PathVariable String name, HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    String result = "invalid";
    if (userId == null)
      return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);

    result = userService.nameChk(name);
    if (!result.equals("valid"))
      return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }
}
