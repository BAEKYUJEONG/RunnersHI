package com.ssafy.runnershi.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements Serializable {
  @Id
  private String userId;

  private String userName;

  @Column(nullable = true)
  private String email;

  @Column(nullable = true)
  private String pwd;

  @Column(nullable = true)
  private String signUpPath;

  private Byte hasCustom;
  private Byte flag;

  @Column(nullable = true)
  @Temporal(TemporalType.TIMESTAMP)
  private Date expiryDate;
}
