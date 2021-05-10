package com.ssafy.runnershi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
public class RankingServiceImpl implements RankingService {

  @Autowired
  private RedisTemplate<String, String> template;

  @Autowired
  private ZSetOperations<String, String> zset;

  @Override
  public void reEnrollRanks() {
    List<HashMap<String, Object>> ranks = new ArrayList<>();

  }

}
