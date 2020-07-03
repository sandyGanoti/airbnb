package org.di.airbnb.api.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
public class UserCreationRequest {

	@NotBlank
	@Size(min = 1, max = 20)
	private String username;

	@NotBlank
	@Size(min = 3 , max = 40)
	private String password;

	@NotBlank
	@Size(min = 1, max = 40)
	private String firstName;

	@NotBlank
	@Size(min = 1, max = 40)
	private String lastName;

	@NotBlank
	private String phoneNumber;

	private boolean isHost;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

}
