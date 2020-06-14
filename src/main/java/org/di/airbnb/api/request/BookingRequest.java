package org.di.airbnb.api.request;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

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
public class BookingRequest {

	@NotNull
	@DateTimeFormat(pattern = "dd.MM.yy")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Instant from;
	@NotNull
	@DateTimeFormat(pattern = "dd.MM.yy")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Instant to;
	@NotNull
	private Integer numberOfPeople;
	@NotNull
	private Long propertyId;
}
