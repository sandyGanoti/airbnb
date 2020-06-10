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

	@DateTimeFormat(pattern = "dd.MM.yy")
	@Temporal(value= TemporalType.TIMESTAMP)
	private Date from;

	@Temporal(value=TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd.MM.yy")
	private Date to;

}
