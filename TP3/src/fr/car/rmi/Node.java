package fr.car.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.car.rmi.core.SiteImpl;

/**
 * This class create new site knwon as node. three parameters are required:
 * siteName [RMIHost RMIPort].
 * 
 * @author Emeline SALOMON & Francois DUBIEZ
 * 
 */
public class Node {

	static String host = "";
	static int port = 0;
	static String siteName = "";
	static Registry registry;
	static SiteImpl site;

	public static void main(final String[] args) {

		getArgs(args);

		// Contacting AdressBook
		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (final RemoteException e1) {
			System.err.println("Error, cannot find registry" + e1.getMessage());
			System.exit(-1);
		}

		// is this Node registered ?
		try {
			registry.lookup(siteName);
			System.out.println("[" + siteName + "] already exists");
			return;
		} catch (final NotBoundException e) {
			// the site doesn't already exist
		} catch (final RemoteException e) {
			System.err
					.println("Error, cannot find Unable to connect to RMI server "
							+ e.getMessage());
			System.exit(-1);

		}

		//create new node and register it.
		try {
			site = new SiteImpl(siteName);
			registry.rebind(site.getName(), site);
			System.out.println("Site " + site.getName() + " créé.");
		} catch (final RemoteException e) {
			System.err
					.println("Error, cannot find Unable to connect to RMI server "
							+ e.getMessage());
			System.exit(-1);
		}


		// kill node afer having suppressed itself from addressBook.
		Thread T = new Thread() {
			public Registry registry;
			public SiteImpl site;

			public void setup(Registry reg, SiteImpl site) {
				this.registry = reg;
				this.site = site;
			}

			@Override
			public void run() {
				RemoveSite(this.registry, this.site);
			}
		};

		// T.setup(registry,site);
		Runtime.getRuntime().addShutdownHook(T);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.currentThread().sleep(99999999);
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}

	private static void RemoveSite(Registry registry, final SiteImpl site) {
		String lname = "";
		try {
			lname = site.getName();
			registry.unbind(lname);
		} catch (final Exception e) {
			throw new RuntimeException("Error while removing site " + lname
					+ " from registry.");
		}
	}

	public static void getArgs(String[] args) {
		// create option port
		Option oSite = new Option("s", "sitename", true,
				"Name of the local node.");
		Option oHost = new Option("a", "addressbook", true,
				"Name of the local registry (aka adressBook.jar)");
		Option oPort = new Option("p", "port", true,
				"port number of the local addressBook (port between 1024 and 65535");
		Option oHelp = new Option("h", "help", false, "Get this help message");
		// create Options object
		Options options = new Options();
		oSite.setRequired(true);
		oHost.setRequired(true);
		oPort.setRequired(true);
		options.addOption(oPort);
		options.addOption(oSite);
		options.addOption(oHost);
		options.addOption(oHelp);

		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e2) {
			System.out
					.println("Error, cannot create parser." + e2.getMessage());
			formatter.printHelp("addressbook", options);
			System.exit(-11);
		}

		if ((args.length < 3) || (cmd.hasOption("h"))) {
			formatter.printHelp("node", options);
			System.exit(-1);
		}

		// Setting addressBook name
		if (cmd.hasOption('a')) {
			host = cmd.getOptionValue('a');
		}

		// Setting AdressBook port
		if (cmd.hasOption('p')) {
			try {
				port = Integer.parseInt(cmd.getOptionValue('p'));
			} catch (NumberFormatException e) {
				System.out
						.println("Error, number between 1024 and 65535 is expected");
				formatter.printHelp("Node", options);
			}
		}

		// setting local siteName
		if (cmd.hasOption('s')) {
			siteName = cmd.getOptionValue('s');
		}

	}

}
