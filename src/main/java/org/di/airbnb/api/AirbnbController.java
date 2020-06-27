package org.di.airbnb.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.di.airbnb.AirbnbManager;
import org.di.airbnb.api.request.BookingRequest;
import org.di.airbnb.api.request.MessagingCreationRequest;
import org.di.airbnb.api.request.ReviewPropertyCreationRequest;
import org.di.airbnb.api.request.SearchRequest;
import org.di.airbnb.api.request.UserCreationRequest;
import org.di.airbnb.api.request.UserUpdateRequest;
import org.di.airbnb.api.request.property.PropertyCreationRequest;
import org.di.airbnb.api.request.property.PropertyUpdateRequest;
import org.di.airbnb.api.response.JwtResponse;
import org.di.airbnb.assemblers.UsernamePasswordModel;
import org.di.airbnb.assemblers.booking.BookingModel;
import org.di.airbnb.assemblers.image.ImageModel;
import org.di.airbnb.assemblers.location.CityModel;
import org.di.airbnb.assemblers.location.CountryModel;
import org.di.airbnb.assemblers.location.DistrictModel;
import org.di.airbnb.assemblers.messaging.MessagingModel;
import org.di.airbnb.assemblers.property.PropertyBasicInfo;
import org.di.airbnb.assemblers.property.PropertyModel;
import org.di.airbnb.assemblers.property.PropertyWithRentingRules;
import org.di.airbnb.assemblers.rating.RatingModel;
import org.di.airbnb.assemblers.user.UserAvatarModel;
import org.di.airbnb.assemblers.user.UserModel;
import org.di.airbnb.constant.Role;
import org.di.airbnb.dao.entities.User;
import org.di.airbnb.exceptions.api.InvalidUserActionException;
import org.di.airbnb.exceptions.api.UserAnauthorizedException;
import org.di.airbnb.exceptions.api.UserNotFoundException;
import org.di.airbnb.exceptions.api.UserNotValidException;
import org.di.airbnb.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Strings;

@RestController
@RequestMapping("/airbnb")
public class AirbnbController {
	private static final Logger LOGGER = LoggerFactory.getLogger( AirbnbController.class );

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtils jwtUtils;
	@Resource
	private AirbnbManager airbnbManager;

	@GetMapping(value = "/healthcheck")
	public String sayHello() {
		return "ping";
	}

	/*
	curl
		-H "Content-Type: application/json"
		-d '{"username": "user4", "password": "user4", "firstName": "user4", "lastName": "user4", "phoneNumber": "123456789", "isHost": "True","email": "user4@user4"  }'
		-X POST -k http://localhost:8443/airbnb/user/signup
	*/
	@PostMapping(value = "user/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<JwtResponse> signUp(
			@RequestBody @Valid @NotNull UserCreationRequest userCreationRequest ) {
		long userId = airbnbManager.createUser( userCreationRequest );
		return ResponseEntity.ok( new JwtResponse(
				generateJwtAuthToken( userCreationRequest.getUsername(),
						userCreationRequest.getPassword() ), userId ) );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-d '{"username": "user4", "password": "user4" }'
		-X POST -k http://localhost:8443/airbnb/user/login
	 */
	@PostMapping(value = "user/login")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<JwtResponse> login(
			@RequestBody @Valid @NotNull UsernamePasswordModel loginRequest ) {
		Optional<User> userOpt = airbnbManager.getUserByUsername( loginRequest.getUsername() );
		if ( !userOpt.isPresent() ) {
			LOGGER.info( String.format( "User with username %s not found",
					loginRequest.getUsername() ) );
			throw new UserNotFoundException( "User do not exist" );
		} else {
			return ResponseEntity.ok( new JwtResponse(
					generateJwtAuthToken( loginRequest.getUsername(), loginRequest.getPassword() ),
					userOpt.get().getId() ) );
		}
	}

	private String generateJwtAuthToken( final String username, final String password ) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken( username, password ) );

