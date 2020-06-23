package org.di.airbnb.assemblers.booking;

import java.util.Date;

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
	private Date fromDatetime;
	private Date toDatetime;
	private Date createdAt;
	private String propertyName;
}
