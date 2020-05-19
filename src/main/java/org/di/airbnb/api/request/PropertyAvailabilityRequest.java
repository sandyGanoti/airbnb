package org.di.airbnb.api.request;

import java.time.Instant;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.di.airbnb.api.Location;

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
public class PropertyAvailabilityRequest {

	@NotNull
	private Location location;
	@NotNull
	@Future
	private Instant from;
	@NotNull
	@Future
	private Instant to;
	@Positive
	private int numberOfPeople;

}
