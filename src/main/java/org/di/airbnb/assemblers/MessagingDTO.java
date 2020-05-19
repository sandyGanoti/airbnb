package org.di.airbnb.assemblers;

import java.time.Instant;

public class MessagingDTO {

	private Long id;
	private Long sender;
	private Long recipient;
	private String messageBody;
	private Instant createdAt;

	public static Builder builder() {
		return new Builder();
	}

	public Long getId() {
		return id;
	}

	public void setId( final Long id ) {
		this.id = id;
	}

	public Long getSender() {
		return sender;
	}

	public void setSender( final Long sender ) {
		this.sender = sender;
	}

	public Long getRecipient() {
		return recipient;
	}

	public void setRecipient( final Long recipient ) {
		this.recipient = recipient;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody( final String messageBody ) {
		this.messageBody = messageBody;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt( final Instant createdAt ) {
		this.createdAt = createdAt;
	}

	public static class Builder {
		private MessagingDTO tmp = new MessagingDTO();

		public Builder id( Long id ) {
			tmp.setId( id );
			return this;
		}

		public Builder sender( Long sender ) {
			tmp.setSender( sender );
			return this;
		}

		public Builder recipient( Long recipient ) {
			tmp.setRecipient( recipient );
			return this;
		}

		public Builder messageBody( String messageBody ) {
			tmp.setMessageBody( messageBody );
			return this;
		}

		public Builder createdAt( Instant createdAt ) {
			tmp.setCreatedAt( createdAt );
			return this;
		}

		public MessagingDTO build() {
			MessagingDTO messagingDTO = new MessagingDTO();
			messagingDTO.setId( tmp.getId() );
			messagingDTO.setSender( tmp.getSender() );
			messagingDTO.setRecipient( tmp.getRecipient() );
			messagingDTO.setMessageBody( tmp.getMessageBody() );
			messagingDTO.setCreatedAt( tmp.getCreatedAt() );

			return messagingDTO;
		}
	}

}
