package org.di.airbnb.assemblers.property;

import org.di.airbnb.dao.entities.Property;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RentingRulesModel {

	private long id;
	private boolean aircondition;
	private boolean tv;
	private boolean internet;
	private boolean livingRoom;
	private boolean kitchen;
	private boolean partyFriendly;
	private boolean petFriendly;
	private boolean smokingFriendly;
	private String freeText;

}
