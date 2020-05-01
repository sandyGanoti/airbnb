package org.di.airbnb.assemblers;

public class UserLimitedDTO {

	private Long id;
	private String username;

	public UserLimitedDTO(String username, Long id) {
		this.username = username;
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId( final Long id ) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername( final String username ) {
		this.username = username;
	}
}
