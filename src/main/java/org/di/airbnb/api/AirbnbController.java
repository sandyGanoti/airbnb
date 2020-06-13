package org.di.airbnb.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.di.airbnb.AirbnbManager;
import org.di.airbnb.api.request.MessagingCreationRequest;
import org.di.airbnb.api.request.ReviewPropertyCreationRequest;
import org.di.airbnb.api.request.SearchRequest;
import org.di.airbnb.api.request.UserCreationRequest;
import org.di.airbnb.api.request.UserUpdateRequest;
import org.di.airbnb.api.request.property.PropertyCreationRequest;
import org.di.airbnb.api.request.property.PropertyUpdateRequest;
import org.di.airbnb.api.response.JwtResponse;
import org.di.airbnb.api.response.SearchResult;
import org.di.airbnb.assemblers.UsernamePasswordModel;
import org.di.airbnb.assemblers.location.CityModel;
import org.di.airbnb.assemblers.location.CountryModel;
import org.di.airbnb.assemblers.location.DistrictModel;
import org.di.airbnb.assemblers.messaging.MessagingModel;
import org.di.airbnb.assemblers.property.PropertyModel;
import org.di.airbnb.assemblers.property.PropertyWithRentingRules;
import org.di.airbnb.assemblers.rating.RatingModel;
import org.di.airbnb.assemblers.user.UserModel;
import org.di.airbnb.dao.entities.User;
import org.di.airbnb.exceptions.api.InvalidUserActionException;
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
	AuthenticationManager authenticationManager;
	@Autowired
	JwtUtils jwtUtils;
	@Resource
	private AirbnbManager airbnbManager;

	@GetMapping(value = "/healthcheck")
	public String sayHello() {
		return "ping";
	}

	/*
	curl
		-H "Content-Type: application/json"
		-d '{"username": "user2", "password": "user2", "firstName": "hopus", "lastName": "bourdou", "phoneNumber": "123456789", "isHost": "True","email": "sandudsw@sandu"  }'
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
		-d '{"username": "user1", "password": "user1" }'
	 	-X POST -k http://localhost:8443/airbnb/user/login
	 */
	@PostMapping(value = "user/login")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<JwtResponse> login(
			@RequestBody @Valid @NotNull UsernamePasswordModel loginRequest ) {
		User user = airbnbManager.getUserByUsername( loginRequest.getUsername() ).get();
		return ResponseEntity.ok( new JwtResponse(
				generateJwtAuthToken( loginRequest.getUsername(), loginRequest.getPassword() ),
				user.getId() ) );
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
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYWJ5IiwiaWF0IjoxNTkxMzg2MTQ5LCJleHAiOjE1OTE0NzI1NDl9.wpUlVD_LGB8ymLXyQGklooCPhkLY2WnpknWqTMfKI_j1lEnNXwfDSFYwY4yaMIH7i1FDx1n2JfRZvg8Fu4R8jQ"
		-d '{"username": "", "password": "", "firstName": "", "lastName": "", "phoneNumber": "12121212", "country": "UK","email": "sandu@sandu"  }'
		-X POST -k http://localhost:8443/airbnb/user/38/update
	*/
	@PostMapping(value = "user/{id}/update")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<JwtResponse> updateUserInfo(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("id") long userId,
			@RequestBody @NotNull UserUpdateRequest userUpdateRequest ) {
		final String usernameFromJwt = getUsernameFromJwt( authorizationHeader );
		if ( !airbnbManager.isUserAuthenticated( userId, usernameFromJwt ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
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
		if ( !airbnbManager.isUserAuthenticated( userId, usernameFromJwt ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
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
	public ResponseEntity<List<PropertyModel>> getPropertiesByHost(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("id") long userId ) {
		if ( !airbnbManager.isUserAuthenticated( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
		}
		return new ResponseEntity<>( airbnbManager.getPropertiesByHost( userId ), HttpStatus.OK );
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
	public ResponseEntity<List<PropertyModel>> getUserBookings(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("id") long userId ) {
		if ( !airbnbManager.isUserAuthenticated( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
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
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYWJ5IiwiaWF0IjoxNTkxMzg2MTQ5LCJleHAiOjE1OTE0NzI1NDl9.wpUlVD_LGB8ymLXyQGklooCPhkLY2WnpknWqTMfKI_j1lEnNXwfDSFYwY4yaMIH7i1FDx1n2JfRZvg8Fu4R8jQ"
		-X POST -k http://localhost:8443/airbnb/user/5/avatar/upload
	*/
	@PostMapping("user/{id}/avatar/upload")
	public ResponseEntity<?> uploadAvatar(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("id") long userId, @RequestParam("imageFile") MultipartFile file )
			throws IOException {
		if ( !airbnbManager.isUserAuthenticated( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
		}
		airbnbManager.saveAvatar( file, userId );
		return new ResponseEntity<>( "Avatar updated", HttpStatus.OK );
	}

	@PostMapping("user/{userId}/property/{propertyId}/upload")
	public ResponseEntity.BodyBuilder uploadPropertyImage(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId, @PathVariable("propertyId") long propertyId,
			@RequestParam("imageFile") MultipartFile file ) throws IOException {
		if ( !airbnbManager.isUserAuthenticated( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
		}
		airbnbManager.savePropertyImage( file, propertyId );
		return ResponseEntity.status( HttpStatus.OK );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkcHcxIiwiaWF0IjoxNTkxNTQzNjk3LCJleHAiOjE1OTE2MzAwOTd9.C-2VSjy-q4w845llI5fQstGX-dmDZkHbuPBNWhMch98XO9A9kv2MaYhru53A8mCzsfoG-HjqQz3nWO_ttWWjTg"
		-d '{"name": "name", "propertyType": "ROOM", "country": "a", "city": "a", "district": "a","price": 1.2, "beds": 1, "bedrooms": 1, "bathrooms": 1, "minimumDays": 1, "maximumTenants": 1, "propertySize": 1, "freeText": "la la", "aircondition": "True",  "tv": "True", "internet": "True", "livingRoom": "True", "kitchen": "True", "partyFriendly": "True", "petFriendly": "True", "smokingFriendly": "True", "extraFreeText": "", "longitude": -74.00898606, "latitude": 40.71727401, "availableDates": [{"from": "", "to": }]  }'
		-X POST -k http://localhost:8443/airbnb/user/4/property/create
	*/
	@PostMapping(value = "user/{userId}/property/create")
	public ResponseEntity<?> createProperty(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId,
			@RequestBody @Valid @NotNull PropertyCreationRequest propertyCreationRequest ) {
		if ( !airbnbManager.isUserAuthenticated( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
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
		if ( !airbnbManager.isUserAuthenticated( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
		}
		airbnbManager.updateProperty( propertyUpdateRequest, propertyId );

		return new ResponseEntity<>( "Property updated", HttpStatus.CREATED );
	}

	/*
	curl
		-X "DELETE"  http://localhost:8443/airbnb/user/4/property/8/delete
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkcHcxIiwiaWF0IjoxNTkxNTQzNjk3LCJleHAiOjE1OTE2MzAwOTd9.C-2VSjy-q4w845llI5fQstGX-dmDZkHbuPBNWhMch98XO9A9kv2MaYhru53A8mCzsfoG-HjqQz3nWO_ttWWjTg"
	*/
	@DeleteMapping(value = "user/{userId}/property/{propertyId}/delete")
	public ResponseEntity<?> deleteAuction(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId, @PathVariable("propertyId") long propertyId ) {
		if ( !airbnbManager.isUserAuthenticated( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
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
		if ( !airbnbManager.isUserAuthenticated( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
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
		if ( !airbnbManager.isUserAuthenticated( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
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

		if ( !airbnbManager.isUserAuthenticated( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
		}
		try {
			airbnbManager.reviewProperty( userId, propertyId,
					reviewPropertyCreationRequest.getMark() );
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
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZGRkcHcxIiwiaWF0IjoxNTkxNjM4NzUxLCJleHAiOjE1OTE3MjUxNTF9.Y5oNQF0v4bZO0M7qFyddxmx6HfGXDYWxas_-39XezQDnMg60Idtxxcr08U9CTCEoktXf0VrTB6rHdvSthOkMLA"
		http://localhost:8443/airbnb/rating/user/1
	*/
	@GetMapping(value = "rating/user/{userId}")
	public ResponseEntity<List<RatingModel>> getHostRating( @PathVariable("userId") long userId ) {
		return new ResponseEntity<>( airbnbManager.getHostRatings( userId ), HttpStatus.OK );
	}

	//TODO: fix the pagination
	// TODO: load image
	// TODO: load meanRating
	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZGRkcHcxIiwiaWF0IjoxNTkxNjM4NzUxLCJleHAiOjE1OTE3MjUxNTF9.Y5oNQF0v4bZO0M7qFyddxmx6HfGXDYWxas_-39XezQDnMg60Idtxxcr08U9CTCEoktXf0VrTB6rHdvSthOkMLA"
		-d '{"from": "2020-06-08T18:10:08Z", "to": "2020-06-08T18:10:08Z", "numberOfPeople": 3, "country": "-", "city": "-", "district": "-", "pagination": {"limit": 0, "offset": 1}  }'
		-X POST -k http://localhost:8443/airbnb/property/search
	*/
	@PostMapping(value = "/property/search")
	public ResponseEntity<List<SearchResult>> searchProperty(
			@RequestBody @Valid @NotNull SearchRequest searchRequest ) {
		return new ResponseEntity<>( airbnbManager.findProperties( searchRequest ),
				HttpStatus.CREATED );
	}

	//TODO: fix the logic for popular places

	/*
	curl
		-H "Content-Type: application/json"
		-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTU5MTk1OTU2OSwiZXhwIjoxNTkyMDQ1OTY5fQ.ksvOzIWMz6-OxxQYh6HaY2GBoqyPB2gNTvZ6i17GHQopCXo4kP-i05LcM0t7ytf7iR2ntd4JfggMHO4z-vCTKQ"
		http://localhost:8443/airbnb/user/4/popular
	*/
	@GetMapping(value = "user/{userId}/popular")
	public ResponseEntity<List<PropertyModel>> getPopularPlaces(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") long userId ) {
		if ( !airbnbManager.isUserAuthenticated( userId,
				getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
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
		http://localhost:8443/airbnb/country/1/districts
	*/
	@GetMapping(value = "city/{cityId}/districts")
	public ResponseEntity<List<DistrictModel>> getDistricts( @PathVariable("cityId") long cityId ) {
		return new ResponseEntity<>( airbnbManager.getDistrictsByCity( cityId ), HttpStatus.OK );
	}

	//	/******* MESSAGES ********/
	//	//	curl -k https://localhost:8443/messages/unread/recipient --header 'X-User-Id':1
	//	@GetMapping(value = "/messages/unread/recipient")
	//	@ResponseStatus(HttpStatus.OK)
	//	public boolean isNewMessage( @RequestHeader("X-User-Id") String userId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//
	//		return messagingService.isNewMessage( user.getId() );
	//	}
	//
	//	//	curl -k https://localhost:8443/users/messages/sent/info --header 'X-User-Id':1
	//	@GetMapping(value = "users/messages/sent/info")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<MessagingLimitedDTO> getSentInfoBySender(
	//			@RequestHeader("X-User-Id") String userId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		return messagingService.getSentInfoBySenderId( user.getId() );
	//	}
	//
	//	//	curl -k https://localhost:8443/users/messages/incoming/info --header 'X-User-Id':1
	//	@GetMapping(value = "users/messages/incoming/info")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<MessagingLimitedDTO> getIncomingInfoBySender(
	//			@RequestHeader("X-User-Id") String userId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//
	//		return messagingService.getIncomingInfoByRecipientId( user.getId() );
	//	}
	//
	//	//	curl -k https://localhost:8443/messages/to/3 --header 'X-User-Id':1
	//	@GetMapping(value = "messages/to/{recipientId}")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<MessagingDTO> getChatBySenderAndRecipient(
	//			@RequestHeader("X-User-Id") String userId,
	//			@PathVariable("recipientId") Long recipientId ) {
	//		try {
	//			userRepository.getUserInfo( Long.parseLong( userId ) );
	//			userRepository.getUserInfo( recipientId );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//
	//		return messagingService.getChatBySenderIdAndRecipientId( Long.parseLong( userId ),
	//				recipientId );
	//	}
	//
	//	//	set messages of a chat as READ
	//	//	curl -H "Content-Type: application/json" --header 'X-User-Id':1 -X POST -k https://localhost:8443/messages/read/to/2
	//	@PostMapping(value = "messages/read/to/{recipientId}")
	//	@ResponseStatus(HttpStatus.OK)
	//	public boolean readChatBySenderAndRecipient( @RequestHeader("X-User-Id") String userId,
	//			@PathVariable("recipientId") Long recipientId ) {
	//		try {
	//			userRepository.getUserInfo( Long.parseLong( userId ) );
	//			userRepository.getUserInfo( recipientId );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//
	//		return messagingService.readChatBySenderIdAndRecipientId( Long.parseLong( userId ),
	//				recipientId );
	//	}
	//
	//	//	curl -d '{"sender": 1, "recipient": 3, "messageBody": "hopus"}'  --header 'X-User-Id':1  -H "Content-Type: application/json"  -X POST -k https://localhost:8443/message
	//	@PostMapping(value = "message")
	//	@ResponseStatus(HttpStatus.CREATED)
	//	public Long createMessage( @RequestHeader("X-User-Id") String userId,
	//			@RequestBody MessagingDTO messagingDTO ) {
	//		try {
	//			userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//
	//		return messagingService.create( messagingDTO );
	//	}

}
