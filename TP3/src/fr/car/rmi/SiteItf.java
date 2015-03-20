package fr.car.rmi;

/**
 * Serveur allows to send and reveive a message from an RMI Object
 * 
 * @author Emeline SALOMON & Francois DUBIEZ
 * 
 */
public interface SiteItf {

	/**
	 * Method allow to send a message
	 * 
	 * @param sender
	 * @param maxId
	 */
	public void send(ObjetRMI sender, String message);

	/***
	 * Method allow to receive a message
	 * 
	 * @param sender
	 * @param message
	 */
	public void receive(ObjetRMI sender, String message);

}
