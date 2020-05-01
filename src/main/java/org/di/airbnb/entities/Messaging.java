package org.di.airbnb.entities;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "messaging")
public class Messaging {

	private long id;
	private long sender;
	private long recipient;
	private String messageBody;
	private Instant createdAt;
	private Boolean readStatus;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Column(name = "message_body")
	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody( final String messageBody ) {
		this.messageBody = messageBody;
	}

	@Column(name = "created_at")
	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt( final Instant createdAt ) {
		this.createdAt = createdAt;
	}

	@Column(name = "read_status")
	public Boolean getReadStatus() {
		return readStatus;
	}

	public void setReadStatus( final Boolean readStatus ) {
		this.readStatus = readStatus;
	}
}
