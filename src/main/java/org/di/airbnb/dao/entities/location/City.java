package org.di.airbnb.dao.entities.location;

import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "city")
public class City {
	private long id;
	private String name;
	private Set<District> districts;

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

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "city_id")
	@JsonIgnoreProperties("districts")
	public Set<District> getDistricts() {
		return districts;
	}

	public void setDistricts( final Set<District> districts ) {
		this.districts = districts;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final City city = (City) o;
		return id == city.id && name.equals( city.name ) && districts.equals( city.districts );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, name, districts );
	}
}
