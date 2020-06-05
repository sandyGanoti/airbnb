package org.di.airbnb.api;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.di.airbnb.AirbnbManager;
import org.di.airbnb.api.request.UserCreationRequest;
import org.di.airbnb.api.request.UserUpdateRequest;
import org.di.airbnb.api.response.JwtResponse;
import org.di.airbnb.assemblers.UsernamePasswordModel;
import org.di.airbnb.assemblers.property.PropertyModel;
import org.di.airbnb.assemblers.rating.RatingModel;
import org.di.airbnb.assemblers.user.UserModel;
import org.di.airbnb.exceptions.api.UserNotValidException;
import org.di.airbnb.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/airbnb")
public class AirbnbController {
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Resource
	private AirbnbManager manager;

	@GetMapping(value = "/")
	public String sayHello() {
		return "ping";
	}

	/*
	curl
		-H "Content-Type: application/json"
		-d '{"username": "pw1", "password": "bourdoud", "firstName": "hopus", "lastName": "bourdou", "phoneNumber": "123456789", "isHost": "True","email": "sandudsw@sandu"  }'
		-X POST -k http://localhost:8443/airbnb/user/signup
	*/
	@PostMapping(value = "user/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity signUp(
			@RequestBody @Valid @NotNull UserCreationRequest userCreationRequest ) {
		manager.createUser( userCreationRequest ).getId();
		return ResponseEntity.ok( "User created successfully" );
	}

