package org.di.airbnb.exceptions.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 403
@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidReviewingException extends RuntimeException {

	private static final long serialVersionUID = 3400248338734389783L;

	public InvalidReviewingException() {
		super();
	}

	public InvalidReviewingException( String message ) {
		super( message );
	}
}
