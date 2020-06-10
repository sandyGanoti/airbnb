package org.di.airbnb.api.response;

import java.math.BigDecimal;

import org.di.airbnb.assemblers.image.ImageModel;
import org.di.airbnb.assemblers.property.PropertyModel;
import org.di.airbnb.dao.entities.Image;

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
@JsonRootName(value = "rating")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResult {

	private PropertyModel propertyModel;
	private Double meanRating;

}