	/*
	curl
		-H "Content-Type: application/json"
		-d '{"username": "pw1", "password": "bourdoud" }'
	 	-X POST -k http://localhost:8443/airbnb/user/login
	 */
	@PostMapping(value = "user/login")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<JwtResponse> login(
			@RequestBody @Valid @NotNull UsernamePasswordModel loginRequest ) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken( loginRequest.getUsername(),
						loginRequest.getPassword() ) );

		SecurityContextHolder.getContext().setAuthentication( authentication );
		String jwt = jwtUtils.generateJwtToken( authentication );

		return ResponseEntity.ok( new JwtResponse( jwt ) );
	}

	/*
	curl
	-H "Content-Type: application/json"
	-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYWJ5IiwiaWF0IjoxNTkxMzg2MTQ5LCJleHAiOjE1OTE0NzI1NDl9.wpUlVD_LGB8ymLXyQGklooCPhkLY2WnpknWqTMfKI_j1lEnNXwfDSFYwY4yaMIH7i1FDx1n2JfRZvg8Fu4R8jQ"
	 http://localhost:8443/airbnb/user/38
	* */
	@GetMapping(value = "user/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<UserModel> getUserInfo( @PathVariable("id") long userId ) {
		return new ResponseEntity<>( manager.getUserInfo( userId ), HttpStatus.OK );
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
	public ResponseEntity<?> updateUserInfo( @PathVariable("id") long userId,
			@RequestBody @NotNull UserUpdateRequest userUpdateRequest ) {
		manager.updateUser( userUpdateRequest, userId );
		return new ResponseEntity<>( "User updated successfully", HttpStatus.CREATED );
	}

	//	//	curl -d '{"username": 1, "password": "bourdou", "firstName": "hopus", "lastName": "bourdou", "phoneNumber": "123456789", "country": "UK","email": "sandu@sandu"  }'  --header 'X-User-Id':1  -H "Content-Type: application/json"  -X POST -k https://localhost:8443/user/signup
	//	@PostMapping(value = "user/avatar")
	//	@ResponseStatus(HttpStatus.CREATED)
	//	public void addAvatar( @RequestHeader("X-User-Id") long userId,
	//			@RequestBody @NotNull UserUpdateRequest userUpdateRequest ) {
	//		manager.updateUser( userUpdateRequest,userId );
	//	}

	/*
	curl
	-H "Content-Type: application/json"
	-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYWJ5IiwiaWF0IjoxNTkxMzg2MTQ5LCJleHAiOjE1OTE0NzI1NDl9.wpUlVD_LGB8ymLXyQGklooCPhkLY2WnpknWqTMfKI_j1lEnNXwfDSFYwY4yaMIH7i1FDx1n2JfRZvg8Fu4R8jQ"
	http://localhost:8443/airbnb/host/38/properties
	*/
	@GetMapping(value = "host/{id}/properties")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<PropertyModel>> getPropertiesByHost(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("id") long userId ) {
		if ( !manager.isUserAuthenticated( userId, getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
		}
		return new ResponseEntity<>( manager.getPropertiesByHost( userId ), HttpStatus.OK );
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
		if ( !manager.isUserAuthenticated( userId, getUsernameFromJwt( authorizationHeader ) ) ) {
			throw new UserNotValidException( "User cannot perform that kind of action" );
		}
		return new ResponseEntity<>( manager.getUserBookings( userId ), HttpStatus.OK );
	}

	/*
	curl
	-H "Content-Type: application/json"
	-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYWJ5IiwiaWF0IjoxNTkxMzg2MTQ5LCJleHAiOjE1OTE0NzI1NDl9.wpUlVD_LGB8ymLXyQGklooCPhkLY2WnpknWqTMfKI_j1lEnNXwfDSFYwY4yaMIH7i1FDx1n2JfRZvg8Fu4R8jQ"
	http://localhost:8443/airbnb/user/38/rating/property/1
	*/
	@GetMapping(value = "user/{id}/rating/property/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<RatingModel>> getPropertyRating( @PathVariable("id") long userId,
			@PathVariable("id") long propertyId ) {
		return new ResponseEntity<>( manager.getPropertyRatings( propertyId ), HttpStatus.OK );
	}

	//	@GetMapping(value = "property/available")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<PropertyModel> getAvailableProperties(@RequestBody @NotNull PropertyAvailabilityRequest propertyAvailabilityRequest ) {
	//
	//			manager.propertyAvailabilityRequest( Long.parseLong( userId ) );
	//
	//
	//	}

	//	//	curl -k https://localhost:8443/auctions/active --header 'X-User-Id':1
	//	@GetMapping(value = "auctions/active")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<AuctionItemDTO> getActiveAuctions( @RequestHeader("X-User-Id") String userId ) {
	//		try {
	//			userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		List<AuctionItemDTO> activeAuctions = auctionItemService.getActiveAuctions();
	//		return activeAuctions;
	//	}
	//
	//	//	curl -k https://localhost:8443/auction/4 --header 'X-User-Id':1
	//	@GetMapping(value = "/auction/{id}")
	//	@ResponseStatus(HttpStatus.OK)
	//	public AuctionItemDTO getAuction( @RequestHeader("X-User-Id") String userId,
	//			@PathVariable("id") Long auctionId ) {
	//		try {
	//			userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		return auctionItemService.get( auctionId );
	//	}
	//
	//	//	curl -k https://localhost:8443/user/auctions/active --header 'X-User-Id':1
	//	@GetMapping(value = "user/auctions/active")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<AuctionItemDTO> getActiveAuctionsByUser(
	//			@RequestHeader("X-User-Id") String userId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		return auctionItemService.getActiveAuctionsByUser( Long.parseLong( userId ), user );
	//	}
	//
	//	//	curl -k https://localhost:8443/user/auctions --header 'X-User-Id':1
	//	@GetMapping(value = "user/auctions")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<AuctionItemDTO> getAuctionsByUser( @RequestHeader("X-User-Id") String userId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		return auctionItemService.getAuctionsByUser( Long.parseLong( userId ), user );
	//	}
	//
	//	//	curl -d '{"name": "item for auction","categories": [{"name": "BABY"}, {"name": "CRAFTS"}],"description": "hopus","location": "US","country": "Alabama","firstBid": 3,"startedAt": "","endsAt": "","bids" : [],"currently": 3,"userId": 1,"active": false}' --header 'X-User-Id':1  -H "Content-Type: application/json" -X POST -k https://localhost:8443/auction/
	//	@PostMapping(value = "auction/")
	//	@ResponseStatus(HttpStatus.CREATED)
	//	public Long createAuction( @RequestHeader("X-User-Id") String userId,
	//			@RequestBody AuctionItemDTO auctionDTO ) {
	//		try {
	//			userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		return auctionItemService.create( auctionDTO );
	//	}
	//
	//	//	curl -H "Content-Type: application/json" --header 'X-User-Id':1 -X POST -k https://localhost:8443/auction/4/activate
	//	@PostMapping(value = "auction/{id}/activate")
	//	@ResponseStatus(HttpStatus.OK)
	//	public boolean activateAuction( @RequestHeader("X-User-Id") String userId,
	//			@PathVariable("id") Long auctionId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		return auctionItemService.setAuctionActive( auctionId, user.getId() );
	//	}
	//
	//	//	curl -k https://localhost:8443/auction/1/bids --header 'X-User-Id':1
	//	@GetMapping(value = "/auction/{id}/bids")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<BidDTO> getBidsByAuctionItem( @RequestHeader("X-User-Id") String userId,
	//			@PathVariable("id") Long auctionItemId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		List<BidDTO> bids = bidService.getBidsByAuctionItemId( auctionItemId );
	//		bids.stream()
	//				.forEach( bidDTO -> bidDTO.setBidderUsername(
	//						userRepository.getUserInfo( bidDTO.getUserId() ).getUsername() ) );
	//		return bids;
	//	}
	//
	//	// curl -k https://localhost:8443/user/bids --header 'X-User-Id':1
	//	@GetMapping(value = "/user/bids")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<BidDTO> getBidsByUser( @RequestHeader("X-User-Id") String userId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		List<BidDTO> bids = bidService.getBidsByUserId( Long.parseLong( userId ) );
	//		bids.stream().forEach( bidDTO -> bidDTO.setBidderUsername( user.getUsername() ) );
	//		return bids;
	//	}
	//
	//	// curl -k https://localhost:8443/user/auction/1/bids --header 'X-User-Id':1
	//	@GetMapping(value = "/user/auction/{id}/bids")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<BidDTO> getBidsByAuctionItemAndUser( @RequestHeader("X-User-Id") String userId,
	//			@PathVariable("id") Long auctionItemId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		List<BidDTO> bids = bidService.getBidsByAuctionItemIdAndUserId( auctionItemId,
	//				Long.parseLong( userId ) );
	//		bids.stream().forEach( bidDTO -> bidDTO.setBidderUsername( user.getUsername() ) );
	//		return bids;
	//	}
	//
	//	//	curl -k  -X "DELETE"  https://localhost:8443/users/auction/3 --header 'X-User-Id':1
	//	@DeleteMapping(value = "users/auction/{id}")
	//	@ResponseStatus(HttpStatus.NO_CONTENT)
	//	public void deleteAuction( @RequestHeader("X-User-Id") String userId,
	//			@PathVariable("id") Long auctionItemId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		AuctionItemDTO auctionItemDTO = auctionItemService.get( auctionItemId );
	//		if ( !auctionItemDTO.canBeDeleted() ) {
	//			throw new InvalidActionException(
	//					"Cannot delete an auction that is active and bids have been submitted" );
	//		}
	//		auctionItemService.delete( auctionItemId, user.getId() );
	//	}
	//
	//	// curl -k https://localhost:8443/auctions/search/category/CRAFTS --header 'X-User-Id':1
	//	@GetMapping(value = "/auctions/search/category/{category}")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<ItemCategoryDTO> getAuctionsByCategory(
	//			@PathVariable("category") String category ) {
	//		return itemCategoryService.getAuctionsByCategory( category );
	//	}
	//
	//	// curl -k https://localhost:8443/auctions/search/text/opu --header 'X-User-Id':1
	//	@GetMapping(value = "/auctions/search/text/{text}")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<ItemCategoryDTO> getAuctionsByCategoryAndText( @PathVariable String text ) {
	//		return itemCategoryService.getAuctionsByCategoryAndText( text );
	//	}
	//
	//	// curl -d '{"amount": 2.0, "time": "", "auctionItemId": 1,"userId":  1}' --header 'X-User-Id':1  -H "Content-Type: application/json" -X POST -k https://localhost:8443/bid
	//	@PostMapping(value = "bid")
	//	@ResponseStatus(HttpStatus.CREATED)
	//	public Long createBid( @RequestHeader("X-User-Id") String userId, @RequestBody BidDTO bidDTO ) {
	//		try {
	//			userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		Long newBid = bidService.create( bidDTO );
	//		if ( newBid == null ) {
	//			throw new ConflictException( "User cannot bid with the same amount" );
	//		}
	//		return newBid;
	//	}
	//
	//	// curl -k https://localhost:8443/categories --header 'X-User-Id':1
	//	@GetMapping(value = "/categories")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<CategoryDTO> getCategories() {
	//		return auctionItemService.getCategories();
	//	}
	//
	//	/*****  Rating  *****/
	//	@PostMapping(value = "rating/")
	//	@ResponseStatus(HttpStatus.CREATED)
	//	public Long rateUser( @RequestHeader("X-User-Id") String userId,
	//			@RequestBody RatingDTO ratingDTO ) {
	//		try {
	//			userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		return ratingService.create( ratingDTO );
	//	}
	//
	//	@GetMapping(value = "/rating/user")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<RatingDTO> getUserRatings( @RequestHeader("X-User-Id") String userId,
	//			@PathVariable("id") Long ratedUserId ) {
	//		try {
	//			userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		return ratingService.getUserRatings( ratedUserId );
	//	}
	//
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
	//
	//	//	curl -X "DELETE"  -k https://localhost:8443/users/messages/2 --header 'X-User-Id':1
	//	@DeleteMapping(value = "users/messages/{messageId}")
	//	@ResponseStatus(HttpStatus.NO_CONTENT)
	//	public void deleteChat( @RequestHeader("X-User-Id") String userId,
	//			@PathVariable("messageId") Long messageId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//
	//		messagingService.delete( messageId, user.getId() );
	//	}
	//
	//	//	curl -k https://localhost:8443/auctions/1/winner --header 'X-User-Id':1
	//	/* Find all the auctions where this user has bid
	//	 * Find all the auctions where this user is owner
	//	 * On all the auctions, find the winners */
	//	@GetMapping(value = "auctions/{id}/winner")
	//	@ResponseStatus(HttpStatus.OK)
	//	public List<UserSubModel> getUsersAbleToChat( @RequestHeader("X-User-Id") String userId ) {
	//		UserSubModel user;
	//		try {
	//			user = userRepository.getUserInfo( Long.parseLong( userId ) );
	//		} catch ( EntityNotFoundException e ) {
	//			throw new UserNotFoundException( String.format( "User with id %d not found", userId ) );
	//		}
	//		/* find auctions created by user */
	//		List<AuctionItemDTO> auctionItems = auctionItemService.getAuctionsByUser(
	//				Long.parseLong( userId ), user );
	//		List<Long> userIds = new ArrayList<>();
	//		auctionItems.stream()
	//				.filter( auctionItemDTO -> !auctionItemDTO.getBids().isEmpty() )
	//				.forEach( auctionItemDTO -> {
	//
	//					BidDTO bidDTO = Collections.max( auctionItemDTO.getBids(),
	//							Comparator.comparing( BidDTO::getAmount ) );
	//					if ( bidDTO != null ) {
	//						userIds.add( bidDTO.getUserId() );
	//					}
	//
	//				} );
	//
	//
	//		/* fetch auctions in which user bid */
	//		List<Long> auctionItemIds = bidService.getAuctionsWhereUserBidded(
	//				Long.parseLong( userId ) );
	//		List<AuctionItemDTO> auctionItemDTOS = auctionItemService.getMany( auctionItemIds );
	//
	//		auctionItemDTOS.stream().forEach( auctionItemDTO -> {
	//			if ( Collections.max( auctionItemDTO.getBids(),
	//					Comparator.comparing( BidDTO::getAmount ) ).getUserId() == Long.parseLong(
	//					userId ) ) {
	//				userIds.add( Long.parseLong( userId ) );
	//			}
	//		} );
	//		return userRepository.getLimitedMany( userIds );
	//	}
	//

}
