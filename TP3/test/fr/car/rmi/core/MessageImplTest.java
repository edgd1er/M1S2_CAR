package fr.car.rmi.core;

import static org.junit.Assert.*;

import java.rmi.RemoteException;

import org.junit.Test;

public class MessageImplTest {

	@Test
	public void testGetContent() {
		try {
			MessageImpl message = new MessageImpl("Contenu du message", new SiteImpl("Nom"));
			
			assertEquals("Contenu du message", message.getContent());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetSender() {
		try {
			SiteImpl site = new SiteImpl("Nom");
			MessageImpl message = new MessageImpl("Contenu du message", site);
			
			assertEquals(site, message.getSender());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testEqualsObject() {
		try {
			SiteImpl site = new SiteImpl("Nom");
			MessageImpl messageUn = new MessageImpl("Contenu du message", site);
			MessageImpl messageDeux = messageUn;
			
			assertEquals(true, messageUn.equals(messageDeux));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}

}
