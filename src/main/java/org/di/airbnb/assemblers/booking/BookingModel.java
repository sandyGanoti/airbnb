package org.di.airbnb.assemblers.booking;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

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
public class BookingModel {

	private long id;
	private long tenantId;
	private long propertyId;
	@DateTimeFormat(pattern = "YYYY-MM-DD")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date fromDatetime;
	@DateTimeFormat(pattern = "YYYY-MM-DD")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date toDatetime;
	private Date createdAt;
	private String propertyName;
}
