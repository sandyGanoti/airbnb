package org.di.airbnb.exceptions.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 401
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedActionException extends RuntimeException {

  private static final long serialVersionUID = 3400248338734389783L;

  public UnauthorizedActionException(String message) {
    super(message);
  }
}
