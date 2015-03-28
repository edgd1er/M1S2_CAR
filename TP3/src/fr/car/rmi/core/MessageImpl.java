package fr.car.rmi.core;

import java.io.Serializable;
import java.util.UUID;

/**
 * Message implementation: this is the element that will be send to node. It
 * contains a message and a sender name.
 * 
 * @author Emeline SALOMON & Francois DUBIEZ
 *
 */
public class MessageImpl implements Message, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6462432972907426065L;
	private final UUID id = UUID.randomUUID();
	private final String content;
	private final SiteItf sender;

	public MessageImpl(final String content, final SiteItf sender) {
		super();
		this.content = content;
		this.sender = sender;
	}

	/**
	 * Useful to get the content of the message
	 * 
	 * @see #getContent()
	 */
	@Override
	public String getContent() {
		return this.content;
	}

	/**
	 * Useful to get the sender of the message
	 * 
	 * @see #getSender()
	 */
	@Override
	public SiteItf getSender() {
		return this.sender;
	}

	/**
	 * Hash the content of the message
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	/**
	 * Allows to know if 2 objects are equals or not
	 */
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