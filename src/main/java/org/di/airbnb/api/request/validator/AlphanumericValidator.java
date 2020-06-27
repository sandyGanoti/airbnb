package org.di.airbnb.api.request.validator;

import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.springframework.stereotype.Component;

@Singleton
@Component
public class AlphanumericValidator {
	private static final Pattern IS_ALPHANUMERIC = Pattern.compile( "^.*[^a-zA-Z0-9 ].*$" );

	protected boolean validate( final String name ) {
		return IS_ALPHANUMERIC.matcher( name ).matches();
	}

}
