package org.di.airbnb.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.di.airbnb.api.request.SearchRequest;
import org.di.airbnb.dao.entities.Booking;
import org.di.airbnb.dao.entities.Image;
import org.di.airbnb.dao.entities.Messaging;
import org.di.airbnb.dao.entities.Property;
import org.di.airbnb.dao.entities.Rating;
import org.di.airbnb.dao.entities.RentingRules;
import org.di.airbnb.dao.entities.location.City;
import org.di.airbnb.dao.entities.location.District;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Repository
public class AirbnbDaoImpl {
	@Autowired
	PlatformTransactionManager platformTransactionManager;
	@PersistenceContext
	private EntityManager entityManager;

	public Optional<Property> getPropertyByHost( final long hostId ) {
		try {
			return Optional.of( entityManager.createQuery( "FROM Property p where hostId = :hostId",
					Property.class ).setParameter( "hostId", hostId ).getSingleResult() );
		} catch ( NoResultException e ) {
			return Optional.empty();
		}
	}

	public List<Image> getPropertyImages( final long propertyId ) {
		return entityManager.createQuery( "FROM Image i where typetheid = :propertyId",
				Image.class )
				.setParameter( "propertyId", String.valueOf( propertyId ) )
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

	@Transactional
	public void saveAvatar( final Image image ) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(
				platformTransactionManager );
		transactionTemplate.execute( new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult( TransactionStatus status ) {
				//				entityManager.createNativeQuery("TRUNCATE TABLE MyTable).executeUpdate();
				entityManager.persist( image );
			}
		} );

		//		try {
		//			transaction = entityManager.getTransaction();
		//			transaction.begin();
		//			entityManager.persist( image );
		//
		//		} catch ( RuntimeException e ) {
		//			if ( transaction != null && transaction.isActive() ) {
		//				transaction.rollback();
		//			}
		//			throw e;
		//		}
	}

	@Transactional
	public void updateAvatar( final Image image ) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(
				platformTransactionManager );
		transactionTemplate.execute( new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult( TransactionStatus status ) {
				entityManager.createQuery(
						"UPDATE Image set picture = :picture, name = :name, type = :type where typetheid = :userId" )
						.setParameter( "picture", image.getPicture() )
						.setParameter( "type", image.getType() )
						.setParameter( "name", image.getName() )
						.setParameter( "userId", image.gettTpetheid() )
						.executeUpdate();
				//				entityManager.createNativeQuery("TRUNCATE TABLE MyTable).executeUpdate();
			}
		} );
	}

	public Optional<Image> getAvatar( final long userId ) {
		try {
			return Optional.of( entityManager.createQuery( "FROM Image  where typetheid = :userId",
					Image.class )
					.setParameter( "userId", String.valueOf( userId ) )
					.getSingleResult() );
		} catch ( NoResultException e ) {
			return Optional.empty();
		}
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

	public List<Messaging> getMessages( final long userId ) {
		TypedQuery<Messaging> query = entityManager.createQuery(
				"FROM Messaging WHERE recipient = :userId OR sender = :userId", Messaging.class )
				.setParameter( "userId", userId );
		return query.getResultList();
	}

	public List<Property> getPropertiesBySearchQuery( final SearchRequest searchRequest ) {
		List<Long> availablePropertyIds = entityManager.createQuery(
				"SELECT pa.propertyId FROM PropertyAvailability pa WHERE (:fromDate BETWEEN pa.availableFrom and pa.availableTo) and (:toDate BETWEEN pa.availableFrom and  pa.availableTo)",
				Long.class )
				.setParameter( "fromDate", searchRequest.getFrom() )
				.setParameter( "toDate", searchRequest.getTo() )
				.getResultList();
		if ( availablePropertyIds.isEmpty() ) {
			return Collections.emptyList();
		}

		TypedQuery<Long> query = entityManager.createQuery(
				"SELECT notBooked.propertyId FROM Booking notBooked WHERE notBooked.propertyId NOT IN (SELECT b.propertyId from Booking b WHERE (b.fromDatetime >= :fromDate and b.toDatetime <= :toDate) or (:fromDate >= b.fromDatetime and :toDate <= b.toDatetime) or (:toDate >= b.fromDatetime and :fromDate <= b.toDatetime) )",
				Long.class )
				.setParameter( "fromDate", searchRequest.getFrom() )
				.setParameter( "toDate", searchRequest.getTo() );

		/* not booked property ids */
		List<Long> propertyIds = query.getResultList();
		if ( propertyIds.isEmpty() ) {
			return Collections.emptyList();
		}
		List<Long> ids = new ArrayList<>();
		propertyIds.forEach( propertyId -> {
			if ( availablePropertyIds.contains( propertyId ) ) {
				ids.add( propertyId );
			}
		} );
		return entityManager.createQuery( "FROM Property p WHERE p.id IN :propertyIds",
				Property.class ).setParameter( "propertyIds", ids ).getResultList();

		//TODO: FIRST CHECK from the availability and collect the ids from there
		// TODO: Also check the other filters as well!

		//TODO: SAME CHECK should be done on booking process as well!
	}

}
