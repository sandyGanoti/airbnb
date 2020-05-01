package org.di.airbnb;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.di.airbnb.assemblers.UserLimitedDTO;
import org.di.airbnb.dao.AirbnbDao;
import org.di.airbnb.dao.repository.UserRepository;
import org.di.airbnb.exceptions.http.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

@Singleton
public class AirbnbManager {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AirbnbDao airbnbDao;

	//TODO: Use assembler here in order to return data from this layer to the above

	public UserLimitedDTO login( final @NotNull String username, final @NotNull String password ) {

		String encoded = null;
		try {
			MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
			byte[] hash = digest.digest( password.getBytes( StandardCharsets.UTF_8 ) );
			encoded = Base64.getEncoder().encodeToString( hash );
		} catch ( NoSuchAlgorithmException e ) {
		}

		Optional<UserLimitedDTO> user = airbnbDao.login( username, encoded );
		if ( !user.isPresent() ) {
			throw new EntityNotFoundException( "No user found with the provided id" );
		}
		return null;

	}

}
