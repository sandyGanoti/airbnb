package org.di.airbnb.exceptions.api;

public class UniqueConstraintViolationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UniqueConstraintViolationException( String message ) {
		super( message );
	}
}
