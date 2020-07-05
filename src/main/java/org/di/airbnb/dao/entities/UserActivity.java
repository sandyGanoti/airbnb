package org.di.airbnb.dao.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_activity")
public class UserActivity implements Serializable {

	private long id;
	private long userId;
	private long propertyId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId( final long id ) {
		this.id = id;
	}

	@Column(name = "user_id")
	public long getUserId() {
		return userId;
	}

	public void setUserId( final long userId ) {
		this.userId = userId;
	}

	@Column(name = "property_id")
	public long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId( final long propertyId ) {
		this.propertyId = propertyId;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final UserActivity that = (UserActivity) o;
		return id == that.id && userId == that.userId && propertyId == that.propertyId;
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, userId, propertyId );
	}

	@Override
	public String toString() {
		return "UserActivity{" + "id=" + id + ", userId=" + userId + ", propertyId=" + propertyId + '}';
	}
}
