package org.di.airbnb;

import java.time.Instant;
import java.util.Optional;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.di.airbnb.api.request.UserCreationRequest;
import org.di.airbnb.assemblers.UserSubModel;
import org.di.airbnb.assemblers.user.UserModel;
import org.di.airbnb.dao.AirbnbDaoImpl;
import org.di.airbnb.dao.entities.User;
import org.di.airbnb.dao.repository.UserRepository;
import org.di.airbnb.exceptions.http.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Singleton
@Service
public class AirbnbManager {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AirbnbDaoImpl airbnbDao;

	@Autowired
	private ModelMapper modelMapper;

	//TODO: Use assembler here in order to return data from this layer to the above

	public UserSubModel login( final @NotNull String username, final @NotNull String password ) {
//		String encoded = null;
//		try {
//			MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
//			byte[] hash = digest.digest( password.getBytes( StandardCharsets.UTF_8 ) );
//			encoded = Base64.getEncoder().encodeToString( hash );
//		} catch ( NoSuchAlgorithmException e ) {
//			return null;
//		}
//
//		Optional<UserSubModel> user = airbnbDao.login( username, encoded );
		Optional<UserSubModel> user = airbnbDao.login( username, password );
		if ( !user.isPresent() ) {
			throw new EntityNotFoundException( "No user found with the provided details." );
		}
		return user.get();
	}

	public UserModel createUser( final @NotNull UserCreationRequest userCreationRequest ) {
		User user = modelMapper.map( userCreationRequest, User.class );
		user.setCreatedAt( Instant.now(  ) );

		return modelMapper.map(
				userRepository.save( user ),
				UserModel.class );
	}

}
