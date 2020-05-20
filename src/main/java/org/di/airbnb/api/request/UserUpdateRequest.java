package org.di.airbnb.api.request;

import javax.validation.constraints.NotNull;

import org.di.airbnb.constant.Role;

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
public class UserUpdateRequest {

	@NotNull
	private long userId;
	@NotNull
	private String username;
	@NotNull
	private String password;
	@NotNull
	private String firstName;
	@NotNull
	private String lastName;
	@NotNull
	private String phoneNumber;
	@NotNull
	private String email;

}
