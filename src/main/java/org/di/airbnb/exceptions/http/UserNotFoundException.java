package org.di.airbnb.exceptions.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 404
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 3400248338734389783L;

  public UserNotFoundException(String message) {
    super(message);
  }
}
