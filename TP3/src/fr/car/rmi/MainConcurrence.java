package fr.car.rmi;

import java.util.ArrayList;
import java.util.List;

public class MainConcurrence {

	/**
	 * Method allow to test the differents classes implemented
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ObjetRMI objetUn = new ObjetRMI();
		ObjetRMI objetDeux = new ObjetRMI();
		ObjetRMI objetTrois = new ObjetRMI();
		ObjetRMI objetQuatre = new ObjetRMI();
		ObjetRMI objetCinq = new ObjetRMI();
		ObjetRMI objetSix = new ObjetRMI();

		List<ObjetRMI> filsObjetUn = new ArrayList<ObjetRMI>();
		filsObjetUn.add(objetDeux);
		filsObjetUn.add(objetCinq);
		objetUn.setFils(filsObjetUn);
		objetUn.setIdObjet(1);
		objetUn.setPere(null);

		List<ObjetRMI> filsObjetDeux = new ArrayList<ObjetRMI>();
		filsObjetDeux.add(objetTrois);
		filsObjetDeux.add(objetQuatre);
		objetDeux.setFils(filsObjetDeux);
		objetDeux.setIdObjet(2);
		objetDeux.setPere(objetUn);

		List<ObjetRMI> filsObjetCinq = new ArrayList<ObjetRMI>();
		filsObjetCinq.add(objetSix);
		objetCinq.setFils(filsObjetCinq);
		objetCinq.setIdObjet(5);
		objetCinq.setPere(objetUn);

		objetTrois.setIdObjet(3);
		objetTrois.setFils(null);
		objetTrois.setPere(objetDeux);

		objetQuatre.setIdObjet(4);
		objetQuatre.setFils(null);
		objetQuatre.setPere(objetDeux);

		objetSix.setIdObjet(6);
		objetSix.setFils(null);
		objetSix.setPere(objetCinq);

		SiteImplConcurrence site = new SiteImplConcurrence();
		site.send(objetUn, "Ceci est un message de test !");
	}

}
