package org.di.airbnb.dao.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "image_table")
public class Image {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String type;
	private byte[] picByte;
	private long ownerId;

	public Image() {
		super();
	}

	public Image( String name, String type, byte[] picByte ) {
		this.name = name;
		this.type = type;
		this.picByte = picByte;
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
	@Column(name = "picByte", length = 1000)
	public byte[] getPicByte() {
		return picByte;
	}

	public void setPicByte( byte[] picByte ) {
		this.picByte = picByte;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId( final long ownerId ) {
		this.ownerId = ownerId;
	}
}
