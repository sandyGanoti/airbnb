package org.di.airbnb.api.response;

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
@JsonRootName(value = "jwtresponse")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private long userId;

	public JwtResponse( String accessToken, long userId ) {
		this.token = accessToken;
		this.userId = userId;
	}

}