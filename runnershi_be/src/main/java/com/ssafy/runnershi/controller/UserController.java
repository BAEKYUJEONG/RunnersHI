package com.ssafy.runnershi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.runnershi.entity.Profile;
import com.ssafy.runnershi.entity.SearchResult;
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
  public ResponseEntity<UserResult> signInKakao(@RequestBody HashMap<String, String> map,
      HttpServletRequest req) {
    String accessToken = map.get("accessToken");
    UserResult result = userService.signInKakao(accessToken);
    if (result == null)
      return new ResponseEntity<UserResult>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<UserResult>(result, HttpStatus.OK);
  }

  @PostMapping("/signin/naver")
  public ResponseEntity<UserResult> signInNaver(@RequestBody HashMap<String, String> map,
      HttpServletRequest req) {
    String accessToken = map.get("accessToken");
    UserResult result = userService.signInNaver(accessToken);
    if (result == null)
      return new ResponseEntity<UserResult>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<UserResult>(result, HttpStatus.OK);
  }


  @PostMapping("/signup/runhi")
  public ResponseEntity<UserResult> signUpRunHi(User user, HttpServletRequest req) {
    UserResult result = userService.signUpRunHi(user);
    if (result == null)
      return new ResponseEntity<UserResult>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<UserResult>(result, HttpStatus.OK);
  }


  @PostMapping("/signin/runhi")
  public ResponseEntity<UserResult> signInRunHi(User user, HttpServletRequest req) {
    UserResult result = userService.signInRunHi(user);
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
    UserResult result = null;
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);

    if (userId == null || !userId.equals(user.getUserId())) {
      result = new UserResult();
      result.setResult("invalid token");
      return new ResponseEntity<UserResult>(result, HttpStatus.OK);
    }

    result = userService.enterRunningType(user);
    if (result == null)
      return new ResponseEntity<UserResult>(result, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<UserResult>(result, HttpStatus.OK);
  }

  @GetMapping("/namechk/{name}")
  public ResponseEntity<Map> nameChk(@PathVariable String name, HttpServletRequest req) {
    Map result = userService.nameChk(name);
    return new ResponseEntity<Map>(result, HttpStatus.OK);
  }

  @GetMapping("/emailchk/{email}")
  public ResponseEntity<Map> emailChk(@PathVariable String email, HttpServletRequest req) {
    Map result = userService.emailChk(email);
    return new ResponseEntity<Map>(result, HttpStatus.OK);
  }

  @PostMapping("/pwdchk")
  public ResponseEntity<String> pwdChk(User user, HttpServletRequest req) {
    String result = null;
    if (user == null || user.getEmail() == null || user.getPwd() == null) {
      return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
    }
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null || !userId.equals(user.getUserId())) {
      return new ResponseEntity<String>("invalid token", HttpStatus.OK);
    }

    result = userService.pwdChk(user);
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }

  @DeleteMapping("/leave")
  public ResponseEntity<String> leaveRunHi(User deleteUser, HttpServletRequest req) {
    String result = null;
    if (deleteUser == null || deleteUser.getUserId() == null) {
      return new ResponseEntity<String>(result, HttpStatus.BAD_REQUEST);
    }
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null || !userId.equals(deleteUser.getUserId())) {
      return new ResponseEntity<String>("invalid Token", HttpStatus.OK);
    }

    result = userService.leave(userId);
    return new ResponseEntity<String>(result, HttpStatus.OK);
  }

  @GetMapping("/search/{word}")
  public ResponseEntity<List<SearchResult>> searchUser(@PathVariable String word,
      HttpServletRequest req) {
    List<SearchResult> result = null;
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<List<SearchResult>>(result, HttpStatus.OK);
    }
    result = userService.searchUser(userId, word);
    return new ResponseEntity<List<SearchResult>>(result, HttpStatus.OK);
  }

  @GetMapping("/profile")
  public ResponseEntity<Profile> getUserProfile(HttpServletRequest req) {
    Profile result = null;
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<Profile>(result, HttpStatus.OK);
    }
    result = userService.getUserProfile(userId);
    return new ResponseEntity<Profile>(result, HttpStatus.OK);
  }

}
