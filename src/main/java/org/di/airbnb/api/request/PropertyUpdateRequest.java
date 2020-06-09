package org.di.airbnb.api.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.di.airbnb.constant.PropertyType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PropertyUpdateRequest {

	private String name;
	private PropertyType propertyType;
	private String country;
	private String city;
	private String district;
	private BigDecimal price;
	private Integer beds;
	private Integer bedrooms;
	private Integer bathrooms;
	private Integer minimumDays;
	private Integer maximumTenants;
	private Double propertySize;
	private String freeText;
	private Boolean aircondition;
	private Boolean tv;
	private Boolean internet;
	private Boolean livingRoom;
	private Boolean kitchen;
	private Boolean partyFriendly;
	private Boolean petFriendly;
	private Boolean smokingFriendly;
	private String extraFreeText;
	private Double longitude;
	private Double latitude;

}
