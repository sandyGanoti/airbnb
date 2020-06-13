package org.di.airbnb.dao.entities;

import java.util.Arrays;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 Reference to https://medium.com/@rameez.s.shaikh/upload-and-retrieve-images-using-spring-boot-angular-8-mysql-18c166f7bc98
*/
@Entity
@Table(name = "image_table")
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String type;
	private String typetheid;
	private byte[] picture;

	public Image() {
		super();
	}

	public Image( final String name, final String type, final String imageOwnerId,
			final byte[] picture ) {
		this.name = name;
		this.type = type;
		this.typetheid = imageOwnerId;
		this.picture = picture;
	}

	public Long getId() {
		return id;
	}

	public void setId( final Long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

	//image bytes can have large lengths so we specify a value
	//which is more than the default length for picByte column
	@Column(name = "picture", length = 1000)
	public byte[] getPicture() {
		return picture;
	}

	public void setPicture( byte[] picture ) {
		this.picture = picture;
	}

	public String gettTpetheid() {
		return typetheid;
	}

	public void setTypetheid( final String imageOwnerId ) {
		this.typetheid = imageOwnerId;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final Image image = (Image) o;
		return id.equals( image.id ) && typetheid == image.typetheid && name.equals(
				image.name ) && type.equals( image.type ) && Arrays.equals( picture,
				image.picture );
	}

	@Override
	public int hashCode() {
		int result = Objects.hash( id, name, type, typetheid );
		result = 31 * result + Arrays.hashCode( picture );
		return result;
	}
}
