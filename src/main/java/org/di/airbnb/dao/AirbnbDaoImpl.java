package org.di.airbnb.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.di.airbnb.assemblers.MessagingLimitedDTO;
import org.di.airbnb.assemblers.UserSubModel;
import org.di.airbnb.dao.entities.Booking;
import org.di.airbnb.dao.entities.Messaging;
import org.di.airbnb.dao.entities.Property;
import org.di.airbnb.dao.entities.Rating;
import org.di.airbnb.dao.entities.RentingRules;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class AirbnbDaoImpl {
	@PersistenceContext
	private EntityManager entityManager;

	public List<Property> getPropertiesByHost( final long hostId ) {
		return entityManager.createQuery( "FROM Property p where hostId = :hostId", Property.class )
				.setParameter( "hostId", hostId )
				.getResultList();
	}

	public List<Booking> getTenantBookings( final long tenantId ) {
		return entityManager.createQuery( "FROM Booking b where tenantId = :tenantId",
				Booking.class ).setParameter( "tenantId", tenantId ).getResultList();
	}

	public List<Rating> getPropertyRatings( final long propertyId ) {
		return entityManager.createQuery( "FROM Rating r where propertyId = :propertyId",
				Rating.class ).setParameter( "propertyId", propertyId ).getResultList();
	}

	public RentingRules getPropertyRentingRules( final long propertyId ) {
		try {
			return entityManager.createQuery( "FROM RentingRules r where propertyId = :propertyId",
					RentingRules.class ).setParameter( "propertyId", propertyId ).getSingleResult();
		} catch ( NoResultException e ) {
			return null;
		}
	}

	//	public List<User> getMany( final List<Long> userIds ) {
	//		TypedQuery<User> query = entityManager.createQuery( "FROM User where id in (:ids)",
	//				User.class ).setParameter( "ids", userIds );
	//		return query.getResultList();
	//	}
	public List<UserSubModel> getLimitedMany( final List<Long> userIds ) {
		TypedQuery<UserSubModel> query = entityManager.createQuery(
				"SELECT NEW org.di.airbnb.airbnb.transferables.UserLimitedDTO(u.username, u.id) FROM User u where id in (:ids)",
				UserSubModel.class ).setParameter( "ids", userIds );
		return query.getResultList();
	}

	@Cacheable("user_limited")
	public Optional<UserSubModel> getUserInfo( final Long userId ) {
		TypedQuery<UserSubModel> query = entityManager.createQuery(
				"SELECT NEW org.di.airbnb.airbnb.transferables.UserLimitedDTO(u.username, u.id) FROM User u where id = :id",
				UserSubModel.class ).setParameter( "id", userId );
		try {
			return Optional.of( query.getSingleResult() );
		} catch ( NoResultException e ) {
			return Optional.empty();
		}
	}

	/* fetch info for recipients that sender=userId has sent messages to */
	public List<MessagingLimitedDTO> getSentInfoBySenderId( final long userId ) {
		TypedQuery<MessagingLimitedDTO> query = entityManager.createQuery(
				"SELECT NEW org.di.airbnb.airbnb.transferables.MessagingLimitedDTO(m.id, u.username, m.createdAt) FROM Messaging m LEFT JOIN User u ON m.recipient = u.id WHERE m.sender = :userId GROUP BY u.id",
				MessagingLimitedDTO.class ).setParameter( "userId", userId );
		return query.getResultList();
	}

	/* fetch info for senders that recipient=userId has received messages from */
	public List<MessagingLimitedDTO> getIncomingInfoByRecipientId( final long userId ) {
		TypedQuery<MessagingLimitedDTO> query = entityManager.createQuery(
				"SELECT NEW org.di.airbnb.airbnb.transferables.MessagingLimitedDTO(m.id, u.username, m.createdAt) FROM Messaging m LEFT JOIN User u ON m.recipient = u.id WHERE m.recipient = :userId GROUP BY u.id",
				MessagingLimitedDTO.class ).setParameter( "userId", userId );
		return query.getResultList();
	}

	public boolean isNewMessage( final long recipientId ) {
		TypedQuery<Long> query = entityManager.createQuery(
				"SELECT COUNT(*) FROM Messaging WHERE readStatus = 0 AND recipient = :recipientId",
				Long.class ).setParameter( "recipientId", recipientId );
		return query.getSingleResult() > 0;
	}

	public List<Messaging> getChatBySenderIdAndRecipientId( final long senderId,
			final long recipientId ) {
		TypedQuery<Messaging> query = entityManager.createQuery(
				"FROM Messaging WHERE recipient = :recipientId and sender = :senderId ORDER BY created_at ASC",
				Messaging.class )
				.setParameter( "recipientId", recipientId )
				.setParameter( "senderId", senderId );
		return query.getResultList();
	}

	@Transactional
	public boolean readChatBySenderIdAndRecipientId( final long senderId, final long recipientId ) {
		String queryString = new StringBuilder().append( "update Messaging " )
				.append( "set read_status = 1 " )
				.append( "where recipient = :recipientId " )
				.append( "and sender = :senderId " )
				.toString();
		Query query = entityManager.createQuery( queryString )
				.setParameter( "recipientId", recipientId )
				.setParameter( "senderId", senderId );
		return query.executeUpdate() > 0;
	}

	public List<Rating> getUserRatings( final long ratedUserId ) {
		TypedQuery<Rating> query = entityManager.createQuery(
				"FROM Rating where rated_user_id = id", Rating.class )
				.setParameter( "id", ratedUserId );
		return query.getResultList();
	}

}
