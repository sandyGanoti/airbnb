package org.di.airbnb.dao;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.di.airbnb.api.response.SearchResult;
import org.di.airbnb.dao.entities.Booking;
import org.di.airbnb.dao.entities.Messaging;
import org.di.airbnb.dao.entities.Property;
import org.di.airbnb.dao.entities.Rating;
import org.di.airbnb.dao.entities.RentingRules;
import org.di.airbnb.dao.entities.location.City;
import org.di.airbnb.dao.entities.location.District;
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

	public List<Rating> getHostRatings( final long userId ) {
		return entityManager.createQuery( "FROM Rating r where hostId = :userId", Rating.class )
				.setParameter( "userId", userId )
				.getResultList();
	}

	public List<Property> getPopularPlaces( final long userId ) {
		return entityManager.createQuery( "FROM Property p", Property.class ).getResultList();
	}

	public List<Booking> getPropertyBookingsByUser( final long userId, final long propertyId ) {
		return entityManager.createQuery(
				"FROM Booking b where tenantId = :userId and propertyId = :propertyId",
				Booking.class )
				.setParameter( "userId", userId )
				.setParameter( "propertyId", propertyId )
				.getResultList();
	}

	public RentingRules getPropertyRentingRules( final long propertyId ) {
		try {
			return entityManager.createQuery( "FROM RentingRules r where propertyId = :propertyId",
					RentingRules.class ).setParameter( "propertyId", propertyId ).getSingleResult();
		} catch ( NoResultException e ) {
			return null;
		}
	}

	public List<City> getCitiesByCountry( final long countryId ) {
		return entityManager.createQuery( "FROM City c where countryId = :countryId", City.class )
				.setParameter( "countryId", countryId )
				.getResultList();

	}

	public List<District> getDistrictsByCity( final long cityId ) {
		return entityManager.createQuery( "FROM District d where cityId = :cityId", District.class )
				.setParameter( "cityId", cityId )
				.getResultList();

	}

	public List<Messaging> getNewMessages( final long recipientId ) {
		TypedQuery<Messaging> query = entityManager.createQuery(
				"FROM Messaging WHERE readStatus = 0 AND recipient = :recipientId",
				Messaging.class ).setParameter( "recipientId", recipientId );
		return query.getResultList();
	}

	public List<Property> getPropertiesBySearchQuery( final Date from, final Date to,
			final int numberOfPeople, final Pagination pagination ) {
		return entityManager.createQuery(
				"FROM Property p INNER JOIN Booking b ON b.propertyId = p.id WHERE :from > b.toDatetime AND :to < b.fromDatetime AND p.maximumTenants >= :numberOfPeople ",
				Property.class )
				.setParameter( "numberOfPeople", numberOfPeople )
				.setParameter( "from", from )
				.setParameter( "to", to )
				.getResultList();
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

}
