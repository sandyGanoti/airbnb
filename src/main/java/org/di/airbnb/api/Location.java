package org.di.airbnb.api;

public class Location {

	private String country;
	private String city;
	private String district;

	public String getCountry() {
		return country;
	}

	public void setCountry( final String country ) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity( final String city ) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict( final String district ) {
		this.district = district;
	}
}
