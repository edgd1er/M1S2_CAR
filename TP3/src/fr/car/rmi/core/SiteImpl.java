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
 * @author   Emeline SALOMON & Francois DUBIEZ
 */
public class SiteImpl extends UnicastRemoteObject implements SiteItf,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3642112121032722800L;
	private final String name;
	private final List<SiteItf> childNodes;
	/**
	 * @uml.property  name="fatherNode"
	 * @uml.associationEnd  
	 */
	private SiteItf FatherNode;
	private final List<MessageItf> receivedMessages;
	private Registry myReg;

	public SiteImpl(final String name, Integer port) throws RemoteException {
		super();
		this.name = name;
		this.childNodes = new ArrayList<SiteItf>();
		this.FatherNode = null;
		this.receivedMessages = new ArrayList<MessageItf>();
		if (myReg == null) {
			myReg = LocateRegistry.getRegistry(port);
		}
		myReg.rebind(this.name, this);
		System.out.println("Node " + this.getName() + " has been registered");
	}

	@Override
	public void send(MessageItf message) throws RemoteException {

		boolean contained = false;
		String alreadySendUUID = "";

		synchronized (this.receivedMessages) {
			for (MessageItf m : this.receivedMessages) {
				if (m.getUUID().equals(message.getUUID())) {
					contained = true;
					alreadySendUUID = message.getUUID().toString();
				}
			}
		}

		if (!contained) {
			this.receivedMessages.add(message);
			try {
				this.receive(message);

				for (SiteItf child : this.childNodes) {
					// inner class to run thread thread
					class myThread extends Thread {

						private SiteItf child;
						private MessageItf message;

						public myThread(SiteItf child, MessageItf message) {
							this.child = child;
							this.message = message;
						}

						public void run() {
							try {
								child.send(message);
							} catch (RemoteException e) {
								try {
									System.err.println("Error, node " + child.getName() + " did not send its message!!!");
								} catch (RemoteException e1) {
								}
								e.printStackTrace();
							}
						}

					}
					myThread t = new myThread(child, message);
					t.start();
					
					System.out.println("node " + this.getName()
							+ " sent a message(" + message.getContent()
							+ ") to " + child.getName());
				}
			} catch (RemoteException e) {
				throw new RuntimeException("Unable to send message to node ", e);
			}
		} else {
			System.out.println("Already treated message on node :"
					+ this.getName() + " " + message.getUUID().toString()
					+ " = " + alreadySendUUID);
		}
	}

	/**
	 * 
	 * 
	 */
	@Override
	public void receive(MessageItf message) throws RemoteException {

		System.out.println("Node " + this.getName() + " has just received "
				+ " a message: '" + message.getContent() + "' initiated by "
				+ message.getSender().getName() + ".");
	}

	/**
	 * Return node's name
	 * @uml.property  name="name"
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

	public void clearSites() throws RemoteException {
		this.childNodes.clear();

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
	 * @return
	 * @throws RemoteException
	 * @uml.property  name="receivedMessages"
	 */
	@Override
	public List<MessageItf> getReceivedMessages() throws RemoteException {
		return this.receivedMessages;
	}

	/**
	 * set father node
	 * @return
	 * @uml.property  name="fatherNode"
	 */
	public void setFatherNode(SiteItf father) throws RemoteException {
		this.FatherNode = father;
	}

	/**
	 * remove father node
	 * 
	 * @return
	 */
	public void removeFatherNode() throws RemoteException {
		this.FatherNode = null;
	}

	/**
	 * @return
	 * @uml.property  name="fatherNode"
	 */
	public SiteItf getFatherNode() throws RemoteException {
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

	@Override
	public void clearFatherNodes() throws RemoteException {
		this.FatherNode = null;

	}

}
