package org.di.airbnb.assemblers.homestay;

import java.math.BigDecimal;
import java.time.Instant;

import org.di.airbnb.constant.Role;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

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
@JsonRootName(value = "user")
@Relation(collectionRelation = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomestayModel extends RepresentationModel<HomestayModel> {

	private long id;
	private String name;
	private String country;
	private String city;
	private String region;
	private Long landlordId;
	private BigDecimal price;
	private Integer beds;
	private Integer bedrooms;
	private Integer minimumDays;
	private Integer maximumDays;
	private Double propertySize;
	private String freeText;

}
