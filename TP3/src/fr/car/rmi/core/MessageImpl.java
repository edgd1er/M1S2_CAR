package fr.car.rmi.core;

import java.io.Serializable;
import java.util.UUID;


/**
 * Message implementation: this is the element that will be send to node. It contains a message and a sender name.
 * @author   Emeline SALOMON & Francois DUBIEZ
 */
public class MessageImpl implements MessageItf, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6462432972907426065L;
	private final UUID id = UUID.randomUUID();
	private final String content;
	/**
	 * @uml.property  name="sender"
	 * @uml.associationEnd  
	 */
	private SiteItf sender;


	public MessageImpl(final String content, final SiteItf sender) {
		super();
		this.content = content;
		this.sender = sender;
	}

	/**
	 * @see  #getContent()
	 * @uml.property  name="content"
	 */
	@Override
	public String getContent() {
		return this.content;
	}

	/**
	 * @see  #getSender()
	 * @uml.property  name="sender"
	 */
	@Override
	public SiteItf getSender() {
		return this.sender;
	}

	
	
	/**
	 * @param  sender
	 * @uml.property  name="sender"
	 */
	public void setSender(SiteItf sender) {
		this.sender = sender;
	}

	/**
	 * @return
	 * @uml.property  name="id"
	 */
	public UUID getId() {
		return id;
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

	@Override
	public UUID getUUID() {
		return this.id;
	}
}