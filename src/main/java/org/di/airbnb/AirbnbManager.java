package org.di.airbnb;

import static org.di.airbnb.dao.UniqueConstraintHelper.causedByUniqueConstraint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.inject.Singleton;
import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;

import org.di.airbnb.api.request.BookingRequest;
import org.di.airbnb.api.request.ReviewPropertyCreationRequest;
import org.di.airbnb.api.request.SearchRequest;
import org.di.airbnb.api.request.UserCreationRequest;
import org.di.airbnb.api.request.UserUpdateRequest;
import org.di.airbnb.api.request.property.PropertyCreationRequest;
import org.di.airbnb.api.request.property.PropertyUpdateRequest;
import org.di.airbnb.assemblers.image.ImageModel;
import org.di.airbnb.assemblers.location.CityModel;
import org.di.airbnb.assemblers.location.CountryModel;
import org.di.airbnb.assemblers.location.DistrictModel;
import org.di.airbnb.assemblers.messaging.MessagingModel;
import org.di.airbnb.assemblers.property.PropertyBasicInfo;
import org.di.airbnb.assemblers.property.PropertyModel;
import org.di.airbnb.assemblers.property.PropertyWithRentingRules;
import org.di.airbnb.assemblers.property.RentingRulesModel;
import org.di.airbnb.assemblers.rating.RatingModel;
import org.di.airbnb.assemblers.user.UserAvatarModel;
import org.di.airbnb.assemblers.user.UserModel;
import org.di.airbnb.constant.Role;
import org.di.airbnb.dao.AirbnbDaoImpl;
import org.di.airbnb.dao.entities.Booking;
import org.di.airbnb.dao.entities.Image;
import org.di.airbnb.dao.entities.Messaging;
import org.di.airbnb.dao.entities.Property;
import org.di.airbnb.dao.entities.PropertyAvailability;
import org.di.airbnb.dao.entities.Rating;
import org.di.airbnb.dao.entities.RentingRules;
import org.di.airbnb.dao.entities.User;
import org.di.airbnb.dao.entities.location.City;
import org.di.airbnb.dao.entities.location.Country;
import org.di.airbnb.dao.entities.location.District;
import org.di.airbnb.dao.repository.BookingRepository;
import org.di.airbnb.dao.repository.MessagingRepository;
import org.di.airbnb.dao.repository.PropertyAvailabilityRepository;
import org.di.airbnb.dao.repository.PropertyRepository;
import org.di.airbnb.dao.repository.RatingRepository;
import org.di.airbnb.dao.repository.RentingRulesRepository;
import org.di.airbnb.dao.repository.UserRepository;
import org.di.airbnb.dao.repository.location.CityRepository;
import org.di.airbnb.dao.repository.location.CountryRepository;
import org.di.airbnb.dao.repository.location.DistrictRepository;
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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Singleton
@Service
public class AirbnbManager {
	private static final Logger LOGGER = LoggerFactory.getLogger( AirbnbManager.class );
	private final LoadingCache<Long, Country> countryCache;
	private final LoadingCache<Long, City> cityCache;
	private final LoadingCache<Long, District> districtCache;
	private final LoadingCache<Long, Image> imageCache;

	@Autowired
	private PropertyAvailabilityRepository propertyAvailabilityRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PropertyRepository propertyRepository;
	@Autowired
	private RentingRulesRepository rentingRulesRepository;
	@Autowired
	private MessagingRepository messagingRepository;
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private RatingRepository ratingRepository;
	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private DistrictRepository districtRepository;
	@Autowired
	private AirbnbDaoImpl airbnbDao;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public AirbnbManager() {
		this.countryCache = CacheBuilder.newBuilder()
				.expireAfterWrite( 20, TimeUnit.SECONDS )
				.recordStats()
				.softValues()
				.build( new CacheLoader<Long, Country>() {
					@Override
					public Country load( final Long countryId ) {
						return countryRepository.findById( countryId ).orElse( null );
					}
				} );
		this.cityCache = CacheBuilder.newBuilder()
				.expireAfterWrite( 20, TimeUnit.SECONDS )
				.recordStats()
				.softValues()
				.build( new CacheLoader<Long, City>() {
					@Override
					public City load( final Long cityId ) {
						return cityRepository.findById( cityId ).orElse( null );
					}
				} );
		this.districtCache = CacheBuilder.newBuilder()
				.expireAfterWrite( 20, TimeUnit.SECONDS )
				.recordStats()
				.softValues()
				.build( new CacheLoader<Long, District>() {
					@Override
					public District load( final Long districtId ) {
						return districtRepository.findById( districtId ).orElse( null );
					}
				} );
		/* propertyId as key */
		this.imageCache = CacheBuilder.newBuilder()
				.expireAfterWrite( 10, TimeUnit.SECONDS )
				.recordStats()
				.softValues()
				.build( new CacheLoader<Long, Image>() {
					@Override
					public Image load( final Long propertyId ) {
						List<Image> images = airbnbDao.getPropertyImages( propertyId );
						return images.isEmpty() ? new Image() : images.get( 0 );
					}
				} );
	}

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

