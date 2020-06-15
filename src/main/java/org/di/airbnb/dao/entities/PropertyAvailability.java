package org.di.airbnb.dao.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "property_availability")
public class PropertyAvailability {

	private long id;
	private long propertyId;
	private Date availableFrom;
	private Date availableTo;

	public PropertyAvailability(){}

	public PropertyAvailability( final long propertyId, final Date availableFrom,
			final Date availableTo ) {
		this.propertyId = propertyId;
		this.availableFrom = availableFrom;
		this.availableTo = availableTo;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}

	@Column(name = "property_id")
	public long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId( final long propertyId ) {
		this.propertyId = propertyId;
	}

	@Column(name = "available_from")
	public Date getAvailableFrom() {
		return availableFrom;
	}

	public void setAvailableFrom( final Date availableFrom ) {
		this.availableFrom = availableFrom;
	}

	@Column(name = "available_to")
	public Date getAvailableTo() {
		return availableTo;
	}

	public void setAvailableTo( final Date availableTo ) {
		this.availableTo = availableTo;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final PropertyAvailability that = (PropertyAvailability) o;
		return id == that.id && propertyId == that.propertyId && availableFrom.equals(
				that.availableFrom ) && availableTo.equals( that.availableTo );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, propertyId, availableFrom, availableTo );
	}

	@Override
	public String toString() {
		return "PropertyAvailability{" + "id=" + id + ", propertyId=" + propertyId + ", availableFrom=" + availableFrom + ", availableTo=" + availableTo + '}';
	}
}
