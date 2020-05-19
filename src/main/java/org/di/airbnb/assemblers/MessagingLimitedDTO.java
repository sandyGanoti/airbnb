package org.di.airbnb.assemblers;

import java.time.Instant;

public class MessagingLimitedDTO {
	private long id;
	//	private long recipientId;
	private String recipientName;
	private Instant createdAt;

	public MessagingLimitedDTO( long id, String recipientName, Instant createdAt ) {
		this.id = id;
		//		this.recipientId = recipientId;
		this.recipientName = recipientName;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId( final long id ) {
		this.id = id;
	}

	//	public long getRecipientId() {
	//		return recipientId;
	//	}
	//
	//	public void setRecipientId( final long recipientId ) {
	//		this.recipientId = recipientId;
	//	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName( final String recipientName ) {
		this.recipientName = recipientName;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt( final Instant createdAt ) {
		this.createdAt = createdAt;
	}
}
