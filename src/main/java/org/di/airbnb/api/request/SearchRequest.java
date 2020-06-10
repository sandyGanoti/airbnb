package org.di.airbnb.api.request;

import java.time.Instant;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.di.airbnb.dao.Pagination;
import org.springframework.format.annotation.DateTimeFormat;

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
	@DateTimeFormat(pattern = "dd.MM.yy")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date from;
	@NotNull
	@DateTimeFormat(pattern = "dd.MM.yy")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date to;
	@NotNull
	private Integer numberOfPeople;
	@NotBlank
	private long countryId;
	@NotBlank
	private long cityId;
	@NotBlank
	private long districtId;
	@NotNull
	private Pagination pagination;
}
