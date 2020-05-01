package org.di.airbnb.assemblers;

import java.time.Instant;

public class UserDTO {

	private Long id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String country;
	private Instant createdAt;
	private String email;

	public static Builder builder() {
		return new Builder();
	}

	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername( final String username ) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( final String password ) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName( final String firstName ) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName( final String lastName ) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber( final String phoneNumber ) {
		this.phoneNumber = phoneNumber;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry( final String country ) {
		this.country = country;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt( final Instant createdAt ) {
		this.createdAt = createdAt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail( final String email ) {
		this.email = email;
	}

	public static class Builder {
		private UserDTO tmp = new UserDTO();

		public Builder id( Long id ) {
			tmp.setId( id );
			return this;
		}

		public Builder username( final String username ) {
			tmp.setUsername( username );
			return this;
		}

		public Builder password( final String password ) {
			tmp.setPassword( password );
			return this;
		}

		public Builder firstName( final String firstName ) {
			tmp.setFirstName( firstName );
			return this;
		}

		public Builder lastName( final String lastName ) {
			tmp.setLastName( lastName );
			return this;
		}

		public Builder phoneNumber( final String phoneNumber ) {
			tmp.setPhoneNumber( phoneNumber );
			return this;
		}

		public Builder country( final String country ) {
			tmp.setCountry( country );
			return this;
		}

		public Builder email( final String email ) {
			tmp.setEmail( email );
			return this;
		}

		public Builder createdAt( final Instant createdAt ) {
			tmp.setCreatedAt( createdAt );
			return this;
		}

		public UserDTO build() {
			UserDTO userDTO = new UserDTO();
			userDTO.setId( tmp.getId() );
			userDTO.setUsername( tmp.getUsername() );
			userDTO.setCountry( tmp.getCountry() );
			userDTO.setCreatedAt( tmp.getCreatedAt() );
			userDTO.setEmail( tmp.getEmail() );
			userDTO.setFirstName( tmp.getFirstName() );
			userDTO.setLastName( tmp.getLastName() );
			userDTO.setPassword( tmp.getPassword() );
			userDTO.setPhoneNumber( tmp.getPhoneNumber() );

			return userDTO;
		}
	}

}
