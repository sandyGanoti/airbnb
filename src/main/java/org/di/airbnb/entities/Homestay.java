package org.di.airbnb.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "homestay")
public class Homestay {

	private long id;
	private String name;
	private String country;
	private String city;
	private String region;
	private Long landlordId;
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

	@Column(name = "landlord_id")
	public Long getLandlordId() {
		return landlordId;
	}

	public void setLandlordId( final Long landlordId ) {
		this.landlordId = landlordId;
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
		final Homestay homestay = (Homestay) o;
		return id == homestay.id && name.equals( homestay.name ) && country.equals(
				homestay.country ) && city.equals( homestay.city ) && region.equals(
				homestay.region ) && landlordId.equals( homestay.landlordId ) && price.equals(
				homestay.price ) && beds.equals( homestay.beds ) && bedrooms.equals(
				homestay.bedrooms ) && minimumDays.equals(
				homestay.minimumDays ) && maximumDays.equals( homestay.maximumDays ) && propertySize
				.equals( homestay.propertySize ) && freeText.equals( homestay.freeText );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, name, country, city, region, landlordId, price, beds, bedrooms,
				minimumDays, maximumDays, propertySize, freeText );
	}

	@Override
	public String toString() {
		return "Homestay{" + "id=" + id + ", name='" + name + '\'' + ", country='" + country + '\'' + ", city='" + city + '\'' + ", region='" + region + '\'' + ", landlordId=" + landlordId + ", price=" + price + ", beds=" + beds + ", bedrooms=" + bedrooms + ", minimumDays=" + minimumDays + ", maximumDays=" + maximumDays + ", propertySize=" + propertySize + ", freeText='" + freeText + '\'' + '}';
	}
}
