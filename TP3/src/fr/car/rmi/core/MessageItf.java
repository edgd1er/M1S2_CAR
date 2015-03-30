package fr.car.rmi.core;

import java.util.UUID;

/**
 * is a class used to avoid cyclic messages, each Message has an unique ID.
 * 
 * @author Emeline SALOMON & Francois DUBIEZ
 * 
 */
public interface MessageItf {

	/**
	 * Get message if
	 * 
	 * @return UUID
	 */
	UUID getUUID();

	/**
	 * Useful to get the content of the message
	 * 
	 * @return message content
	 */
	String getContent();

	/**
	 * Useful to get the sender of the message
	 * 
	 * @return sender name
	 */
	SiteItf getSender();
}