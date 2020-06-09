package org.di.airbnb.dao.entities.location;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "district")
public class District {
	private long id;
	private String name;
	private long cityId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId( final long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( final String name ) {
		this.name = name;
	}

	@Column(name = "city_id")
	public long getCityId() {
		return cityId;
	}

	public void setCityId( final long cityId ) {
		this.cityId = cityId;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final District district = (District) o;
		return id == district.id && name.equals( district.name ) && cityId == district.cityId;
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, name, cityId );
	}
}
