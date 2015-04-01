package fr.car.rmi;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import org.junit.*;

import fr.car.rmi.core.MessageImpl;
import fr.car.rmi.core.MessageItf;
import fr.car.rmi.core.SiteImpl;

public class testSite {

	@Test
	public void sendMessageToHimSelf(int port) throws RemoteException {

		final SiteImpl sender = new SiteImpl("Site1",port);
		final MessageItf m = new MessageImpl("Mon contenu", sender);

		sender.send(m);
		assertEquals(m.getContent(), sender.getReceivedMessages().get(0)
				.getContent());
	}

	/**
	 * Vérifie que les enfants recoivent les messages de son parent
	 * 
	 * @throws RemoteException
	 */
	@Test
	public void sendMessageToOther(int port) throws RemoteException {

		final SiteImpl sender = new SiteImpl("Site1",port);
		final SiteImpl receiver = new SiteImpl("Site2",port);
		sender.addSite(receiver);
		final MessageItf m = new MessageImpl("Mon contenu", sender);

		sender.send(m);
		assertEquals(m.getContent(), receiver.getReceivedMessages().get(0)
				.getContent());
	}

	/**
	 * Vérifie que les parents ne recoivent pas les messages des enfants
	 * 
	 * @throws RemoteException
	 */
	@Test
	public void sendMessageParentNotReceiveMessage(int port) throws RemoteException {

		final SiteImpl parent = new SiteImpl("Site1",port);
		final SiteImpl children = new SiteImpl("Site2",port);
		parent.addSite(children);
		final MessageItf m = new MessageImpl("Mon contenu", children);

		children.send(m);
		assertEquals(0, parent.getReceivedMessages().size());
	}

	/**
	 * Vérifie que les parents ne recoivent pas les messages des enfants
	 * 
	 * @throws RemoteException
	 */
	@Test
	public void sendMessageChildrenReceive(int port) throws RemoteException {

		final SiteImpl parent = new SiteImpl("parent",port);
		final SiteImpl parentChildren = new SiteImpl("parentChildren",port);
		final SiteImpl children = new SiteImpl("children",port);
		parent.addSite(parentChildren);
		parentChildren.addSite(children);

		final MessageItf m = new MessageImpl("Mon contenu", parent);

		parent.send(m);
		assertEquals(m.getContent(), parentChildren.getReceivedMessages()
				.get(0).getContent());
		assertEquals(m.getContent(), children.getReceivedMessages().get(0)
				.getContent());
	}

}
