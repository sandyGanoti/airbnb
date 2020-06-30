package org.di.airbnb.api.request;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.di.airbnb.constant.PropertyType;
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

	@NotNull
	private Long countryId;

	@NotNull
	private Long cityId;

	@NotNull
	private Long districtId;

	@NotNull
	private Pagination pagination;

	@NotNull
	private PropertyType propertyType;
}
