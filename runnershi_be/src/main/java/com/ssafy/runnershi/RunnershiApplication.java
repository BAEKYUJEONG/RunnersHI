package com.ssafy.runnershi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RunnershiApplication {

  public static void main(String[] args) {
    SpringApplication.run(RunnershiApplication.class, args);
  }

}
