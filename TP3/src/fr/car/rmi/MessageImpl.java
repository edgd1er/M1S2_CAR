package fr.car.rmi;

import java.io.Serializable;
import java.util.UUID;


public class MessageImpl implements Message, Serializable {
	private final String content;
	private final SiteItf sender;
	private final UUID id = UUID.randomUUID();

	public MessageImpl(final String content, final SiteItf sender) {
		super();
		this.content = content;
		this.sender = sender;
	}

	/**
	 * @see #getContent()
	 */
	@Override
	public String getContent() {
		return this.content;
	}

	/**
	 * @see #getSender()
	 */
	@Override
	public SiteItf getSender() {
		return this.sender;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MessageImpl)) {
			return false;
		}
		final MessageImpl other = (MessageImpl) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}
}