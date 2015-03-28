package fr.car.rmi.core;

/**
 * is a class used to avoid cyclic messages, each Message has an unique ID.
 * 
 * @author Emeline SALOMON & Francois DUBIEZ
 * 
 */
public interface Message {
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