package fr.car.rmi.core;

import static org.junit.Assert.assertEquals;

import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

public class MessageImplTest {

	int port;
	
	@Before
	private void setUp() {
		@SuppressWarnings("unused")
		int port=2100;
	}
	
	
	@Test
	public void testGetContent() {
		try {
			MessageImpl message = new MessageImpl("Contenu du message", new SiteImpl("Nom",port));
			
			assertEquals("Contenu du message", message.getContent());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetSender() {
		try {
			SiteImpl site = new SiteImpl("Nom",port);
			MessageImpl message = new MessageImpl("Contenu du message", site);
			
			assertEquals(site, message.getSender());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	
	@Test
	public void testEqualsObject() {
		try {
			SiteImpl site = new SiteImpl("Nom",port);
			MessageImpl messageUn = new MessageImpl("Contenu du message", site);
			MessageImpl messageDeux = messageUn;
			
			assertEquals(true, messageUn.equals(messageDeux));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}

}
