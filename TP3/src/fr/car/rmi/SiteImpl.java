package fr.car.rmi;

/**
 * Server interface allow to send and receive a message from an RMI Object
 * 
 * @author Emeline SALOMON & François DUBIEZ
 * 
 */
public class SiteImpl implements SiteItf {

	@Override
	public void send(ObjetRMI sender, String message) {
		this.receive(sender, message);
		if (sender.getFils() != null) {
			for (int i = 0; i < sender.getFils().size(); i++) {
				this.send(sender.getOneFils(i), message);
			}
		} else {
			System.out.println("--> L objet " + sender.getIdObjet()  + " n'as pas de fils.Fin de la branche ! <--");
		}
	}

	@Override
	public void receive(ObjetRMI sender, String message) {
		System.out.println("L'objet " + sender.getIdObjet()
				+ " vient de recevoir le message '" + message + "'.");
	}

}
