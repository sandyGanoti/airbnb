package org.di.airbnb.api.request;

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

	private String password;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;

}
