package fr.car.rmi.core;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SiteImplTest {

	int port;
	
	@Before
	private void setUp() {
		int port=2100;
	}
	
	@Test
	public void testGetName() {
		try {
			SiteImpl site = new SiteImpl("Nom",port);
			
			assertEquals("Nom", site.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddSite() {
		try {
			SiteImpl site = new SiteImpl("Nom",port);
			SiteImpl siteDeux = new SiteImpl("Deuxième site",port);
			site.addSite(siteDeux);
			
			List<SiteItf> list = new ArrayList<SiteItf>();
			list.add(siteDeux);
			
			assertEquals(list, site.getChildNodes());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetFatherNode() {
		try {
			SiteImpl site = new SiteImpl("Nom",port);
			SiteImpl father = new SiteImpl("Père",port);
			site.setFatherNode(father);
			
			assertEquals(father, site.getFatherNode());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
