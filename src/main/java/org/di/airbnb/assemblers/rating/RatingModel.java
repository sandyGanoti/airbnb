package org.di.airbnb.assemblers.rating;

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
public class RatingModel {

	private long id;
	private long raterId;
	private long hostId;
	private String text;
	private long propertyId;
	private int mark;

}
