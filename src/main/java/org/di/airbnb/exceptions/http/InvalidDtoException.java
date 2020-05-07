package org.di.airbnb.exceptions.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 422
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidDtoException extends RuntimeException {

  private static final long serialVersionUID = -8688941371357886061L;
  
  public InvalidDtoException(String message) {
    super(message);
  }

}
