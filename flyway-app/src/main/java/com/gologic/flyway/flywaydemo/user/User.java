package com.gologic.flyway.flywaydemo.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
  private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
  private String name;
  private String country;
  private Long age;

}
