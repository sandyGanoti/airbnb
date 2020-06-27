package org.di.airbnb.api.request.validator;

import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.springframework.stereotype.Component;

@Singleton
@Component
public class EmailValidator {
	private static final Pattern EMAIL_PATTERN = Pattern.compile( "[^@]+@[^@]+" );

	protected boolean validate( final String emailAddress ) {
		return EMAIL_PATTERN.matcher( emailAddress ).matches();
	}
}
