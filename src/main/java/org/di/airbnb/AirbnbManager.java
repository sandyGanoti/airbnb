package org.di.airbnb;

import static org.di.airbnb.dao.UniqueConstraintHelper.causedByUniqueConstraint;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.di.airbnb.api.request.UserCreationRequest;
import org.di.airbnb.api.request.UserUpdateRequest;
import org.di.airbnb.assemblers.UserSubModel;
import org.di.airbnb.assemblers.property.PropertyModel;
import org.di.airbnb.assemblers.rating.RatingModel;
import org.di.airbnb.assemblers.user.UserModel;
import org.di.airbnb.constant.Role;
import org.di.airbnb.dao.AirbnbDaoImpl;
import org.di.airbnb.dao.entities.User;
import org.di.airbnb.dao.repository.UserRepository;
import org.di.airbnb.exceptions.api.UniqueConstraintViolationException;
import org.di.airbnb.exceptions.api.EntityNotFoundException;
import org.di.airbnb.exceptions.api.UserNotFoundException;
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
		UserModel userModel = null;
		User user = modelMapper.map( userCreationRequest, User.class );
		user.setCreatedAt( Instant.now() );
		user.setRole( userCreationRequest.isHost() ? Role.TENANT_AND_HOST : Role.TENANT );
		try {
			userModel = modelMapper.map( userRepository.save( user ), UserModel.class );
		} catch ( RuntimeException e ) {
			handleException( e );
		}
		return userModel;
	}

	public UserModel updateUser( final @NotNull UserUpdateRequest userUpdateRequest,
			final long userId ) {
		UserModel userModel = null;
		Optional<User> userIsPresent = userRepository.findById( userId );
		if ( userIsPresent.isPresent() ) {
			User user = userIsPresent.get();
			if ( userUpdateRequest.getEmail() != null ) {
				user.setEmail( userUpdateRequest.getEmail() );
			}
			if ( userUpdateRequest.getFirstName() != null ) {
				user.setFirstName( userUpdateRequest.getFirstName() );
			}
			if ( userUpdateRequest.getLastName() != null ) {
				user.setLastName( userUpdateRequest.getLastName() );
			}
			if ( userUpdateRequest.getUsername() != null ) {
				user.setUsername( userUpdateRequest.getUsername() );
			}
			if ( userUpdateRequest.getEmail() != null ) {
				user.setEmail( userUpdateRequest.getEmail() );
			}
			if ( userUpdateRequest.getPassword() != null ) {
				user.setPassword( userUpdateRequest.getPassword() );
			}
			if ( userUpdateRequest.getPhoneNumber() != null ) {
				user.setPhoneNumber( userUpdateRequest.getPhoneNumber() );
			}
			try {
				userModel = modelMapper.map( userRepository.save( user ), UserModel.class );
			} catch ( RuntimeException e ) {
				handleException( e );
			}
		} else {
			throw new UserNotFoundException( "User do not exist" );
		}
		return userModel;
	}

	private void handleException( final RuntimeException e ) {
		if ( causedByUniqueConstraint( e, "email" ) ) {
			throw new UniqueConstraintViolationException( "Email already in use" );
		} else if ( causedByUniqueConstraint( e, "username" ) ) {
			throw new UniqueConstraintViolationException( "Username already exists" );
		} else {
			throw e;
		}
	}

	public boolean isValidUser( final long userId ) {
		return userRepository.findById( userId ).isPresent();
	}

	public List<PropertyModel> getPropertiesByHost( final long hostId ) {
		return modelMapper.map( airbnbDao.getPropertiesByHost( hostId ), List.class );
	}

	public List<PropertyModel> getUserBookings( final long hostId ) {
		return modelMapper.map( airbnbDao.getTenantBookings( hostId ), List.class );
	}

	public UserModel getUserInfo( long userId ) {
		try {
			return modelMapper.map( userRepository.getOne( userId ), UserModel.class );
		} catch ( EntityNotFoundException e ) {
			return null;
		}
	}

	public List<RatingModel> getPropertyRatings( long propertyId ) {
		return modelMapper.map( airbnbDao.getPropertyRatings( propertyId ), List.class );
	}

}
