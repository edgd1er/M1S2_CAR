package fr.car.rmi.core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Serveur allows to send and reveive a message from an RMI Object
 * @author   Emeline SALOMON & Francois DUBIEZ
 */
public interface SiteItf extends Remote {

	/**
	 * Method allow to send a message
	 * 
	 * @param MessageItf
	 *            Message to transmit
	 */
	public void send(MessageItf messageItf) throws RemoteException;

	/***
	 * Method allow to receive a message
	 * 
	 * @param messageItf
	 */
	public void receive(MessageItf messageItf) throws RemoteException;

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

	public void clearSites() throws RemoteException;
	
	/**
	 * return list of sons
	 * 
	 * @return list of Sons
	 * @throws RemoteException
	 */
	public List<SiteItf> getListOfNode() throws RemoteException;
	
	
	/**
	 * Set a Father to the node
	 * @return
	 * @uml.property  name="fatherNode"
	 */
	public void setFatherNode(SiteItf father) throws RemoteException;


	/**
	 * Set a Father to the node
	 * 
	 * @return
	 */
	public void removeFatherNode() throws RemoteException;

	public void clearFatherNodes() throws RemoteException;

	
	/**
	 * return a Father to the node.
	 * @return
	 * @uml.property  name="fatherNode"
	 * @uml.associationEnd  
	 */
	public SiteItf getFatherNode() throws RemoteException;


	/**
	 * A node can store received message
	 * 
	 * @return
	 * @throws RemoteException
	 */
	List<MessageItf> getReceivedMessages() throws RemoteException;

	/**
	 * quit, unreference the current instance from its nodes
	 */
	public void quit() throws RemoteException;

}
