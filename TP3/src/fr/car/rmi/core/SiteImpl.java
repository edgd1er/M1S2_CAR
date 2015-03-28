package fr.car.rmi.core;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Server interface allow to send and receive a message from an RMI Object
 * 
 * @author Emeline SALOMON & Francois DUBIEZ
 * 
 */
public class SiteImpl implements SiteItf, Serializable {

	private static final long serialVersionUID = 3642112121032722800L;
	private final String name;
	private final List<SiteItf> childNodes;
	private SiteItf FatherNode;
	private final List<Message> receivedMessages;

	public SiteImpl(final String name) throws RemoteException {
		this.name = name;
		this.childNodes = new ArrayList<SiteItf>();
		this.FatherNode = null;
		this.receivedMessages = new ArrayList<Message>();
	}

	/**
	 * Allow to send a message
	 */
	@Override
	public void send(Message message) {

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
	 * Allow to receive a message
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

	/**
	 * Allow to had a child
	 */
	@Override
	public void addSite(SiteItf node) throws RemoteException {
		if (!this.childNodes.contains(node)) {
			this.childNodes.add(node);
			System.out.println(this.name + " added " + node.getName()
					+ " as a child.");
		}

	}

	/**
	 * Return a list of the message that has been received
	 */
	@Override
	public List<Message> getReceivedMessages() throws RemoteException {
		return this.receivedMessages;
	}

	/**
	 * Return the father node
	 * 
	 * @return
	 */
	public SiteItf getFatherNode() {
		return FatherNode;
	}

	public List<SiteItf> getChildNodes() {
		return childNodes;
	}
	
	public void setFatherNode(SiteItf father) {
		this.FatherNode = father;
	}

}
