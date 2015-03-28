package fr.car.rmi;

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

import fr.car.rmi.core.SiteItf;

/**
 * is an executable class used to connect 2 sites. The main has parameters:
 * "siteName1->siteName2" [rmiPort].
 * 
 * @author Emeline SALOMON & Francois DUBIEZ
 * 
 */
public class NodeConnect {

	static String host = "";
	static int port = 0;
	static String strCom = "";
	static Registry registry;
	static String siteName1 = null;
	static String siteName2 = null;
	

	public static void main(final String[] args) {

		SiteItf site1 = null;
		SiteItf site2 = null;

		getArgs(args);

		try {
			registry = LocateRegistry.getRegistry(port);
		} catch (RemoteException e) {
			System.err.println("Error, cannot contact local registry on port "
					+ String.valueOf(port));
			System.exit(-1);
		}

		try {

			site1 = (SiteItf) registry.lookup(siteName1);
		} catch (final Exception e) {
			System.err.println("Error, cannot find site " + siteName1 +" :" + e.getMessage());
			System.exit(-1);
		}
		try {
			site2 = (SiteItf) registry.lookup(siteName2);
		} catch (final Exception e) {
			System.err.println("Error, cannot find site " + siteName2 +" :" + e.getMessage());
			System.exit(-1);
		}

		try {
			// site2 is a child of site1
			site1.addSite(site2);
			// site2 has site1 as father
			site2.setFatherNode(site1);

		} catch (final RemoteException e) {
			System.err.println("Cannot connect " + siteName1 + " to "
					+ siteName2 + " :" + e.getMessage());
			e.printStackTrace();
			System.exit(-1);

		}
	}

	public static void getArgs(String[] args) {
		// create option port
		Option oConnect1 = new Option("s", "source", true,
				"sitename 1 as parent node.");
		Option oConnect2 = new Option("d", "destination", true,
				"sitename 2 as child node.");
		Option oHost = new Option("a", "addressbook", true,
				"Name of the local registry (aka adressBook.jar)");
		Option oPort = new Option("p", "port", true,
				"port number of the local addressBook (port between 1024 and 65535");
		Option oHelp = new Option("h", "help", false, "Get this help message");
		// create Options object
		Options options = new Options();
		oConnect1.setRequired(true);
		oConnect2.setRequired(true);
		oHost.setRequired(true);
		oPort.setRequired(true);
		options.addOption(oConnect1);
		options.addOption(oConnect2);
		options.addOption(oHost);
		options.addOption(oPort);
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
			formatter.printHelp("NodeConnect", options);
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
				formatter.printHelp("addressbook", options);
			}
		}

		// setting father siteName
		if (cmd.hasOption('s')) {
			siteName1 = cmd.getOptionValue('s');
		}

		// setting child siteName
		if (cmd.hasOption('d')) {
			siteName2 = cmd.getOptionValue('d');
		}

		
	}

}
