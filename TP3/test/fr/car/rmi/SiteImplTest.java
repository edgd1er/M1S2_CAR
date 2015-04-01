package fr.car.rmi;

import static org.junit.Assert.assertEquals;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.car.rmi.core.SiteImpl;


public class SiteImplTest {

int port;
	
	@Before
	private void setUp() {
		@SuppressWarnings("unused")
		int port=2100;
	}
	
	@Test
	public void testGetFils() {
		SiteImpl rmi, filsUn=null;
		try {
			rmi = new SiteImpl("site1",port);
			filsUn = new SiteImpl("site3",port);
			rmi.addSite(filsUn);
			ArrayList<SiteImpl> fils = new ArrayList<SiteImpl>();
			fils.add(filsUn);
			assertEquals(fils, rmi.getListOfNode());

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddFils() {
		SiteImpl rmi;
		try {
			rmi = new SiteImpl("site1",port);
	
		List<SiteImpl> lesFils = new ArrayList<SiteImpl>();
		SiteImpl filsUn = new SiteImpl("siteFils",port);
		lesFils.add(filsUn);
		rmi.addSite(filsUn);
		assertEquals(lesFils, rmi.getChildNodes());
		
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetName() {
		try {
		SiteImpl rmi = new SiteImpl("site1",port);
		assertEquals("site1", rmi.getName());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	//set fathernode not yet implemented -> this test will not work.
	public void testgetFatherNode() {
		try{
		SiteImpl rmi = new SiteImpl("site1",port);
		SiteImpl rmiPere = new SiteImpl("site pere",port);
		rmiPere.addSite(rmi);
		assertEquals(rmiPere, rmi.getFatherNode());
		}
		 catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
