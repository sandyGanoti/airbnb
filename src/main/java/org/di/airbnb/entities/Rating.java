package org.di.airbnb.entities;

import java.time.Instant;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.di.airbnb.constant.Role;

@Entity()
@Table(name = "rating")
public class Rating {

	private long raterId;
	private long homestayId;
	private int rating;
	private Instant createdAt;

	public long getRaterId() {
		return raterId;
	}

	public void setRaterId( final long raterId ) {
		this.raterId = raterId;
	}

	public long getHomestayId() {
		return homestayId;
	}

	public void setHomestayId( final long homestayId ) {
		this.homestayId = homestayId;
	}

	public int getRating() {
		return rating;
	}

	public void setRating( final int rating ) {
		this.rating = rating;
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
		final Rating rating1 = (Rating) o;
		return raterId == rating1.raterId && homestayId == rating1.homestayId && rating == rating1.rating && createdAt
				.equals( rating1.createdAt );
	}

	@Override
	public int hashCode() {
		return Objects.hash( raterId, homestayId, rating, createdAt );
	}

	@Override
	public String toString() {
		return "Rating{" + "raterId=" + raterId + ", homestayId=" + homestayId + ", rating=" + rating + ", createdAt=" + createdAt + '}';
	}
}
