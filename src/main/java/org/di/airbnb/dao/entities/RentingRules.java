package org.di.airbnb.dao.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "renting_rules")
public class RentingRules implements Serializable {

	private long id;
	private long propertyId;
	private Boolean aircondition;
	private Boolean tv;
	private Boolean internet;
	private Boolean livingRoom;
	private Boolean kitchen;
	private Boolean partyFriendly;
	private Boolean petFriendly;
	private Boolean smokingFriendly;
	private String freeText;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId( final long id ) {
		this.id = id;
	}

	@Column( name = "property_id")
	public long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId( final long propertyId ) {
		this.propertyId = propertyId;
	}

	@Column(name = "aircondition")
	public Boolean getAircondition() {
		return aircondition;
	}

	public void setAircondition( final Boolean aircondition ) {
		this.aircondition = aircondition;
	}

	@Column(name = "tv")
	public Boolean getTv() {
		return tv;
	}

	public void setTv( final Boolean tv ) {
		this.tv = tv;
	}

	@Column(name = "internet")
	public Boolean getInternet() {
		return internet;
	}

	public void setInternet( final Boolean internet ) {
		this.internet = internet;
	}

	@Column(name = "living_room")
	public Boolean getLivingRoom() {
		return livingRoom;
	}

	public void setLivingRoom( final Boolean livingRoom ) {
		this.livingRoom = livingRoom;
	}

	@Column(name = "kitchen")
	public Boolean getKitchen() {
		return kitchen;
	}

	public void setKitchen( final Boolean kitchen ) {
		this.kitchen = kitchen;
	}

	@Column(name = "party_friendly")
	public Boolean getPartyFriendly() {
		return partyFriendly;
	}

	public void setPartyFriendly( final Boolean partyFriendly ) {
		this.partyFriendly = partyFriendly;
	}

	@Column(name = "pet_friendly")
	public Boolean getPetFriendly() {
		return petFriendly;
	}

	public void setPetFriendly( final Boolean petFriendly ) {
		this.petFriendly = petFriendly;
	}

	@Column(name = "smoking_friendly")
	public Boolean getSmokingFriendly() {
		return smokingFriendly;
	}

	public void setSmokingFriendly( final Boolean smokingFriendly ) {
		this.smokingFriendly = smokingFriendly;
	}

	@Column(name = "free_text")
	public String getFreeText() {
		return freeText;
	}

	public void setFreeText( final String freeText ) {
		this.freeText = freeText;
	}

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final RentingRules that = (RentingRules) o;
		return aircondition == that.aircondition && tv == that.tv && internet == that.internet && livingRoom == that.livingRoom && kitchen == that.kitchen && partyFriendly == that.partyFriendly && petFriendly == that.petFriendly && smokingFriendly == that.smokingFriendly && propertyId == that.propertyId && freeText
				.equals( that.freeText );
	}

	@Override
	public int hashCode() {
		return Objects.hash( propertyId, aircondition, tv, internet, livingRoom, kitchen,
				partyFriendly, petFriendly, smokingFriendly, freeText );
	}

	@Override
	public String toString() {
		return "RentingRules{" + "propertyId=" + propertyId + ", aircondition=" + aircondition + ", tv=" + tv + ", internet=" + internet + ", livingRoom=" + livingRoom + ", kitchen=" + kitchen + ", partyFriendly=" + partyFriendly + ", petFriendly=" + petFriendly + ", smokingFriendly=" + smokingFriendly + ", freeText='" + freeText + '\'' + '}';
	}
}