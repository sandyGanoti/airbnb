package org.di.airbnb.assemblers.property;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyWithRentingRules {

	private PropertyModel propertyModel;
	private RentingRulesModel rentingRulesModel;
	private List<AvailableDate> availableDates;


}
