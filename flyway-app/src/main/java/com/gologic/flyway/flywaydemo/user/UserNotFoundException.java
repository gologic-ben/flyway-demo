package com.gologic.flyway.flywaydemo.user;

class UserNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  UserNotFoundException(Long id) {
    super("Could not find user " + id);
  }
}