package fr.car.rmi;

public class ThreadConcurrence extends Thread {

	private ObjetRMI objet;
	private String message;

	public ThreadConcurrence(ObjetRMI o, String m) {
		super();
		this.objet = o;
		this.message = m;
	}

	@Override
	public void run() {
		SiteImpl site = new SiteImpl();

		site.send(this.objet, message);
	}

}
