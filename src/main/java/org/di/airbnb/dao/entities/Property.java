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
	private long countryId;
	private long cityId;
	private long districtId;
	private long hostId;
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
	private Boolean historic;

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

	@Column(name = "country_id")
	public long getCountryId() {
		return countryId;
	}

	public void setCountryId( final long countryId ) {
		this.countryId = countryId;
	}

	@Column(name = "city_id")
	public long getCityId() {
		return cityId;
	}

	public void setCityId( final long cityId ) {
		this.cityId = cityId;
	}

	@Column(name = "district_id")
	public long getDistrictId() {
		return districtId;
	}

	public void setDistrictId( final long districtId ) {
		this.districtId = districtId;
	}

	@Column(name = "host_id")
	public long getHostId() {
		return hostId;
	}

	public void setHostId( final long hostId ) {
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

	public Boolean isHistoric() {
		return historic;
	}

	public void setHistoric( final Boolean historic ) {
		this.historic = historic;
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
		return id == property.id && countryId == property.countryId && cityId == property.cityId && districtId == property.districtId && hostId == property.hostId && name
				.equals( property.name ) && propertyType == property.propertyType && price.equals(
				property.price ) && extraPricePerPerson.equals(
				property.extraPricePerPerson ) && beds.equals( property.beds ) && bedrooms.equals(
				property.bedrooms ) && bathrooms.equals( property.bathrooms ) && minimumDays.equals(
				property.minimumDays ) && maximumTenants.equals(
				property.maximumTenants ) && propertySize.equals(
				property.propertySize ) && freeText.equals( property.freeText ) && latitude.equals(
				property.latitude ) && longitude.equals( property.longitude ) && historic.equals(
				property.historic );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, name, propertyType, countryId, cityId, districtId, hostId, price,
				extraPricePerPerson, beds, bedrooms, bathrooms, minimumDays, maximumTenants,
				propertySize, freeText, latitude, longitude, historic );
	}

	@Override
	public String toString() {
		return "Property{" + "id=" + id + ", name='" + name + '\'' + ", propertyType=" + propertyType + ", countryId=" + countryId + ", cityId=" + cityId + ", districtId=" + districtId + ", hostId=" + hostId + ", price=" + price + ", extraPricePerPerson=" + extraPricePerPerson + ", beds=" + beds + ", bedrooms=" + bedrooms + ", bathrooms=" + bathrooms + ", minimumDays=" + minimumDays + ", maximumTenants=" + maximumTenants + ", propertySize=" + propertySize + ", freeText='" + freeText + '\'' + ", latitude=" + latitude + ", longitude=" + longitude + ", historic=" + historic + '}';
	}
}
