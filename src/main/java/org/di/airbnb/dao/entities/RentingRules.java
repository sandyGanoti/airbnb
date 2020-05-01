package org.di.airbnb.dao.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "renting_rules")
public class RentingRules implements Serializable {

	private long homestayId;
	private boolean airdcondition;
	private boolean tv;
	private boolean internet;
	private boolean livingRoom;
	private boolean kitchen;
	private boolean partyFriendly;
	private boolean petFriendly;
	private boolean smokingFriendly;
	private String freeText;

	@Column(name = "homestay_id")
	public long getHomestayId() {
		return homestayId;
	}

	public void setHomestayId( final long homestayId ) {
		this.homestayId = homestayId;
	}

	public boolean hasAirdcondition() {
		return airdcondition;
	}

	public void setAirdcondition( final boolean airdcondition ) {
		this.airdcondition = airdcondition;
	}

	public boolean hasTv() {
		return tv;
	}

	public void setTv( final boolean tv ) {
		this.tv = tv;
	}

	public boolean hasInternet() {
		return internet;
	}

	public void setInternet( final boolean internet ) {
		this.internet = internet;
	}

	@Column(name = "living_room")
	public boolean hasLivingRoom() {
		return livingRoom;
	}

	public void setLivingRoom( final boolean livingRoom ) {
		this.livingRoom = livingRoom;
	}

	public boolean hasKitchen() {
		return kitchen;
	}

	public void setKitchen( final boolean kitchen ) {
		this.kitchen = kitchen;
	}

	@Column(name = "party_friendly")
	public boolean isPartyFriendly() {
		return partyFriendly;
	}

	public void setPartyFriendly( final boolean partyFriendly ) {
		this.partyFriendly = partyFriendly;
	}

	@Column(name = "pet_friendly")
	public boolean isPetFriendly() {
		return petFriendly;
	}

	public void setPetFriendly( final boolean petFriendly ) {
		this.petFriendly = petFriendly;
	}

	@Column(name = "smoking_friendly")
	public boolean isSmokingFriendly() {
		return smokingFriendly;
	}

	public void setSmoking( final boolean smokingFriendly ) {
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
		return airdcondition == that.airdcondition && tv == that.tv && internet == that.internet && livingRoom == that.livingRoom && kitchen == that.kitchen && partyFriendly == that.partyFriendly && petFriendly == that.petFriendly && smokingFriendly == that.smokingFriendly && homestayId == that.homestayId && freeText
				.equals( that.freeText );
	}

	@Override
	public int hashCode() {
		return Objects.hash( homestayId, airdcondition, tv, internet, livingRoom, kitchen,
				partyFriendly, petFriendly, smokingFriendly, freeText );
	}

	@Override
	public String toString() {
		return "RentingRules{" + "homestayId=" + homestayId + ", airdcondition=" + airdcondition + ", tv=" + tv + ", internet=" + internet + ", livingRoom=" + livingRoom + ", kitchen=" + kitchen + ", partyFriendly=" + partyFriendly + ", petFriendly=" + petFriendly + ", smokingFriendly=" + smokingFriendly + ", freeText='" + freeText + '\'' + '}';
	}
}