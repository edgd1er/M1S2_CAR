package fr.car.rmi;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.car.rmi.core.SiteImpl;
import fr.car.rmi.core.SiteItf;


public class ObjetRMITest {

int port;
	
	@Before
	private void setUp() {
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
		ObjetRMI rmi = new ObjetRMI();
		List<ObjetRMI> fils = new ArrayList<ObjetRMI>();
		ObjetRMI filsUn = new ObjetRMI();
		fils.add(filsUn);
		rmi.setFils(fils);
		
		assertEquals(fils, rmi.getFils());
	}

	@Test
	public void testGetOneFils() {
		ObjetRMI rmiPere = new ObjetRMI();
		List<ObjetRMI> fils = new ArrayList<ObjetRMI>();
		ObjetRMI rmi = null;
		ObjetRMI filsUn = new ObjetRMI(2, rmi, null);
		fils.add(filsUn);
		rmi = new ObjetRMI(1, rmiPere, fils);
		
		assertEquals(filsUn, rmi.getOneFils(0));
	}

	@Test
	public void testGetIdObjet() {
		ObjetRMI rmiPere = new ObjetRMI();
		List<ObjetRMI> fils = new ArrayList<ObjetRMI>();
		ObjetRMI rmi = null;
		ObjetRMI filsUn = new ObjetRMI(2, rmi, null);
		fils.add(filsUn);
		rmi = new ObjetRMI(1, rmiPere, fils);
		
		assertEquals(1, rmi.getIdObjet());
	}

	@Test
	public void testGetPere() {
		ObjetRMI rmiPere = new ObjetRMI();
		List<ObjetRMI> fils = new ArrayList<ObjetRMI>();
		ObjetRMI rmi = null;
		ObjetRMI filsUn = new ObjetRMI(2, rmi, null);
		fils.add(filsUn);
		rmi = new ObjetRMI(1, rmiPere, fils);
		
		assertEquals(rmiPere, rmi.getPere());
	}

	@Test
	public void testSetIdObjet() {
		ObjetRMI rmi = new ObjetRMI();
		rmi.setIdObjet(1);
		
		assertEquals(1, rmi.getIdObjet());
	}

	@Test
	public void testSetPere() {
		ObjetRMI rmiPere = new ObjetRMI();
		ObjetRMI rmi = new ObjetRMI();
		rmi.setPere(rmiPere);
		
		assertEquals(rmiPere, rmi.getPere());
	}

	@Test
	public void testSetFils() {
		ObjetRMI rmi = new ObjetRMI();
		List<ObjetRMI> fils = new ArrayList<ObjetRMI>();
		ObjetRMI filsUn = new ObjetRMI();
		fils.add(filsUn);
		
		rmi.setFils(fils);
		
		assertEquals(fils, rmi.getFils());
	}

}
