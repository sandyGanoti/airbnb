package org.di.airbnb.api.request;

import java.time.Instant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.di.airbnb.dao.Pagination;

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
public class SearchRequest {

	@NotNull
	private Instant from;
	@NotNull
	private Instant to;
	@NotNull
	private Integer numberOfPeople;
	@NotBlank
	private String country;
	@NotBlank
	private String city;
	@NotBlank
	private String district;
	@NotNull
	private Pagination pagination;
}
