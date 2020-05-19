package org.di.airbnb.dao.entities;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bid")
public class Booking implements Serializable {

	private long id;
	private long tenantId;
	private long propertyId;
	private Instant fromDatetime;
	private Instant toDatetime;
	private Instant createdAt;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId( final long id ) {
		this.id = id;
	}

	public long getTenantId() {
		return tenantId;
	}

	public void setTenantId( final long tenantId ) {
		this.tenantId = tenantId;
	}

	public long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId( final long propertyId ) {
		this.propertyId = propertyId;
	}

	public Instant getFromDatetime() {
		return fromDatetime;
	}

	public void setFromDatetime( final Instant fromDatetime ) {
		this.fromDatetime = fromDatetime;
	}

	public Instant getToDatetime() {
		return toDatetime;
	}

	public void setToDatetime( final Instant toDatetime ) {
		this.toDatetime = toDatetime;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt( final Instant createdAt ) {
		this.createdAt = createdAt;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final Booking booking = (Booking) o;
		return id == booking.id && tenantId == booking.tenantId && propertyId == booking.propertyId && fromDatetime
				.equals( booking.fromDatetime ) && toDatetime.equals(
				booking.toDatetime ) && createdAt.equals( booking.createdAt );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, tenantId, propertyId, fromDatetime, toDatetime, createdAt );
	}

	@Override
	public String toString() {
		return "Booking{" + "id=" + id + ", tenantId=" + tenantId + ", propertyId=" + propertyId + ", fromDatetime=" + fromDatetime + ", toDatetime=" + toDatetime + ", createdAt=" + createdAt + '}';
	}
}
