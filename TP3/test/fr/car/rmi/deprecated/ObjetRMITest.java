package fr.car.rmi.deprecated;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.car.rmi.deprecated.ObjetRMI;

public class ObjetRMITest {

	@Test
	public void testGetFils() {
		ObjetRMI rmi = new ObjetRMI();
		List<ObjetRMI> fils = new ArrayList<ObjetRMI>();
		ObjetRMI filsUn = new ObjetRMI();
		fils.add(filsUn);
		rmi.setFils(fils);
		
		assertEquals(fils, rmi.getFils());
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