		SecurityContextHolder.getContext().setAuthentication( authentication );
		return jwtUtils.generateJwtToken( authentication );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyNCIsImlhdCI6MTU5MzI2NTQyNiwiZXhwIjoxNTkzMzUxODI2fQ.s2H_V8tf4gBPyTwagdAoO1H4bavtZmAt8Z5TpWDwCoHl5zzyQvBPw_Jyhf-IO1lYc0RlRg-uyT0cj_bmmvnDXQ"
		-d '{"username": "", "password": "", "firstName": "firstNameUpdated", "lastName": "aValidLastName", "phoneNumber": "12121212", "email": "sandu@sandu"  }'
		-X POST -k http://localhost:8443/airbnb/user/4/update
	*/
	@PostMapping(value = "user/{id}/update")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<JwtResponse> updateUserInfo(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("id") long userId,
			@RequestBody @NotNull UserUpdateRequest userUpdateRequest ) {
		final String usernameFromJwt = getUsernameFromJwt( authorizationHeader );
		if ( !airbnbManager.isUserAuthorized( userId, usernameFromJwt ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		airbnbManager.updateUser( userUpdateRequest, userId );

		String newPassword = userUpdateRequest.getPassword();
		authorizationHeader = authorizationHeader.replace( "Bearer", "" );
		authorizationHeader.trim();
		String authToken = !Strings.isNullOrEmpty( newPassword ) ? generateJwtAuthToken(
				usernameFromJwt, newPassword ) : authorizationHeader;
		return new ResponseEntity<>( new JwtResponse( authToken, userId ), HttpStatus.CREATED );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYWJ5IiwiaWF0IjoxNTkxMzg2MTQ5LCJleHAiOjE1OTE0NzI1NDl9.wpUlVD_LGB8ymLXyQGklooCPhkLY2WnpknWqTMfKI_j1lEnNXwfDSFYwY4yaMIH7i1FDx1n2JfRZvg8Fu4R8jQ"
		-X POST -k http://localhost:8443/airbnb/user/5/beHost
	*/
	@PostMapping(value = "user/{id}/beHost")
	public ResponseEntity<?> updateUserToBeHost(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("id") long userId ) {
		final String usernameFromJwt = getUsernameFromJwt( authorizationHeader );
		if ( !airbnbManager.isUserAuthorized( userId, usernameFromJwt ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		airbnbManager.updateUserToBeHost( userId );
		return new ResponseEntity<>( HttpStatus.OK );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJnYXRha29zIiwiaWF0IjoxNTkxNzEyMTM4LCJleHAiOjE1OTE3OTg1Mzh9.owSjPSD76NzE255Q2Gbfi1atR6VmS96ID2gVPcabXiQ7FlT9-9CXyxVQkUE6mj8tAr30MDXGfpruDy05kF_KNQ"
	 	http://localhost:8443/airbnb/user/38
	* */
	@GetMapping(value = "user/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<UserModel> getUserInfo( @PathVariable("id") long userId ) {
		Optional<UserModel> user = airbnbManager.getUserInfo( userId );
		if ( user.isPresent() ) {
			return new ResponseEntity<>( user.get(), HttpStatus.OK );
		} else {
			LOGGER.info( String.format( "User with id: %s cannot be found", userId ) );
			return new ResponseEntity<>( HttpStatus.NOT_FOUND );
		}
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTU5MTcxNzM1NSwiZXhwIjoxNTkxODAzNzU1fQ.Y-tVjhsCdXLGd5C-npGarUFEyrPQYIez-9QQLIm-alKcJsRDknSXgrsKqEnJbUqigOM3_Yhawh0GlDDwvt2D8A"
		http://localhost:8443/airbnb/host/38/properties
	*/
	@GetMapping(value = "host/{id}/properties")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<PropertyModel> getPropertiesByHost(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("id") long userId ) {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		Optional<PropertyModel> propertyModel = airbnbManager.getPropertyByHost( userId );
		return propertyModel.isPresent() ? new ResponseEntity<>( propertyModel.get(),
				HttpStatus.OK ) : new ResponseEntity<>( HttpStatus.NOT_FOUND );
	}

	private String getUsernameFromJwt( String authorizationHeader ) {
		if ( StringUtils.hasText( authorizationHeader ) && authorizationHeader.startsWith(
				"Bearer " ) ) {
			return jwtUtils.getUserNameFromJwtToken( authorizationHeader.substring( 7 ) );
		}

		return null;
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYWJ5IiwiaWF0IjoxNTkxMzg2MTQ5LCJleHAiOjE1OTE0NzI1NDl9.wpUlVD_LGB8ymLXyQGklooCPhkLY2WnpknWqTMfKI_j1lEnNXwfDSFYwY4yaMIH7i1FDx1n2JfRZvg8Fu4R8jQ"
		http://localhost:8443/airbnb/user/38/bookings
	*/
	@GetMapping(value = "user/{id}/bookings")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<BookingModel>> getUserBookings(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("id") long userId ) {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		return new ResponseEntity<>( airbnbManager.getUserBookings( userId ), HttpStatus.OK );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzczEiLCJpYXQiOjE1OTE1Mjk3NjUsImV4cCI6MTU5MTYxNjE2NX0.yXEmqMgvHhGO3OQL8oxpdSaGtE2DfaXb65zWSF6iHU7YAiI_qU-K97-vvKEPsBdmPk_i623sWiuUlLkKyVFmJg"
		http://localhost:8443/airbnb/property/1
	*/
	@GetMapping(value = "property/{id}")
	public ResponseEntity<PropertyWithRentingRules> getPropertyById(
			@PathVariable("id") long propertyId ) {
		Optional<PropertyWithRentingRules> propertyModelOpt = airbnbManager.getPropertyById(
				propertyId );
		return propertyModelOpt.isPresent() ? new ResponseEntity<>( propertyModelOpt.get(),
				HttpStatus.OK ) : new ResponseEntity<>( HttpStatus.NOT_FOUND );
	}

	/*
curl
	-H "Content-Type: application/json"
	-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzczEiLCJpYXQiOjE1OTE1Mjk3NjUsImV4cCI6MTU5MTYxNjE2NX0.yXEmqMgvHhGO3OQL8oxpdSaGtE2DfaXb65zWSF6iHU7YAiI_qU-K97-vvKEPsBdmPk_i623sWiuUlLkKyVFmJg"
	http://localhost:8443/airbnb/property/1/host
*/
	@GetMapping(value = "property/{id}/host")
	public ResponseEntity<UserAvatarModel> getPropertyHost( @PathVariable("id") long propertyId ) {
		return new ResponseEntity<>( airbnbManager.getPropertyHost( propertyId ), HttpStatus.OK );
	}

	/*
	curl
		-H "Authorization: Bearer  eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTU5MzI4NTU3MCwiZXhwIjoxNTkzMzcxOTcwfQ.3bJWHP6xVzVD9zPe-Ak2znYvtCsnhQBsPyhHw6RsC2zL9_EHXAmmeTUMUfRinCSafK6BX_VpyEDAm6WHJGS-NQ"
		-X POST -k http://localhost:8443/airbnb/user/1/avatar/upload
		-F "imageFile=@testPng.png"
	*/
	@PostMapping("user/{id}/avatar/upload")
	public ResponseEntity<?> uploadAvatar(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("id") long userId, @RequestParam("imageFile") MultipartFile file )
			throws IOException {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		airbnbManager.saveAvatar( file, userId );
		return new ResponseEntity<>( "Avatar updated", HttpStatus.OK );
	}

	/*
	curl
		-H "Authorization: Bearer  eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMiIsImlhdCI6MTU5MzI4NjA0OSwiZXhwIjoxNTkzMzcyNDQ5fQ.ImUnNOQ0lJ84aQb0Od_jMBU-_fZS8582J8h725nt4JB8YEoK_9NJltL7vRH6DD9fR4awH9dy3EG-H_tTWtWDKg"
		-X POST -k http://localhost:8443/airbnb/user/2/property/2/upload
		-F "imageFile=@testPng.png"
	*/
	@PostMapping("user/{userId}/property/{propertyId}/upload")
	public ResponseEntity<?> uploadPropertyImage(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId, @PathVariable("propertyId") long propertyId,
			@RequestParam("imageFile") MultipartFile file ) throws IOException {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		airbnbManager.savePropertyImage( file, propertyId, userId );
		return new ResponseEntity<>( "Property image uploaded", HttpStatus.OK );
	}

	/*
		curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer  eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTU5MjA2NDc1OCwiZXhwIjoxNTkyMTUxMTU4fQ.uZIutRzfWZJw0tirhhYvv7v2bkSxOl4y5P6Ye9F0-LucYE1wVNAlXA-ajdIbZ4lJXLBqoBzdD1nfN9l83e5Diw"
		http://localhost:8443/airbnb/user/1/avatar
	*/
	@GetMapping(path = { "/user/{userId}/avatar" })
	public ResponseEntity<ImageModel> getAvatar( @PathVariable("userId") long userId )
			throws IOException {
		final Optional<ImageModel> retrievedImage = airbnbManager.getAvatar( userId );

		return retrievedImage.isPresent() ? new ResponseEntity<>( retrievedImage.get(),
				HttpStatus.OK ) : new ResponseEntity<>( HttpStatus.NOT_FOUND );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkcHcxIiwiaWF0IjoxNTkxNTQzNjk3LCJleHAiOjE1OTE2MzAwOTd9.C-2VSjy-q4w845llI5fQstGX-dmDZkHbuPBNWhMch98XO9A9kv2MaYhru53A8mCzsfoG-HjqQz3nWO_ttWWjTg"
		-d '{"name": "name", "propertyType": "ROOM", "countryId": 1, "cityId": 1, "districtId": 1,"price": 1.2, "beds": 1, "bedrooms": 1, "bathrooms": 1, "minimumDays": 1, "maximumTenants": 1, "propertySize": 1, "freeText": "la la", "aircondition": "True",  "tv": "True", "internet": "True", "livingRoom": "True", "kitchen": "True", "partyFriendly": "True", "petFriendly": "True", "smokingFriendly": "True", "extraFreeText": "", "longitude": -74.00898606, "latitude": 40.71727401, "availableDates": [{"from": "", "to": }]  }'
		-X POST -k http://localhost:8443/airbnb/user/4/property/create
	*/
	@PostMapping(value = "user/{userId}/property/create")
	public ResponseEntity<?> createProperty(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId,
			@RequestBody @Valid @NotNull PropertyCreationRequest propertyCreationRequest ) {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		long createdPropertyId = airbnbManager.createProperty( propertyCreationRequest, userId );

		return new ResponseEntity<>( createdPropertyId, HttpStatus.CREATED );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkcHcxIiwiaWF0IjoxNTkxNTQzNjk3LCJleHAiOjE1OTE2MzAwOTd9.C-2VSjy-q4w845llI5fQstGX-dmDZkHbuPBNWhMch98XO9A9kv2MaYhru53A8mCzsfoG-HjqQz3nWO_ttWWjTg"
		-d '{"name": "la la la la", "propertyType": "HOUSE", "country": "a", "city": "a", "district": "a","price": 1.2, "beds": 1, "bedrooms": 1, "bathrooms": 1, "minimumDays": 1, "maximumTenants": 1, "propertySize": 1, "freeText": "la la", "aircondition": "True",  "tv": "True", "internet": "True", "livingRoom": "True", "kitchen": "True", "partyFriendly": "True", "petFriendly": "True", "smokingFriendly": "True", "extraFreeText": ""  }'
		-X POST -k http://localhost:8443/airbnb/user/4/property/7/update
	*/
	@PostMapping(value = "user/{userId}/property/{propertyId}/update")
	public ResponseEntity<?> updateProperty(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId, @PathVariable("propertyId") long propertyId,
			@RequestBody @NotNull PropertyUpdateRequest propertyUpdateRequest ) {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		airbnbManager.updateProperty( propertyUpdateRequest, propertyId );

		return new ResponseEntity<>( "Property updated", HttpStatus.CREATED );
	}

	//TODO: DO not actually delete!! Make it not available for search, booking etc!
	// it cause how else will you keep the
	// SOS. Na ftiaksw na kanei delete kai ap to availability
	/*
	curl
		-X "DELETE"  http://localhost:8443/airbnb/user/4/property/8/delete
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkcHcxIiwiaWF0IjoxNTkxNTQzNjk3LCJleHAiOjE1OTE2MzAwOTd9.C-2VSjy-q4w845llI5fQstGX-dmDZkHbuPBNWhMch98XO9A9kv2MaYhru53A8mCzsfoG-HjqQz3nWO_ttWWjTg"
	*/
	@DeleteMapping(value = "user/{userId}/property/{propertyId}/delete")
	public ResponseEntity<?> deleteProperty(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId, @PathVariable("propertyId") long propertyId ) {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		try {
			airbnbManager.deleteProperty( userId, propertyId );
		} catch ( InvalidUserActionException e ) {
			return new ResponseEntity<>( "User is not eligible to perform this action",
					HttpStatus.NO_CONTENT );
		}
		return new ResponseEntity<>( "Resource deleted", HttpStatus.OK );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwdzEiLCJpYXQiOjE1OTE1NjAzODQsImV4cCI6MTU5MTY0Njc4NH0.41NI-LKJO67Iu_RoHUVjosQTkSXr_x8bkXTdZyC04m12F6Dyj1FqA-Z-3PjvuoB9QUf6j0ZA-HsYNYHhvQ6P7A"
		http://localhost:8443/airbnb/user/1/messaging
	*/
	@GetMapping(value = "user/{userId}/messaging")
	public ResponseEntity<HashMap<Long, List<MessagingModel>>> getMessages(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId ) {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}

		return new ResponseEntity<>( airbnbManager.getMessages( userId ), HttpStatus.OK );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwdzEiLCJpYXQiOjE1OTE1NjAzODQsImV4cCI6MTU5MTY0Njc4NH0.41NI-LKJO67Iu_RoHUVjosQTkSXr_x8bkXTdZyC04m12F6Dyj1FqA-Z-3PjvuoB9QUf6j0ZA-HsYNYHhvQ6P7A"
		http://localhost:8443/airbnb/user/1/messaging/2
		-d '{"body": "la la la la"  }'
	*/
	@PostMapping(value = "user/{userId}/messaging/{recipientId}")
	public ResponseEntity<?> createMessage(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId, @PathVariable("recipientId") long recipientId,
			@RequestBody @Valid @NotNull MessagingCreationRequest messagingCreationRequest ) {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		if ( userId == recipientId ) {
			throw new UserNotValidException( "User cannot send a message to itself." );
		}
		airbnbManager.createMessaging( messagingCreationRequest.getBody(), userId, recipientId );
		return new ResponseEntity<>( HttpStatus.CREATED );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZGRkcHcxIiwiaWF0IjoxNTkxNjM4NzUxLCJleHAiOjE1OTE3MjUxNTF9.Y5oNQF0v4bZO0M7qFyddxmx6HfGXDYWxas_-39XezQDnMg60Idtxxcr08U9CTCEoktXf0VrTB6rHdvSthOkMLA"
		-d '{"mark": 3 }'
		-X POST -k http://localhost:8443/airbnb/user/4/property/4/review/create
	*/
	@PostMapping(value = "user/{userId}/property/{propertyId}/review/create")
	public ResponseEntity<?> reviewProperty(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId, @PathVariable("propertyId") long propertyId,
			@RequestBody @Valid @NotNull ReviewPropertyCreationRequest reviewPropertyCreationRequest ) {

		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		try {
			airbnbManager.reviewProperty( userId, propertyId, reviewPropertyCreationRequest );
		} catch ( InvalidUserActionException e ) {
			throw new UserNotValidException(
					"User has to have booked the place before to try to review it." );
		}
		return new ResponseEntity<>( "Review submitted!", HttpStatus.CREATED );
	}

	//TODO: owner cannot review its own property
	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZGRkcHcxIiwiaWF0IjoxNTkxNjM4NzUxLCJleHAiOjE1OTE3MjUxNTF9.Y5oNQF0v4bZO0M7qFyddxmx6HfGXDYWxas_-39XezQDnMg60Idtxxcr08U9CTCEoktXf0VrTB6rHdvSthOkMLA"
		http://localhost:8443/airbnb/rating/property/1
	*/
	@GetMapping(value = "rating/property/{propertyId}")
	public ResponseEntity<List<RatingModel>> getPropertyRating(
			@PathVariable("propertyId") long propertyId ) {
		return new ResponseEntity<>( airbnbManager.getPropertyRatings( propertyId ),
				HttpStatus.OK );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer  eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyNCIsImlhdCI6MTU5MjI0NzU3MCwiZXhwIjoxNTkyMzMzOTcwfQ.k4j4UxZlh0skF01_xRi3hBmhE2lQYscBD_NccCqGX8aMXUOljPDJrIcVJdz5WEwHag6e30DRUCzwlU7kOPp6sw"
		http://localhost:8443/airbnb/rating/user/1
	*/
	@GetMapping(value = "rating/user/{userId}")
	public ResponseEntity<List<RatingModel>> getHostRating( @PathVariable("userId") long userId ) {
		return new ResponseEntity<>( airbnbManager.getHostRatings( userId ), HttpStatus.OK );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer  eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyNCIsImlhdCI6MTU5MjI0NzU3MCwiZXhwIjoxNTkyMzMzOTcwfQ.k4j4UxZlh0skF01_xRi3hBmhE2lQYscBD_NccCqGX8aMXUOljPDJrIcVJdz5WEwHag6e30DRUCzwlU7kOPp6sw"
		http://localhost:8443/airbnb/user/1/ishost
	*/
	@GetMapping(value = "/user/{userId}/ishost")
	public ResponseEntity<?> isUserHost( @RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId ) {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		return new ResponseEntity<>(
				airbnbManager.getUserInfo( userId ).get().getRole().equals( Role.TENANT_AND_HOST ),
				HttpStatus.OK );
	}

	//TODO: 1
	//TODO: fix the pagination
	// TODO: load image
	// TODO: load meanRating
	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer  eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyNCIsImlhdCI6MTU5MjE2NzYxNCwiZXhwIjoxNTkyMjU0MDE0fQ.FyOHZcP4Q6JUeG3SULiDmPPSQ8G8VS9pRiLQZE6GyLlVPQsInu3CMH9M9EBeA1SSOceQrbrGuUGwPjpxSEJjsg"
		-d '{"from": "2020-06-08", "to": "2020-06-08", "numberOfPeople": 3, "countryId": "1", "cityId": "1", "districtId": "1", "pagination": {"limit": 0, "offset": 1}  }'
		-X POST -k https://airbnb-sandu.herokuapp.com/airbnb/property/search


	curl -H "Content-Type: application/json"
	-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTU5MjI0ODYyNiwiZXhwIjoxNTkyMzM1MDI2fQ.Y2yFZZgKruGUrVqCTSEWDvbmqBdp_HkkS4tPlbVL7hapkl1DUenr-vsR15VHEhp5G1qBnMD8yZNB1uGuyYAKog"
	-d '{"from": "2020-06-08", "to": "2020-06-08", "numberOfPeople": 3, "countryId": "1", "cityId": "1", "districtId": "1", "pagination": {"limit": 0, "offset": 1}  }'
	-X POST -k http://localhost:8443/airbnb/property/search

	*/
	@PostMapping(value = "/property/search")
	public ResponseEntity<List<PropertyBasicInfo>> searchProperty(
			@RequestBody @Valid @NotNull SearchRequest searchRequest ) {
		return new ResponseEntity<>( airbnbManager.findProperties( searchRequest ),
				HttpStatus.CREATED );
	}

	//TODO: 2
	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZGRkcHcxIiwiaWF0IjoxNTkxNjM4NzUxLCJleHAiOjE1OTE3MjUxNTF9.Y5oNQF0v4bZO0M7qFyddxmx6HfGXDYWxas_-39XezQDnMg60Idtxxcr08U9CTCEoktXf0VrTB6rHdvSthOkMLA"
		-d '{"from": "2020-06-08", "to": "2020-06-18", "numberOfPeople": 3, "propertyId": 1  }'
		-X POST -k http://localhost:8443/airbnb/user/1/property/book
	*/
	@PostMapping(value = "user/{userId}/property/book")
	public ResponseEntity<?> bookProperty(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId,
			@RequestBody @Valid @NotNull BookingRequest bookingRequest ) {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}
		airbnbManager.bookProperty( userId, bookingRequest );

		return new ResponseEntity<>( HttpStatus.CREATED );
	}

	//TODO: fix the logic for popular places

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTU5MjIyOTkxOSwiZXhwIjoxNTkyMzE2MzE5fQ.GDbRGwPpr6hzsQMT1Y3gvWuReEj7nnqVR6cVut8Onr-mZ5jfa480MmrsWrWFSJp9bbsjXmhwSrhQKUDBK_tM5g"
		http://localhost:8443/airbnb/user/1/popular
	*/
	@GetMapping(value = "user/{userId}/popular")
	public ResponseEntity<List<PropertyBasicInfo>> getPopularPlaces(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId ) {
		if ( !airbnbManager.isUserAuthorized( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			LOGGER.info( String.format( "User with id: %s is not authorized", userId ) );
			throw new UserAnauthorizedException();
		}

		return new ResponseEntity<>( airbnbManager.getPopularPlaces( userId ), HttpStatus.OK );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTU5MTcyMDg5MSwiZXhwIjoxNTkxODA3MjkxfQ.i_Xbc1YDPDIMbz0Wl5bHwyK3Psrbc7wXZOb9ksEhXVohZUm7ZOmdGfEYRG5L_rEzAD6XabRPWtCodYXDcK9xSw"
		http://localhost:8443/airbnb/location/countries
*/
	@GetMapping(value = "location/countries")
	public ResponseEntity<List<CountryModel>> getCountries() {
		return new ResponseEntity<>( airbnbManager.getCountries(), HttpStatus.OK );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTU5MTcyNTkyMywiZXhwIjoxNTkxODEyMzIzfQ.YSSxTpAqqO6gpFpcA_9xgTKVuLuwUUiA9EW2FLA-aWVcjwuwG3eUR4L9cLgJ4no_K6bkvLx3X0SXEG_6ldlspw"
		http://localhost:8443/airbnb/country/1/cities
	*/
	@GetMapping(value = "country/{countryId}/cities")
	public ResponseEntity<List<CityModel>> getCities( @PathVariable("countryId") long countryId ) {
		return new ResponseEntity<>( airbnbManager.getCitiesByCountry( countryId ), HttpStatus.OK );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZGRkcHcxIiwiaWF0IjoxNTkxNjM4NzUxLCJleHAiOjE1OTE3MjUxNTF9.Y5oNQF0v4bZO0M7qFyddxmx6HfGXDYWxas_-39XezQDnMg60Idtxxcr08U9CTCEoktXf0VrTB6rHdvSthOkMLA"
		http://localhost:8443/airbnb/city/1/districts
	*/
	@GetMapping(value = "city/{cityId}/districts")
	public ResponseEntity<List<DistrictModel>> getDistricts( @PathVariable("cityId") long cityId ) {
		return new ResponseEntity<>( airbnbManager.getDistrictsByCity( cityId ), HttpStatus.OK );
	}

}
