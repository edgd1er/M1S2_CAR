package fr.car.rmi.deprecated;

import java.rmi.server.RemoteObject;
import java.util.List;

/**
 * 
 * Class representing an RMI object with a left and a right door neighbor
 * 
 * @author Emeline SALOMON & François DUBIEZ
 * 
 */
public class ObjetRMI extends RemoteObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idObjet;
	private ObjetRMI pere;
	private List<ObjetRMI> fils;

	public ObjetRMI() {
		super();
	}

	public ObjetRMI(int idObjet, ObjetRMI pere, List<ObjetRMI> fils) {
		super();
		this.idObjet = idObjet;
		this.pere = pere;
		this.fils = fils;
	}

	public List<ObjetRMI> getFils() {
		return fils;
	}

	public void addFils(ObjetRMI f) {
		this.fils.add(f);
	}

	public ObjetRMI getOneFils(int idFils) {
		return this.fils.get(idFils);
	}

	public int getIdObjet() {
		return idObjet;
	}

	public ObjetRMI getPere() {
		return pere;
	}

	public void setIdObjet(int idObjet) {
		this.idObjet = idObjet;
	}

	public void setPere(ObjetRMI pere) {
		this.pere = pere;
	}

	public void setFils(List<ObjetRMI> fils) {
		this.fils = fils;
	}

}
