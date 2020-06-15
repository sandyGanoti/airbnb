package org.di.airbnb.assemblers.property;

import java.time.Instant;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableDate {

	@DateTimeFormat(pattern = "YYYY-MM-DD")
	@Temporal(value= TemporalType.TIMESTAMP)
	private Date availableFrom;

	@Temporal(value=TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "YYYY-MM-DD")
	private Date availableTo;

}
