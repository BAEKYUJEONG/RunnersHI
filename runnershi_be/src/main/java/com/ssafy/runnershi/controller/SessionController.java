package com.ssafy.runnershi.controller;

import com.ssafy.runnershi.entity.Room;
import com.ssafy.runnershi.entity.RoomMember;
import com.ssafy.runnershi.entity.User;
import com.ssafy.runnershi.repository.RoomMemberRepository;
import com.ssafy.runnershi.repository.RoomRepository;
import com.ssafy.runnershi.repository.UserRepository;
import com.ssafy.runnershi.service.JwtService;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.TokenOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-sessions")
public class SessionController {

  @Autowired
  JwtService jwtService;

  @Autowired
  RoomRepository roomRepository;

  @Autowired
  RoomMemberRepository roomMemberRepository;

  @Autowired
  UserRepository userRepository;

  // OpenVidu object as entrypoint of the SDK
  OpenVidu openVidu;


  private Map<Long, Session> roomIdSession = new ConcurrentHashMap<>();
  private Map<String, Map<String, String>> sessionIdUserIdToken = new ConcurrentHashMap<>();

  // URL where our OpenVidu server is listening
  private String OPENVIDU_URL;
  // Secret shared with our OpenVidu server
  private String SECRET;

  public SessionController(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
    this.SECRET = secret;
    this.OPENVIDU_URL = openviduUrl;
    this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
  }

