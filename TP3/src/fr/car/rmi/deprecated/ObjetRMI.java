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
	
	/**
	 * Return the list of the sons of the node
	 * @return
	 */
	public List<ObjetRMI> getFils() {
		return fils;
	}
	
	/**
	 * Add a son to the list
	 * @param f
	 */
	public void addFils(ObjetRMI f) {
		this.fils.add(f);
	}
	
	/**
	 * Return a son with his id
	 * @param idFils
	 * @return
	 */
	public ObjetRMI getOneFils(int idFils) {
		return this.fils.get(idFils);
	}
	
	/**
	 * Return the id of the object
	 * @return
	 */
	public int getIdObjet() {
		return idObjet;
	}
	
	/**
	 * Return the father of the node
	 * @return
	 */
	public ObjetRMI getPere() {
		return pere;
	}
	
	/**
	 * Allow to set the id of the object
	 * @param idObjet
	 */
	public void setIdObjet(int idObjet) {
		this.idObjet = idObjet;
	}
	
	/**
	 * Allow to set the father of the node
	 * @param pere
	 */
	public void setPere(ObjetRMI pere) {
		this.pere = pere;
	}
	
	/**
	 * Allow to set the list of sons of the object
	 * @param fils
	 */
	public void setFils(List<ObjetRMI> fils) {
		this.fils = fils;
	}

}
