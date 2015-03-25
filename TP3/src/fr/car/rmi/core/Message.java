package fr.car.rmi.core;



/**
 * is a class used to avoid cyclic messages, each Message has an unique ID.
 * 
* @author Emeline SALOMON & Francois DUBIEZ
 * 
 */
public interface Message {
	/**
	 * 
	 * @return message content
	 */
	String getContent();

	/**
	 *  
	 * @return sender name 
	 */
	SiteItf getSender();
}