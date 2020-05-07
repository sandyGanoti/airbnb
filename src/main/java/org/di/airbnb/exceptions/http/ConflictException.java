package org.di.airbnb.exceptions.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 409
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

  private static final long serialVersionUID = 3400248338734389783L;

  public ConflictException(String message) {
    super(message);
  }
}