	public Optional<PropertyModel> getPropertyByHost( final long hostId ) {
		PropertyModel propertyModel = null;
		Optional<Property> property = airbnbDao.getPropertyByHost( hostId );
		if ( property.isPresent() ) {
			propertyModel = modelMapper.map( property.get(), PropertyModel.class );
			Image image = imageCache.getUnchecked( propertyModel.getId() );
			propertyModel.setImage(
					image != null ? modelMapper.map( image, ImageModel.class ) : null );
		}
		return propertyModel == null ? Optional.empty() : Optional.of( propertyModel );
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

	public List<PropertyBasicInfo> getPopularPlaces( final long userId ) {
		return constructPropertyBasicInfo( airbnbDao.getPopularPlaces( userId ) );
	}

	private List<PropertyBasicInfo> constructPropertyBasicInfo( final List<Property> properties ) {
		return properties.stream().map( property -> {
			PropertyBasicInfo propertyBasicInfo = new PropertyBasicInfo();
			propertyBasicInfo.setId( property.getId() );
			propertyBasicInfo.setCity( cityCache.getUnchecked( property.getCityId() ).getName() );
			propertyBasicInfo.setCountry(
					countryCache.getUnchecked( property.getCountryId() ).getName() );
			propertyBasicInfo.setDistrict(
					districtCache.getUnchecked( property.getDistrictId() ).getName() );
			propertyBasicInfo.setImage(
					modelMapper.map( imageCache.getUnchecked( property.getId() ),
							ImageModel.class ) );
			propertyBasicInfo.setMeanRating( getMeanRating( property.getId() ) );
			propertyBasicInfo.setPropertyName( property.getName() );
			return propertyBasicInfo;
		} ).collect( Collectors.toList() );
	}

	public List<CityModel> getCitiesByCountry( final long countryId ) {
		return modelMapper.map( airbnbDao.getCitiesByCountry( countryId ), List.class );
	}

	public List<DistrictModel> getDistrictsByCity( final long cityId ) {
		return modelMapper.map( airbnbDao.getDistrictsByCity( cityId ), List.class );
	}

	public UserAvatarModel getPropertyHost( final long propertyId ) {
		Property property = propertyRepository.getOne( propertyId );
		if ( property == null ) {
			throw new PropertyNotFoundException( "Property do not exist" );
		}

		User user = userRepository.getOne( property.getHostId() );
		Optional<Image> avatarOpt = airbnbDao.getAvatar( user.getId() );
		ImageModel imageModel = null;
		if ( avatarOpt.isPresent() ) {
			imageModel = modelMapper.map( avatarOpt.get(), ImageModel.class );
		}
		return new UserAvatarModel( user.getId(), imageModel );
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

		propertyWithRentingRules.setImages( airbnbDao.getPropertyImages( propertyId )
				.stream()
				.map( image -> modelMapper.map( image, ImageModel.class ) )
				.collect( Collectors.toList() ) );

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
			imageM.setPicture( decompressBytes( imageM.getPicture() ) );
			imageModel = Optional.of( imageM );
		} else {
			imageModel = Optional.empty();
		}
		return imageModel;
	}

	//TODO: do it in one transaction
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
		final long newlyCreatedPropertyId = newlyCreatedProperty.getId();
		propertyCreationRequest.getAvailableDates().stream().forEach( availableDate -> {
			propertyAvailabilityRepository.save( new PropertyAvailability( newlyCreatedPropertyId,
					availableDate.getAvailableFrom(), availableDate.getAvailableTo() ) );
		} );

		RentingRules rentingRules = modelMapper.map( propertyCreationRequest, RentingRules.class );
		rentingRules.setPropertyId( newlyCreatedPropertyId );
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
		if ( property.getHostId() != userId ) {
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
			if ( propertyUpdateRequest.getCountryId() != null ) {
				property.setCountryId( propertyUpdateRequest.getCountryId() );
			}
			if ( propertyUpdateRequest.getCityId() != null ) {
				property.setCityId( propertyUpdateRequest.getCityId() );
			}
			if ( propertyUpdateRequest.getDistrictId() != null ) {
				property.setDistrictId( propertyUpdateRequest.getDistrictId() );
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
			Long key = !message.getSender()
					.equals( userId ) ? message.getSender() : message.getRecipient();
			List<MessagingModel> messages = newMessages.get( key );
			if ( messages == null ) {
				messages = new ArrayList<>();
			}
			messages.add( modelMapper.map( message, MessagingModel.class ) );
			newMessages.put( key, messages );
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

	public void reviewProperty( final long raterId, final long propertyId,
			final ReviewPropertyCreationRequest reviewPropertyCreationRequest ) {
		if ( !airbnbDao.getPropertyBookingsByUser( raterId, propertyId ).isEmpty() ) {
			Rating rating = new Rating();
			rating.setMark( reviewPropertyCreationRequest.getMark() );
			rating.setText( reviewPropertyCreationRequest.getReview() );
			rating.setPropertyId( propertyId );
			rating.setRaterId( raterId );
			rating.setCreatedAt( Instant.now() );
			ratingRepository.save( rating );
		} else {
			throw new InvalidUserActionException();
		}
	}

	private double getMeanRating( final long propertyId ) {
		List<Rating> ratings = airbnbDao.getPropertyRatings( propertyId );
		double meanRating = 0;
		if ( ratings != null && !ratings.isEmpty() ) {
			meanRating = ratings.stream().mapToDouble( Rating::getMark ).average().getAsDouble();
		}
		return meanRating;
	}

	public List<PropertyBasicInfo> findProperties( final SearchRequest searchRequest ) {
		List<Property> properties = airbnbDao.getPropertiesBySearchQuery( searchRequest );
		return constructPropertyBasicInfo( properties );
	}

	public void bookProperty( final long userId, final BookingRequest bookingRequest ) {
		Booking booking = new Booking();
		booking.setCreatedAt( Date.from( Instant.now() ) );
		booking.setFromDatetime( bookingRequest.getFrom() );
		booking.setPropertyId( bookingRequest.getPropertyId() );
		booking.setToDatetime( bookingRequest.getTo() );
		booking.setTenantId( userId );

		bookingRepository.save( booking );

	}

}
