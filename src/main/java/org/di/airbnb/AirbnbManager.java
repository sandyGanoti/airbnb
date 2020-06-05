package org.di.airbnb;

import static org.di.airbnb.dao.UniqueConstraintHelper.causedByUniqueConstraint;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.di.airbnb.api.request.UserCreationRequest;
import org.di.airbnb.api.request.UserUpdateRequest;
import org.di.airbnb.assemblers.property.PropertyModel;
import org.di.airbnb.assemblers.rating.RatingModel;
import org.di.airbnb.assemblers.user.UserModel;
import org.di.airbnb.constant.Role;
import org.di.airbnb.dao.AirbnbDaoImpl;
import org.di.airbnb.dao.entities.User;
import org.di.airbnb.dao.repository.UserRepository;
import org.di.airbnb.exceptions.api.EntityNotFoundException;
import org.di.airbnb.exceptions.api.UniqueConstraintViolationException;
import org.di.airbnb.exceptions.api.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

@Singleton
@Service
public class AirbnbManager {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AirbnbDaoImpl airbnbDao;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserModel createUser( final @NotNull UserCreationRequest userCreationRequest ) {
		UserModel userModel = null;
		User user = modelMapper.map( userCreationRequest, User.class );
		user.setCreatedAt( Instant.now() );
		user.setRole( userCreationRequest.isHost() ? Role.TENANT_AND_HOST : Role.TENANT );
		user.setPassword( bCryptPasswordEncoder.encode( userCreationRequest.getPassword() ) );
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
			if ( !Strings.isNullOrEmpty( userUpdateRequest.getEmail() ) ) {
				user.setEmail( userUpdateRequest.getEmail() );
			}
			if ( !Strings.isNullOrEmpty( userUpdateRequest.getFirstName() ) ) {
				user.setFirstName( userUpdateRequest.getFirstName() );
			}
			if ( !Strings.isNullOrEmpty( userUpdateRequest.getLastName() ) ) {
				user.setLastName( userUpdateRequest.getLastName() );
			}
			if ( !Strings.isNullOrEmpty( userUpdateRequest.getUsername() ) ) {
				user.setUsername( userUpdateRequest.getUsername() );
			}
			if ( !Strings.isNullOrEmpty( userUpdateRequest.getEmail() ) ) {
				user.setEmail( userUpdateRequest.getEmail() );
			}
			if ( !Strings.isNullOrEmpty( userUpdateRequest.getPassword() ) ) {
				user.setPassword( bCryptPasswordEncoder.encode( userUpdateRequest.getPassword() ) );
			}
			if ( !Strings.isNullOrEmpty( userUpdateRequest.getPhoneNumber() ) ) {
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

	public boolean isUserAuthenticated( final long userId, final String username ) {
		return userRepository.findById( userId ).get().getUsername().equals( username );
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
