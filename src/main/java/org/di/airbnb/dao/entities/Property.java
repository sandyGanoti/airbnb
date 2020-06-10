package org.di.airbnb.dao.entities;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.di.airbnb.constant.PropertyType;

@Entity
@Table(name = "property_to_rent")
public class Property {

	private long id;
	private String name;
	private PropertyType propertyType;
	private String country;
	private String city;
	private String district;
	private Long hostId;
	private BigDecimal price;
	private BigDecimal extraPricePerPerson;
	private Integer beds;
	private Integer bedrooms;
	private Integer bathrooms;
	private Integer minimumDays;
	private Integer maximumTenants;
	private Double propertySize;
	private String freeText;
	private Double latitude;
	private Double longitude;

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

	@Column(name = "property_type")
	@Enumerated(EnumType.STRING)
	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType( final PropertyType propertyType ) {
		this.propertyType = propertyType;
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

	public String getDistrict() {
		return district;
	}

	public void setDistrict( final String district ) {
		this.district = district;
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

	@Column(name = "extra_price_per_person")
	public BigDecimal getExtraPricePerPerson() {
		return extraPricePerPerson;
	}

	public void setExtraPricePerPerson( final BigDecimal extraPricePerPerson ) {
		this.extraPricePerPerson = extraPricePerPerson;
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

	public Integer getBathrooms() {
		return bathrooms;
	}

	public void setBathrooms( final Integer bathrooms ) {
		this.bathrooms = bathrooms;
	}

	@Column(name = "maximum_tenants")
	public Integer getMaximumTenants() {
		return maximumTenants;
	}

	public void setMaximumTenants( final Integer maximumTenants ) {
		this.maximumTenants = maximumTenants;
	}

	@Column(name = "minimum_days")
	public Integer getMinimumDays() {
		return minimumDays;
	}

	public void setMinimumDays( final Integer minimumDays ) {
		this.minimumDays = minimumDays;
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

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude( final Double latitude ) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude( final Double longitude ) {
		this.longitude = longitude;
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
		return id == property.id && name.equals(
				property.name ) && propertyType == property.propertyType && country.equals(
				property.country ) && city.equals( property.city ) && district.equals(
				property.district ) && hostId.equals( property.hostId ) && price.equals(
				property.price ) && beds.equals( property.beds ) && bedrooms.equals(
				property.bedrooms ) && bathrooms.equals( property.bathrooms ) && minimumDays.equals(
				property.minimumDays ) && maximumTenants.equals(
				property.maximumTenants ) && propertySize.equals(
				property.propertySize ) && extraPricePerPerson.equals(
				property.extraPricePerPerson ) && freeText.equals(
				property.freeText ) && latitude.equals( property.latitude ) && longitude.equals(
				property.longitude );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, name, propertyType, country, city, district, hostId, price, beds,
				bedrooms, bathrooms, minimumDays, extraPricePerPerson, maximumTenants, propertySize,
				freeText, latitude, longitude );
	}
}
