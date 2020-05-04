package org.di.airbnb.dao;

import java.util.List;
import java.util.Optional;

import org.di.airbnb.assemblers.MessagingLimitedDTO;
import org.di.airbnb.assemblers.UserSubModel;
import org.di.airbnb.dao.entities.Messaging;
import org.di.airbnb.dao.entities.Rating;
import org.springframework.stereotype.Repository;

@Repository
public interface AirbnbDao {

	//TODO: RETURN ENTITIES

	List<UserSubModel> getLimitedMany( List<Long> userIds );

	Optional<UserSubModel> getUserInfo( Long userId );

	Optional<UserSubModel> login( String username, String password );

	boolean isNewMessage( final long recipientId );

	List<MessagingLimitedDTO> getSentInfoBySenderId( final long senderId );

	List<MessagingLimitedDTO> getIncomingInfoByRecipientId( final long recipientId );

	List<Messaging> getChatBySenderIdAndRecipientId( final long senderId, final long recipientId );

	boolean readChatBySenderIdAndRecipientId( final long senderId, final long recipientId );

	List<Rating> getUserRatings( long userId );
}
