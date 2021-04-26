package com.ssafy.runnershi.service;

import static com.auth0.jwt.JWT.require;
import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtService {

  // encode
  @Value("${JWT.ISSUER}")
  private String ISSUER;

  @Value("${JWT.SECRET}")
  private String SECRET;

  /**
   * 토큰 생성
   *
   * @param userSeq 토큰에 담길 로그인한 사용자의 회원 고유 IDX
   * @return 토큰
   */

  // encode
  public String create(String userSeq) {
    try {
      JWTCreator.Builder b = JWT.create();
      // 토큰 발급자
      b.withIssuer(ISSUER);
      // 토큰 payload 작성, key - value 형식, 객체도 가능
      b.withClaim("userSeq", userSeq);
      // 토큰 만료날짜 지정
      b.withExpiresAt(expiresAt());
      return b.sign(Algorithm.HMAC256(SECRET));
    } catch (JWTCreationException jwtCreationException) {
      log.info(jwtCreationException.getLocalizedMessage());
    }
    return null;
  }

  // encode
  private Date expiresAt() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    // 한달 24 * 31
    cal.add(Calendar.HOUR, 744);
    return cal.getTime();
  }

  // encode
  public static class TokenRes {
    private String token;

    public TokenRes() {

    }

    public TokenRes(String token) {
      this.token = token;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }
  }

  // decode
  public String decode(String token) {
    try {
      // 토큰 해독 객체 생성
      final JWTVerifier jwtVerifier = require(Algorithm.HMAC256(SECRET)).withIssuer(ISSUER).build();
      // 토큰 검증
      DecodedJWT decodedJWT = jwtVerifier.verify(token);
      // 토큰 payload 반환, 정상적인 토큰이라면 토큰 사용자 고유 ID, 아니라면 -1
      return decodedJWT.getClaim("userSeq").asString();
    } catch (JWTVerificationException jve) {
      log.error(jve.getMessage());
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return null;
  }
}