  @PostMapping(value = "/create-session")
  public ResponseEntity<String> createSession(@RequestBody HashMap<String, String> map, HttpServletRequest req) {

    long roomId;
    String[] users;
    ArrayList<String> members;
    User user;

    // 토큰 검증
    String jwt = req.getHeader("token");
    //String userId = jwtService.decode(jwt);
    String userId = jwt;

    if (userId == null) {
      return new ResponseEntity<>("invalid token", HttpStatus.UNAUTHORIZED);
    }
    System.out.println("현재 사용자: " + userId);

    String title = map.get("title");
    int type = Integer.parseInt(map.get("type"));

    try {
      Session session = this.openVidu.createSession();
      Room room = new Room(title, type, 0, 1);

      room = roomRepository.save(room);
      roomId = room.getRoomId();

      // 방에 입장 제한이 있는 경우
      if (type == 1) {
        // 멤버 파싱
        users = map.get("members").split(",");
        members = new ArrayList<>(Arrays.asList(users));
        System.out.println("멤버 추가");
        System.out.println("members: " + members);

        for (String id : members) {
          roomMemberRepository.save(new RoomMember(room, userRepository.findByUserId(id)));
        }
        System.out.println("입장가능 멤버 추가 완료");
      }
      this.roomIdSession.put(roomId, session);
      this.sessionIdUserIdToken.put(session.getSessionId(), new HashMap<>());

      showMap();

      return new ResponseEntity<>(Long.toString(roomId), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("session created error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(value = "/generate-token")
  public ResponseEntity<String> generateToken(@RequestBody long roomId, HttpServletRequest req) {
    System.out.println("토큰 생성");

    // 토큰 검증
    String jwt = req.getHeader("token");
    //String userId = jwtService.decode(jwt);
    String userId = jwt;
    if (userId == null) {
      return new ResponseEntity<>("invalid token", HttpStatus.UNAUTHORIZED);
    }

    Optional<Room> r = roomRepository.findById(roomId);

    // 방이 없는지
    if (!r.isPresent()) {
      System.out.println("방 없음");
      return new ResponseEntity<>("session does not exist", HttpStatus.BAD_REQUEST);
    }

    // 방에 권한이 있는지
    RoomMember rm = roomMemberRepository.findByRoom_RoomIdAndUser_UserId(roomId, jwt);
    System.out.println(rm);
    if (rm == null) {
      return new ResponseEntity<>("Unauthorization", HttpStatus.BAD_REQUEST);
    }

    // 세션이 없는지
    if (this.roomIdSession.get(roomId) == null) {
      System.out.println("세션 없음");
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    String sessionId = roomIdSession.get(roomId).getSessionId();

    // 사용자가 이미 세션을 가지고 있는지
    if (sessionIdUserIdToken.get(sessionId).containsKey(userId)) {
      String token = sessionIdUserIdToken.get(sessionId).get(userId);
      showMap();
      return new ResponseEntity<>(token, HttpStatus.CONFLICT);
    }

    Session session = this.roomIdSession.get(roomId);
    //OpenViduRole role = user.hasRoleTeacher() ? OpenViduRole.PUBLISHER : OpenViduRole.SUBSCRIBER;

    TokenOptions tokenOpts = new TokenOptions.Builder()
        .data("SERVER=" + userId).build();
    try {
      String token = this.roomIdSession.get(roomId).generateToken(tokenOpts);

      this.sessionIdUserIdToken.get(session.getSessionId()).put(userId, token);

      showMap();

      return new ResponseEntity<>(token, HttpStatus.OK);
    } catch (OpenViduJavaClientException e1) {
      // If internal error generate an error message and return it to client
      return new ResponseEntity<>("OpenViduJavaClientException e1", HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (OpenViduHttpException e2) {
      if (404 == e2.getStatus()) {
        // Invalid sessionId (user left unexpectedly). Session object is not valid
        // anymore. Must clean invalid session and create a new one
        try {
          this.sessionIdUserIdToken.remove(session.getSessionId());
          session = this.openVidu.createSession();
          this.roomIdSession.put(roomId, session);
          this.sessionIdUserIdToken.put(session.getSessionId(), new HashMap<>());
          String token = session.generateToken(tokenOpts);
          // END IMPORTANT STUFF

          this.sessionIdUserIdToken.get(session.getSessionId()).put(userId, token);
          //responseJson.put(0, token);

          showMap();

          return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e3) {
          return new ResponseEntity<>("OpenViduJavaClientException | OpenViduHttpException e3", HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } else {
        return new ResponseEntity<>("OpenViduHttpException e2", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  @PostMapping(value = "/leave-session")
  public ResponseEntity<String> removeUser(@RequestBody long roomId, HttpServletRequest req) {

    // 토큰 검증
    String jwt = req.getHeader("token");
    //String userId = jwtService.decode(jwt);
    String userId = jwt;
    if (userId == null) {
      return new ResponseEntity<>("invalid token", HttpStatus.UNAUTHORIZED);
    }

    System.out.println("Removing user | user=" + userId + " | roomId=" + roomId);

    // 방이 없으면
    if (this.roomIdSession.get(roomId) == null) {
      return new ResponseEntity<>("room does not exist", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    String sessionId = this.roomIdSession.get(roomId).getSessionId();
    Map<String, String> RoomIdUserIdsessions = this.sessionIdUserIdToken.get(sessionId);

    // 세션에 사용자가 없으면
    if (!RoomIdUserIdsessions.containsKey(userId)) {
      return new ResponseEntity<>("there is no user in room", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 유저 접속 제거
    this.sessionIdUserIdToken.get(sessionId).remove(userId);
    // 방이 비었으면 방 삭제
    if (this.sessionIdUserIdToken.get(sessionId).isEmpty()) {
      this.roomIdSession.remove(roomId);
      this.sessionIdUserIdToken.remove(sessionId);
      roomRepository.deleteById(roomId);
    }

    showMap();

    return new ResponseEntity<>("success leave-session", HttpStatus.OK);
  }

  @GetMapping(value = "/active-session")
  public ResponseEntity<JSONObject> getActiveSession(HttpServletRequest req) {
    JSONObject json = new JSONObject();
    json.put(".?", "ssss");
    json.put("list", this.openVidu.getActiveSessions());

    List<Session> s = this.openVidu.getActiveSessions();
    System.out.println(s);

    return new ResponseEntity<>(json, HttpStatus.OK);
  }

  private void showMap() {
    System.out.println("------------------------------");
    System.out.println(this.roomIdSession.toString());
    System.out.println(this.sessionIdUserIdToken.toString());
    System.out.println("------------------------------");
  }

}
