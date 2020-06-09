package org.di.airbnb.assemblers.property;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;

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
@JsonRootName(value = "property_to_rent")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyModel {

	@NotNull
	private long id;
	@NotBlank
	private String name;
	@NotBlank
	private String propertyType;
	@NotBlank
	private String country;
	@NotBlank
	private String city;
	@NotBlank
	private String region;
	@NotNull
	private Long hostId;
	@NotNull
	private BigDecimal price;
	@NotNull
	private Integer beds;
	@NotNull
	private Integer bedrooms;
	@NotNull
	private Integer minimumDays;
	@NotNull
	private Integer maximumDays;
	@NotNull
	private Double propertySize;

	private String freeText;

	@NotNull
	private Double latitude;

	@NotNull
	private Double longitude;

}
