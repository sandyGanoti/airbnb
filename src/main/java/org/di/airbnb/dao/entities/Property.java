package org.di.airbnb.dao.entities;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "property")
public class Property {

	private long id;
	private String name;
	private String country;
	private String city;
	private String region;
	private Long hostId;
	private BigDecimal price;
	private Integer beds;
	private Integer bedrooms;
	private Integer minimumDays;
	private Integer maximumDays;
	private Double propertySize;
	private String freeText;

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

	public String getCountry() {
		return country;
	}

	public void setCountry( final String country ) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity( final String city ) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion( final String region ) {
		this.region = region;
	}

	@Column(name = "host_id")
	public Long getHostId() {
		return hostId;
	}

	public void setHostId( final Long hostId ) {
		this.hostId = hostId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice( final BigDecimal price ) {
		this.price = price;
	}

	public Integer getBeds() {
		return beds;
	}

	public void setBeds( final Integer beds ) {
		this.beds = beds;
	}

	public Integer getBedrooms() {
		return bedrooms;
	}

	public void setBedrooms( final Integer bedrooms ) {
		this.bedrooms = bedrooms;
	}

	@Column(name = "minimum_days")
	public Integer getMinimumDays() {
		return minimumDays;
	}

	public void setMinimumDays( final Integer minimumDays ) {
		this.minimumDays = minimumDays;
	}

	@Column(name = "maximum_days")
	public Integer getMaximumDays() {
		return maximumDays;
	}

	public void setMaximumDays( final Integer maximumDays ) {
		this.maximumDays = maximumDays;
	}

	@Column(name = "property_size")
	public Double getPropertySize() {
		return propertySize;
	}

	public void setPropertySize( final Double propertySize ) {
		this.propertySize = propertySize;
	}

	@Column(name = "free_text")
	public String getFreeText() {
		return freeText;
	}

	public void setFreeText( final String freeText ) {
		this.freeText = freeText;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final Property property = (Property) o;
		return id == property.id && name.equals( property.name ) && country.equals(
				property.country ) && city.equals( property.city ) && region.equals(
				property.region ) && hostId.equals( property.hostId ) && price.equals(
				property.price ) && beds.equals( property.beds ) && bedrooms.equals(
				property.bedrooms ) && minimumDays.equals(
				property.minimumDays ) && maximumDays.equals( property.maximumDays ) && propertySize
				.equals( property.propertySize ) && freeText.equals( property.freeText );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, name, country, city, region, hostId, price, beds, bedrooms,
				minimumDays, maximumDays, propertySize, freeText );
	}

	@Override
	public String toString() {
		return "Property{" + "id=" + id + ", name='" + name + '\'' + ", country='" + country + '\'' + ", city='" + city + '\'' + ", region='" + region + '\'' + ", hostId=" + hostId + ", price=" + price + ", beds=" + beds + ", bedrooms=" + bedrooms + ", minimumDays=" + minimumDays + ", maximumDays=" + maximumDays + ", propertySize=" + propertySize + ", freeText='" + freeText + '\'' + '}';
	}
}
