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
	 * 
	 * @return message content
	 */
	String getContent() ;

	/**
	 *  
	 * @return sender name 
	 */
	SiteItf getSender();
}