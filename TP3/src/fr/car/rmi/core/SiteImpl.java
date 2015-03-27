package fr.car.rmi.core;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Server interface allow to send and receive a message from an RMI Object
 * 
 * @author Emeline SALOMON & Francois DUBIEZ
 * 
 */
public class SiteImpl extends UnicastRemoteObject  implements SiteItf, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3642112121032722800L;
	private final String name;
	private final List<SiteItf> childNodes;
	private SiteItf FatherNode;
	private final List<Message> receivedMessages;
	private Registry myReg;

	public SiteImpl(final String name, Integer port) throws RemoteException {
		super();
		this.name = name;
		this.childNodes = new ArrayList<SiteItf>();
		this.FatherNode = null;
		this.receivedMessages = new ArrayList<Message>();
		if (myReg == null) {
			myReg = LocateRegistry.getRegistry(port);
		}
		myReg.rebind(this.name, this);
		System.out.println("Node " + this.getName() + " has been registered");
	}

	@Override
	public void send(Message message)  throws RemoteException {

		synchronized (this.receivedMessages) {
			if (this.receivedMessages.contains(message)) {
				return;
			}
		}

		this.receivedMessages.add(message);
		try {
			this.receive(message);

			for (final SiteItf child : this.childNodes) {
				if (!child.equals(message.getSender())) {
					child.send(message);
					System.out.println(this.getName() + " sent a message("
							+ message.getContent() + ") to " + child.getName());
				}
			}
		} catch (RemoteException e) {
			throw new RuntimeException("Unable to send message to node ", e);

		}
	}

	/**
	 * 
	 * 
	 */
	@Override
	public void receive(Message message) throws RemoteException {

		System.out.println("Le noeud " + message.getSender().getName()
				+ " vient de recevoir le message '" + message + "'.");
	}

	/**
	 * Return node's name
	 * 
	 */
	@Override
	public String getName() throws RemoteException {
		return this.name;
	}

	/***
	 * Add a child node
	 * 
	 */
	@Override
	public void addSite(SiteItf node) throws RemoteException {
		if (!this.childNodes.contains(node)) {
			this.childNodes.add(node);
			System.out.println(this.name + " added " + node.getName()
					+ " as a child.");
		}

	}

	@Override
	public void removeSite(SiteItf node) throws RemoteException {
		this.childNodes.remove(node);

	}

	/**
	 * return list of child node.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public List<SiteItf> getListOfNode() throws RemoteException {
		return this.childNodes;
	}

	/**
	 * Get all received messages
	 * 
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public List<Message> getReceivedMessages() throws RemoteException {
		return this.receivedMessages;
	}

	/**
	 * set father node
	 * 
	 * @return
	 */
	public void setFatherNode(SiteItf father)  throws RemoteException {
		this.FatherNode = father;
	}

	/**
	 * remove father node
	 * 
	 * @return
	 */
	public void removeFatherNode()  throws RemoteException {
		this.FatherNode = null;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public SiteItf getFatherNode()  throws RemoteException {
		return FatherNode;
	}

	/**
	 * Quit after having informed childs recreated the links and remmoved self
	 * from registry
	 * 
	 * @throws RemoteException
	 */
	public void quit() throws RemoteException {
		try {

			for (SiteItf son : this.childNodes) {
				// relink all child to this node's father
				son.setFatherNode(this.getFatherNode());
				// link fathernode to all new child
				this.getFatherNode().addSite(son);
			}
			// remove this node from father child list.
			this.getFatherNode().removeSite(this);
			this.myReg.unbind(this.getName());

		} catch (AccessException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
