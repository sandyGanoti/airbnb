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
@JsonRootName(value = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingModel {

	private long id;
	private long tenantId;
	private long homestayId;
	private Instant fromDatetime;
	private Instant toDatetime;
	private Instant createdAt;

}
