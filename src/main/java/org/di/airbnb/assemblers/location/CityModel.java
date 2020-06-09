package org.di.airbnb.assemblers.location;

import java.util.Set;

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
@JsonRootName(value = "city")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityModel {

	private long id;
	private String name;
	private Set<DistrictModel> districts;

}
