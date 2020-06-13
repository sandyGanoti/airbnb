package org.di.airbnb;

import static org.di.airbnb.dao.UniqueConstraintHelper.causedByUniqueConstraint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.inject.Singleton;
import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;

import org.di.airbnb.api.request.SearchRequest;
import org.di.airbnb.api.request.UserCreationRequest;
import org.di.airbnb.api.request.UserUpdateRequest;
import org.di.airbnb.api.request.property.PropertyCreationRequest;
import org.di.airbnb.api.request.property.PropertyUpdateRequest;
import org.di.airbnb.api.response.SearchResult;
import org.di.airbnb.assemblers.image.ImageModel;
import org.di.airbnb.assemblers.location.CityModel;
import org.di.airbnb.assemblers.location.CountryModel;
import org.di.airbnb.assemblers.location.DistrictModel;
import org.di.airbnb.assemblers.messaging.MessagingModel;
import org.di.airbnb.assemblers.property.PropertyModel;
import org.di.airbnb.assemblers.property.PropertyWithRentingRules;
import org.di.airbnb.assemblers.property.RentingRulesModel;
import org.di.airbnb.assemblers.rating.RatingModel;
import org.di.airbnb.assemblers.user.UserModel;
import org.di.airbnb.constant.Role;
import org.di.airbnb.dao.AirbnbDaoImpl;
import org.di.airbnb.dao.entities.Image;
import org.di.airbnb.dao.entities.Messaging;
import org.di.airbnb.dao.entities.Property;
import org.di.airbnb.dao.entities.Rating;
import org.di.airbnb.dao.entities.RentingRules;
import org.di.airbnb.dao.entities.User;
import org.di.airbnb.dao.repository.ImageRepository;
import org.di.airbnb.dao.repository.MessagingRepository;
import org.di.airbnb.dao.repository.PropertyRepository;
import org.di.airbnb.dao.repository.RatingRepository;
import org.di.airbnb.dao.repository.RentingRulesRepository;
import org.di.airbnb.dao.repository.UserRepository;
import org.di.airbnb.dao.repository.location.CountryRepository;
import org.di.airbnb.exceptions.api.InvalidUserActionException;
import org.di.airbnb.exceptions.api.PropertyNotFoundException;
import org.di.airbnb.exceptions.api.UniqueConstraintViolationException;
import org.di.airbnb.exceptions.api.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Strings;

@Singleton
@Service
public class AirbnbManager {
	private static final Logger LOGGER = LoggerFactory.getLogger( AirbnbManager.class );

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private RentingRulesRepository rentingRulesRepository;

	@Autowired
	private MessagingRepository messagingRepository;

	@Autowired
	private RatingRepository ratingRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private AirbnbDaoImpl airbnbDao;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// compress the image bytes before storing it in the database
	private static byte[] compressBytes( byte[] data ) {
		Deflater deflater = new Deflater();
		deflater.setInput( data );
		deflater.finish();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( data.length );
		byte[] buffer = new byte[1024];
		while ( !deflater.finished() ) {
			int count = deflater.deflate( buffer );
			outputStream.write( buffer, 0, count );
		}
		try {
			outputStream.close();
		} catch ( IOException e ) {
		}
		LOGGER.info( "Compressed Image Byte Size - " + outputStream.toByteArray().length );
		return outputStream.toByteArray();
	}

	public static byte[] decompressBytes( byte[] data ) {
		Inflater inflater = new Inflater();
		inflater.setInput( data );
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( data.length );
		byte[] buffer = new byte[1024];
		try {
			while ( !inflater.finished() ) {
				int count = inflater.inflate( buffer );
				outputStream.write( buffer, 0, count );
			}
			outputStream.close();
		} catch ( IOException ioe ) {
		} catch ( DataFormatException e ) {
		}
		return outputStream.toByteArray();
	}

	public long createUser( final @NotNull UserCreationRequest userCreationRequest ) {
		User user = modelMapper.map( userCreationRequest, User.class );
		user.setCreatedAt( Instant.now() );
		user.setRole( userCreationRequest.isHost() ? Role.TENANT_AND_HOST : Role.TENANT );
		user.setPassword( bCryptPasswordEncoder.encode( userCreationRequest.getPassword() ) );
		try {
			//			user = userRepository.save( user );
			user = userRepository.saveAndFlush( user );
		} catch ( RuntimeException e ) {
			handleException( e );
		}
		return user.getId();
	}

	public Optional<User> getUserByUsername( final String username ) {
		return userRepository.findByUsername( username );
	}

