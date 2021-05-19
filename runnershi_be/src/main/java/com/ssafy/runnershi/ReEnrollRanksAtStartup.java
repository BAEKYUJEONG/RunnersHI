package com.ssafy.runnershi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.ssafy.runnershi.service.RankingService;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ReEnrollRanksAtStartup implements CommandLineRunner {

  private final RankingService rankingService;

  @Override
  public void run(String... args) throws Exception {
    // TODO Auto-generated method stub
    // rankingService.reEnrollRanks();
  }

}
