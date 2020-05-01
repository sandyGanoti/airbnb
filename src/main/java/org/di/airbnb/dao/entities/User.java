package org.di.airbnb.dao.entities;

import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.di.airbnb.constant.Role;

@Entity
@Table(name = "user",
		uniqueConstraints = { @UniqueConstraint(name = "email", columnNames = { "email" }),
							  @UniqueConstraint(name = "username", columnNames = { "username" }) })
public class User {

	private long id;
	private String password;
	private String firstName;
	private String lastName;
	private String username;
	private String phoneNumber;
	private Role role;
	private Instant createdAt;
	private String email;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId( final long id ) {
		this.id = id;
	}

	@Column(length = 60)
	public String getPassword() {
		return password;
	}

	public void setPassword( final String password ) {
		this.password = password;
	}

	@Column(name = "first_name")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName( final String firstName ) {
		this.firstName = firstName;
	}

	@Column(name = "last_name")
	public String getLastName() {
		return lastName;
	}

	public void setLastName( final String lastName ) {
		this.lastName = lastName;
	}

	@Column(name = "username")
	public String getUsername() {
		return username;
	}

	public void setUsername( final String username ) {
		this.username = username;
	}

	@Column(name = "phone_number")
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber( final String phoneNumber ) {
		this.phoneNumber = phoneNumber;
	}

	public Role getRole() {
		return role;
	}

	public void setRole( final Role role ) {
		this.role = role;
	}

	@Column(name = "created_at")
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

	@Override
	public String toString() {
		return "User{" + "id=" + id + ", password='" + password + '\'' + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", username='" + username + '\'' + ", phoneNumber='" + phoneNumber + '\'' + ", role=" + role + ", createdAt=" + createdAt + ", email='" + email + '\'' + '}';
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final User user = (User) o;
		return id == user.id && password.equals( user.password ) && firstName.equals(
				user.firstName ) && lastName.equals( user.lastName ) && username.equals(
				user.username ) && phoneNumber.equals(
				user.phoneNumber ) && role == user.role && createdAt.equals(
				user.createdAt ) && email.equals( user.email );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, password, firstName, lastName, username, phoneNumber, role,
				createdAt, email );
	}
}