	public void updateUser( final @NotNull UserUpdateRequest userUpdateRequest,
			final long userId ) {
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
				userRepository.save( user );
			} catch ( RuntimeException e ) {
				handleException( e );
			}
		} else {
			throw new UserNotFoundException( "User do not exist" );
		}
	}

	public void updateUserToBeHost( final long userId ) {
		Optional<User> userIsPresent = userRepository.findById( userId );
		if ( userIsPresent.isPresent() ) {
			User user = userIsPresent.get();
			user.setRole( Role.TENANT_AND_HOST );
			try {
				userRepository.save( user );
			} catch ( RuntimeException e ) {
				handleException( e );
			}
		} else {
			throw new UserNotFoundException( "User do not exist" );
		}
	}

	private void handleException( final RuntimeException e ) {
		if ( causedByUniqueConstraint( e, "email" ) ) {
			throw new UniqueConstraintViolationException( "Email already in use" );
		} else if ( causedByUniqueConstraint( e, "username" ) ) {
			throw new UniqueConstraintViolationException( "Username already exists" );
		} else if ( causedByUniqueConstraint( e, "name" ) ) {
			throw new UniqueConstraintViolationException( "Name already exists" );
		} else if ( causedByUniqueConstraint( e, "host_id" ) ) {
			throw new UniqueConstraintViolationException(
					"There is already one property assigned to this host" );
		} else {
			throw e;
		}
	}

	public boolean isUserAuthenticated( final long userId, final String username ) {
		Optional<User> pendingUser = userRepository.findById( userId );
		return pendingUser.isPresent() && pendingUser.get().getUsername().equals( username );
	}

	public List<PropertyModel> getPropertiesByHost( final long hostId ) {
		return modelMapper.map( airbnbDao.getPropertiesByHost( hostId ), List.class );
	}

	public List<PropertyModel> getUserBookings( final long hostId ) {
		return modelMapper.map( airbnbDao.getTenantBookings( hostId ), List.class );
	}

	public Optional<UserModel> getUserInfo( final long userId ) {
		try {
			return Optional.of(
					modelMapper.map( userRepository.getOne( userId ), UserModel.class ) );
		} catch ( PersistenceException e ) {
			return Optional.empty();
		}
	}

	public List<RatingModel> getPropertyRatings( final long propertyId ) {
		return modelMapper.map( airbnbDao.getPropertyRatings( propertyId ), List.class );
	}

	public List<RatingModel> getHostRatings( final long userId ) {
		return modelMapper.map( airbnbDao.getHostRatings( userId ), List.class );
	}

	public List<CountryModel> getCountries() {
		return modelMapper.map( countryRepository.findAll(), List.class );
	}

	public List<PropertyModel> getPopularPlaces( final long userId ) {
		return modelMapper.map( airbnbDao.getPopularPlaces( userId ), List.class );
	}

	public List<CityModel> getCitiesByCountry( final long countryId ) {
		return modelMapper.map( airbnbDao.getCitiesByCountry( countryId ), List.class );
	}

	public List<DistrictModel> getDistrictsByCity( final long cityId ) {
		return modelMapper.map( airbnbDao.getDistrictsByCity( cityId ), List.class );
	}

	public Optional<PropertyWithRentingRules> getPropertyById( final long propertyId ) {
		Property property = propertyRepository.getOne( propertyId );
		if ( property == null ) {
			return Optional.empty();
		}
		RentingRules rentingRules = airbnbDao.getPropertyRentingRules( propertyId );

		PropertyWithRentingRules propertyWithRentingRules = new PropertyWithRentingRules();
		propertyWithRentingRules.setPropertyModel(
				modelMapper.map( property, PropertyModel.class ) );
		propertyWithRentingRules.setRentingRulesModel(
				modelMapper.map( rentingRules, RentingRulesModel.class ) );

		return Optional.of( propertyWithRentingRules );
	}

	public void saveAvatar( final MultipartFile file, final long userId ) throws IOException {
		Image image = new Image( file.getOriginalFilename(), file.getContentType(),
				String.valueOf( userId ), compressBytes( file.getBytes() ) );
		Optional<Image> avatar = airbnbDao.getAvatar( userId );
		if ( avatar.isPresent() ) {
			Image userAvatar = avatar.get();
			userAvatar.setName( image.getName() );
			userAvatar.setType( image.getType() );
			userAvatar.setPicture( image.getPicture() );
			airbnbDao.updateAvatar( image );
			LOGGER.error( "update existing" + userAvatar.getId() );
		} else {
			airbnbDao.saveAvatar( image );
		}

		//		imageRepository.save( image );
	}

	public void savePropertyImage( final MultipartFile file, final long propertyId )
			throws IOException {
		Image image = new Image( file.getOriginalFilename(), file.getContentType(),
				String.valueOf( propertyId ), compressBytes( file.getBytes() ) );
		//		imageRepository.save( image );
		airbnbDao.saveAvatar( image );
	}

	public Optional<ImageModel> getAvatar( final long userId ) throws IOException {
		Optional<Image> image = airbnbDao.getAvatar( userId );
		Optional<ImageModel> imageModel = null;
		if ( image.isPresent() ) {
			ImageModel imageM = modelMapper.map( image.get(), ImageModel.class );
			imageM.setPicByte( decompressBytes( imageM.getPicByte() ) );
			imageModel = Optional.of( imageM );
		} else {
			imageModel = Optional.empty();
		}
		return imageModel;
	}

	public long createProperty( final PropertyCreationRequest propertyCreationRequest,
			final long hostId ) {
		Property property = modelMapper.map( propertyCreationRequest, Property.class );
		property.setHostId( hostId );
		Property newlyCreatedProperty = null;
		try {
			newlyCreatedProperty = propertyRepository.save( property );
		} catch ( RuntimeException e ) {
			handleException( e );
		}
		RentingRules rentingRules = modelMapper.map( propertyCreationRequest, RentingRules.class );
		rentingRules.setPropertyId( newlyCreatedProperty.getId() );
		rentingRulesRepository.save( rentingRules );
		return newlyCreatedProperty.getId();
	}

	//TODO: se ena transaction auto kai to apo panw
	public void deleteProperty( final long userId, final long propertyId ) {
		Optional<Property> propertyOpt = propertyRepository.findById( propertyId );
		if ( !propertyOpt.isPresent() ) {
			throw new PropertyNotFoundException( "Property do not exist" );
		}
		Property property = propertyOpt.get();
		if ( !property.getHostId().equals( userId ) ) {
			throw new InvalidUserActionException();
		}
		RentingRules rentingRules = airbnbDao.getPropertyRentingRules( propertyId );
		if ( rentingRules != null ) {
			rentingRulesRepository.delete( rentingRules );
		}
		propertyRepository.delete( property );
	}

	public void updateProperty( final @NotNull PropertyUpdateRequest propertyUpdateRequest,
			final long propertyId ) {
		Optional<Property> propertyIsPresent = propertyRepository.findById( propertyId );
		if ( propertyIsPresent.isPresent() ) {
			Property property = propertyIsPresent.get();
			RentingRules rentingRules = airbnbDao.getPropertyRentingRules( propertyId );

			if ( !Strings.isNullOrEmpty( propertyUpdateRequest.getName() ) ) {
				property.setName( propertyUpdateRequest.getName() );
			}

			if ( propertyUpdateRequest.getPropertyType() != null ) {
				property.setPropertyType( propertyUpdateRequest.getPropertyType() );
			}
			if ( !Strings.isNullOrEmpty( propertyUpdateRequest.getCountry() ) ) {
				property.setCountry( propertyUpdateRequest.getCountry() );
			}
			if ( !Strings.isNullOrEmpty( propertyUpdateRequest.getCity() ) ) {
				property.setCity( propertyUpdateRequest.getCity() );
			}
			if ( !Strings.isNullOrEmpty( propertyUpdateRequest.getDistrict() ) ) {
				property.setDistrict( propertyUpdateRequest.getDistrict() );
			}
			if ( propertyUpdateRequest.getPrice() != null && propertyUpdateRequest.getPrice()
					.compareTo( BigDecimal.ZERO ) == 1 ) {
				property.setPrice( propertyUpdateRequest.getPrice() );
			}
			if ( propertyUpdateRequest.getExtraPricePerPerson() != null && propertyUpdateRequest.getExtraPricePerPerson()
					.compareTo( BigDecimal.ZERO ) == 1 ) {
				property.setExtraPricePerPerson( propertyUpdateRequest.getExtraPricePerPerson() );
			}
			if ( propertyUpdateRequest.getBeds() != null && propertyUpdateRequest.getBeds()
					.intValue() > 0 ) {
				property.setBeds( propertyUpdateRequest.getBeds() );
			}
			if ( propertyUpdateRequest.getBedrooms() != null && propertyUpdateRequest.getBedrooms()
					.intValue() > 0 ) {
				property.setBedrooms( propertyUpdateRequest.getBedrooms() );
			}
			if ( propertyUpdateRequest.getBathrooms() != null && propertyUpdateRequest.getBathrooms()
					.intValue() > 0 ) {
				property.setBathrooms( propertyUpdateRequest.getBathrooms() );
			}
			if ( propertyUpdateRequest.getMinimumDays() != null && propertyUpdateRequest.getMinimumDays()
					.intValue() > 0 ) {
				property.setMinimumDays( propertyUpdateRequest.getMinimumDays() );
			}
			if ( propertyUpdateRequest.getMaximumTenants() != null && propertyUpdateRequest.getMaximumTenants()
					.intValue() > 0 ) {
				property.setMaximumTenants( propertyUpdateRequest.getMaximumTenants() );
			}
			if ( propertyUpdateRequest.getPropertySize() != null && Double.compare(
					propertyUpdateRequest.getPropertySize(), 0 ) > 1 ) {
				property.setPropertySize( propertyUpdateRequest.getPropertySize() );
			}
			if ( !Strings.isNullOrEmpty( propertyUpdateRequest.getFreeText() ) ) {
				property.setFreeText( propertyUpdateRequest.getFreeText() );
			}
			if ( propertyUpdateRequest.getLatitude() != null ) {
				property.setLatitude( propertyUpdateRequest.getLatitude() );
			}
			if ( propertyUpdateRequest.getLongitude() != null ) {
				property.setLongitude( propertyUpdateRequest.getLongitude() );
			}
			if ( propertyUpdateRequest.getAircondition() != null ) {
				rentingRules.setAircondition( propertyUpdateRequest.getAircondition() );
			}
			if ( propertyUpdateRequest.getTv() != null ) {
				rentingRules.setTv( propertyUpdateRequest.getTv() );
			}
			if ( propertyUpdateRequest.getInternet() != null ) {
				rentingRules.setInternet( propertyUpdateRequest.getInternet() );
			}
			if ( propertyUpdateRequest.getLivingRoom() != null ) {
				rentingRules.setLivingRoom( propertyUpdateRequest.getLivingRoom() );
			}
			if ( propertyUpdateRequest.getKitchen() != null ) {
				rentingRules.setKitchen( propertyUpdateRequest.getKitchen() );
			}
			if ( propertyUpdateRequest.getPartyFriendly() != null ) {
				rentingRules.setPartyFriendly( propertyUpdateRequest.getPartyFriendly() );
			}
			if ( propertyUpdateRequest.getPetFriendly() != null ) {
				rentingRules.setPetFriendly( propertyUpdateRequest.getPetFriendly() );
			}
			if ( propertyUpdateRequest.getSmokingFriendly() != null ) {
				rentingRules.setSmokingFriendly( propertyUpdateRequest.getSmokingFriendly() );
			}
			if ( propertyUpdateRequest.getExtraFreeText() != null ) {
				rentingRules.setFreeText( propertyUpdateRequest.getExtraFreeText() );
			}

			try {
				propertyRepository.save( property );
				rentingRulesRepository.save( rentingRules );
			} catch ( RuntimeException e ) {
				handleException( e );
			}
		} else {
			throw new PropertyNotFoundException( "Property do not exist" );
		}
	}

	public HashMap<Long, List<MessagingModel>> getMessages( final long userId ) {
		HashMap<Long, List<MessagingModel>> newMessages = new HashMap<>();

		airbnbDao.getMessages( userId ).forEach( message -> {
			Long sender = message.getSender();
			List<MessagingModel> messages = newMessages.get( sender );
			if ( messages == null ) {
				messages = new ArrayList<>();
			}
			messages.add( modelMapper.map( message, MessagingModel.class ) );
			newMessages.put( sender, messages );
		} );

		return newMessages;
	}

	public void createMessaging( final String body, final long senderId, final long recipientId ) {
		Messaging messaging = new Messaging();
		messaging.setRecipient( recipientId );
		messaging.setSender( senderId );
		messaging.setMessageBody( body );
		messaging.setCreatedAt( Instant.now() );
		messaging.setReadStatus( Boolean.FALSE );
		messagingRepository.save( messaging );
	}

	public void reviewProperty( final long raterId, final long propertyId, final int mark ) {
		if ( !airbnbDao.getPropertyBookingsByUser( raterId, propertyId ).isEmpty() ) {
			Rating rating = new Rating();
			rating.setMark( mark );
			rating.setPropertyId( propertyId );
			rating.setRaterId( raterId );
			rating.setCreatedAt( Instant.now() );
			ratingRepository.save( rating );
		} else {
			throw new InvalidUserActionException();
		}
	}

	public List<SearchResult> findProperties( final SearchRequest searchRequest ) {
		List<Property> properties = airbnbDao.getPropertiesBySearchQuery( searchRequest.getFrom(),
				searchRequest.getTo(), searchRequest.getNumberOfPeople(),
				searchRequest.getPagination() );
		return properties.stream().map( property -> {
			List<Rating> ratings = airbnbDao.getPropertyRatings( property.getId() );
			double meanRating = 0;
			if ( ratings != null ) {
				meanRating = ratings.stream()
						.mapToDouble( Rating::getMark )
						.average()
						.getAsDouble();
			}
			return new SearchResult( modelMapper.map( property, PropertyModel.class ), meanRating );
		} ).collect( Collectors.toList() );
	}

}
