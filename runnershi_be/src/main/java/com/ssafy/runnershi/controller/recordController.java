package com.ssafy.runnershi.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.runnershi.entity.Record;
import com.ssafy.runnershi.entity.RecordResult;
import com.ssafy.runnershi.service.JwtService;
import com.ssafy.runnershi.service.RecordService;

@RestController
@RequestMapping("/record")
public class recordController {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private RecordService recordService;

  @DeleteMapping("/delete/{recordId}")
  public ResponseEntity<String> deleteRecord(@PathVariable long recordId, HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String userId = jwtService.decode(jwt);
    if (userId == null) {
      return new ResponseEntity<String>("invalid token", HttpStatus.OK);
    }
    return new ResponseEntity<String>(recordService.deleteRecord(userId, recordId), HttpStatus.OK);
  }

  @GetMapping("/list/{userId}")
  public ResponseEntity<List<RecordResult>> recordList(@PathVariable String userId,
      HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String tokenUserId = jwtService.decode(jwt);
    if (tokenUserId == null) {
      return new ResponseEntity<List<RecordResult>>((List<RecordResult>) null, HttpStatus.OK);
    }
    return new ResponseEntity<List<RecordResult>>(recordService.recordList(userId), HttpStatus.OK);
  }

  @GetMapping("/detail/{recordId}")
  public ResponseEntity<Record> recordDetail(@PathVariable long recordId, HttpServletRequest req) {
    String jwt = req.getHeader("token");
    String tokenUserId = jwtService.decode(jwt);
    if (tokenUserId == null) {
      return new ResponseEntity<Record>((Record) null, HttpStatus.OK);
    }
    return new ResponseEntity<Record>(recordService.recordDetail(recordId), HttpStatus.OK);
  }

}
