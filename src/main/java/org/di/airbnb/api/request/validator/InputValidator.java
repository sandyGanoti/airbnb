package org.di.airbnb.api.request.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.inject.Singleton;

@Singleton
@Component
public class InputValidator {

	@Autowired
	private EmailValidator emailValidator;

	@Autowired
	private AlphanumericValidator alphanumericValidator;

	@Autowired AlphaValidator alphaValidator;

	public boolean validateUsername( final String username ) {
		return username.length() > 5 && alphanumericValidator.validate( username );
	}

	public boolean validateName( final String name ) {
		return name.length() > 5 && alphaValidator.validate( name );
	}

	public boolean validateEmail( final String email ) {
		return emailValidator.validate( email );
	}

}
