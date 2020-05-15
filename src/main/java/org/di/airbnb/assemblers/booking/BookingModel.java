package org.di.airbnb.assemblers.booking;

import java.time.Instant;

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
@JsonRootName(value = "booking")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingModel {

	private long id;
	private long tenantId;
	private long propertyId;
	private Instant fromDatetime;
	private Instant toDatetime;
	private Instant createdAt;

}
