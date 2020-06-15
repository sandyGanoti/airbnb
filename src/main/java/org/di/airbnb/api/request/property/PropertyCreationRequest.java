package org.di.airbnb.api.request.property;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.di.airbnb.assemblers.property.AvailableDate;
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
public class PropertyCreationRequest {
	private long id;

	@NotBlank
	@Size(min = 1, max = 20)
	private String name;
	@NotNull
	private PropertyType propertyType;
	@NotNull
	private int countryId;
	@NotNull
	private int cityId;
	@NotNull
	private int districtId;
	@NotNull
	private BigDecimal price;
	@NotNull
	private BigDecimal extraPricePerPerson;
	@NotNull
	private Integer beds;
	@NotNull
	private Integer bedrooms;
	@NotNull
	private Integer bathrooms;
	@NotNull
	private Integer minimumDays;
	@NotNull
	private Integer maximumTenants;
	@NotNull
	private Double propertySize;
	@NotBlank
	private String freeText;
	@NotNull
	private Boolean aircondition;
	@NotNull
	private Boolean tv;
	@NotNull
	private Boolean internet;
	@NotNull
	private Boolean livingRoom;
	@NotNull
	private Boolean kitchen;
	@NotNull
	private Boolean partyFriendly;
	@NotNull
	private Boolean petFriendly;
	@NotNull
	private Boolean smokingFriendly;
	@NotNull
	private String extraFreeText;
	@NotNull
	private Double longitude;
	@NotNull
	private Double latitude;
	@NotNull
	private List<AvailableDate> availableDates;

}
