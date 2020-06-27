package org.di.airbnb.exceptions.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotValidInputException extends RuntimeException {

	private static final long serialVersionUID = 3400248338734389783L;

	public NotValidInputException() {
		super("Not valid user input");
	}
}
