package org.di.airbnb.dao.entities;

import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "rating")
public class Rating {

	private long id;
	private long raterId;
	private long propertyId;
	private long hostId;
	private String text;
	private int mark;
	private Instant createdAt;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId( final long id ) {
		this.id = id;
	}

	@Column(name = "rater_id")
	public long getRaterId() {
		return raterId;
	}

	public void setRaterId( final long raterId ) {
		this.raterId = raterId;
	}

	@Column(name = "property_id")
	public long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId( final long propertyId ) {
		this.propertyId = propertyId;
	}

	@Column(name = "host_id")
	public long getHostId() {
		return hostId;
	}

	public void setHostId( final long hostId ) {
		this.hostId = hostId;
	}

	public String getText() {
		return text;
	}

	public void setText( final String text ) {
		this.text = text;
	}

	public int getMark() {
		return mark;
	}

	public void setMark( final int mark ) {
		this.mark = mark;
	}

	@Column(name = "created_at")
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
		final Rating rating = (Rating) o;
		return id == rating.id && raterId == rating.raterId && propertyId == rating.propertyId && hostId == rating.hostId && mark == rating.mark && text
				.equals( rating.text ) && createdAt.equals( rating.createdAt );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, raterId, propertyId, hostId, text, mark, createdAt );
	}

	@Override
	public String toString() {
		return "Rating{" + "id=" + id + ", raterId=" + raterId + ", propertyId=" + propertyId + ", hostId=" + hostId + ", text='" + text + '\'' + ", mark=" + mark + ", createdAt=" + createdAt + '}';
	}
}
