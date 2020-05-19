package org.di.airbnb.assemblers.user;

import javax.validation.constraints.NotNull;

import org.di.airbnb.constant.Role;

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
@JsonRootName(value = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModel {

	@NotNull
	private long id;
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
	private Role role;
	@NotNull
	private String email;

}
