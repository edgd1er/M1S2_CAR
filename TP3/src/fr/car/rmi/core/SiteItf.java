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
	 *            Message to transmit
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
	 *            Child node
	 * @throws RemoteException
	 *             Remote site not accessible
	 */
	public void addSite(SiteItf node) throws RemoteException;

	/**
	 * remove a child node.
	 * 
	 * @param node
	 *            Child node
	 * @throws RemoteException
	 *             Remote site not accessible
	 */
	public void removeSite(SiteItf node) throws RemoteException;

	/**
	 * return list of sons
	 * 
	 * @return list of Sons
	 * @throws RemoteException
	 */
	public List<SiteItf> getListOfNode() throws RemoteException;
	
	
	/**
	 * Set a Father to the node
	 * 
	 * @return
	 */
	public void setFatherNode(SiteItf father);

	/**
	 * Set a Father to the node
	 * 
	 * @return
	 */
	public void removeFatherNode();

	
	/**
	 * return a Father to the node.
	 * 
	 * @return
	 */
	public SiteItf getFatherNode();


	/**
	 * A node can store received message
	 * 
	 * @return
	 * @throws RemoteException
	 */
	List<Message> getReceivedMessages() throws RemoteException;

	/**
	 * quit, unreference the current instance from its nodes
	 */
	public void quit() throws RemoteException;

}
