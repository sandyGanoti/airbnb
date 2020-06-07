package org.di.airbnb.exceptions.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserNotValidException extends RuntimeException {

	private static final long serialVersionUID = 3400248338734389783L;

	public UserNotValidException( String message ) {
		super( message );
	}
}
