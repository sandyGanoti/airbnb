package org.di.airbnb.dao.entities;

import java.time.Instant;
import java.util.Objects;

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

	@Override
	public boolean equals( final Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final Messaging messaging = (Messaging) o;
		return id == messaging.id && sender == messaging.sender && recipient == messaging.recipient && messageBody
				.equals( messaging.messageBody ) && createdAt.equals(
				messaging.createdAt ) && readStatus.equals( messaging.readStatus );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id, sender, recipient, messageBody, createdAt, readStatus );
	}

	@Override
	public String toString() {
		return "Messaging{" + "id=" + id + ", sender=" + sender + ", recipient=" + recipient + ", messageBody='" + messageBody + '\'' + ", createdAt=" + createdAt + ", readStatus=" + readStatus + '}';
	}
}
