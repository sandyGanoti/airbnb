package org.di.airbnb.assemblers.property;

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
@JsonRootName(value = "renting_rules")
public class RentingRulesModel {

	private long id;
	private Boolean aircondition;
	private Boolean tv;
	private Boolean internet;
	private Boolean livingRoom;
	private Boolean kitchen;
	private Boolean partyFriendly;
	private Boolean petFriendly;
	private Boolean smokingFriendly;
	private String freeText;

}
