package fr.car.rmi.core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


/**
 * Serveur allows to send and reveive a message from an RMI Object
 * 
 * @author Emeline SALOMON & Francois DUBIEZ
 * 
 */
public interface SiteItf extends Remote {

	/**
	 * Method allow to send a message
	 * 
	 * @param Message
	 * 		Message to transmit
	 */
	public void send(Message message) throws RemoteException;

	/***
	 * Method allow to receive a message
	 * 
	 * @param message
	 */
	public void receive(Message message) throws RemoteException;
	
	/**
	 * Get SiteName
	 * 
	 * @return
	 * @throws RemoteException
	 * 		
	 */
	String getName() throws RemoteException;
	

	/**
	 * Add a child node.
	 * 
	 * @param node
	 * 		Child node
	 * @throws RemoteException
	 * 		Remote site not accessible
	 */
	public void addSite(SiteItf node) throws RemoteException;
	
	/**
	 * A node can store received message
	 * 
	 * @return
	 * @throws RemoteException
	 */
	List<Message> getReceivedMessages() throws RemoteException;
	
}
