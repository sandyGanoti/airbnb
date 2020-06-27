package org.di.airbnb.api.request.validator;

import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.springframework.stereotype.Component;

@Singleton
@Component
public class AlphaValidator {
	private static final Pattern IS_ALPHA = Pattern.compile( "[a-zA-Z]+" );

	protected boolean validate( final String name ) {
		return IS_ALPHA.matcher( name ).matches();
	}

}
