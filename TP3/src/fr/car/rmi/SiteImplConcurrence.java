package fr.car.rmi;

public class SiteImplConcurrence implements SiteItf {

	@Override
	public void send(ObjetRMI sender, String message) {
		this.receive(sender, message);
		if (sender.getFils() != null) {
			for (int i = 0; i < sender.getFils().size(); i++) {
				ThreadConcurrence thread = new ThreadConcurrence(
						sender.getOneFils(i), message);
				thread.start();

			}
		} else {
			System.out.println("--> L objet "+ sender.getIdObjet() +" n'as pas de fils.Fin de la branche ! <--");
		}
	}

	@Override
	public void receive(ObjetRMI sender, String message) {
		System.out.println("L'objet " + sender.getIdObjet()
				+ " vient de recevoir le message '" + message + "'.");
	}

}
