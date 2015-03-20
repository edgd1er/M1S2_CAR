package fr.car.rmi;


/**
 * is a class used to avoid cyclic messages, each Message has an unique ID.
 * 
 * @author
 * 
 */
public interface Message {
	String getContent();

	SiteItf getSender();
}