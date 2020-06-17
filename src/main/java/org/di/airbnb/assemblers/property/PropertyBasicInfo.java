package org.di.airbnb.assemblers.property;

import org.di.airbnb.assemblers.image.ImageModel;

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
@JsonRootName(value = "popularplaces")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyBasicInfo {

	private long id;
	private ImageModel image;
	private double price;
	private String country;
	private String city;
	private String district;
	private double meanRating;

}
