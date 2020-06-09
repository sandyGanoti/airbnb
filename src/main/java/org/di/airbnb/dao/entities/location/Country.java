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
@Table(name = "country")
public class Country {
	private long id;
	private String name;
	private Set<City> cities;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( final String name ) {
		this.name = name;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "country_id")
	@JsonIgnoreProperties("cities")
	public Set<City> getCities() {
		return cities;
	}

	public void setCities( final Set<City> cities ) {
		this.cities = cities;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final Country country = (Country) o;
		return id == country.id && name.equals( country.name );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, name );
	}
}
