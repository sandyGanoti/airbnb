package org.di.airbnb.assemblers.user;

import javax.validation.constraints.NotBlank;
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
public class UserModel {

	@NotNull
	private long id;
	@NotBlank
	private String username;
	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;
	@NotBlank
	private String phoneNumber;
	@NotBlank
	private Role role;
	@NotBlank
	private String email;

}
