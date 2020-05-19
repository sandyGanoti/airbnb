package org.di.airbnb.api;

public class Location {

	private String address;
	private String region;
	private String country;

	public String getAddress() {
		return address;
	}

	public void setAddress( final String address ) {
		this.address = address;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion( final String region ) {
		this.region = region;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry( final String country ) {
		this.country = country;
	}
}
