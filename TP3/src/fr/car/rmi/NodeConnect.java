package fr.car.rmi;

import java.rmi.RemoteException;
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

	public static void main(final String[] args) {

		String siteName1 = null;
		String siteName2 = null;

		getArgs(args);

		SiteImpl site1;
		SiteImpl site2;
		try {
			site1 = (SiteImpl) registry.lookup(siteName1);
		} catch (final Exception e) {
			System.err.println("Impossible de touver le site " + siteName1);
			return;
		}
		try {
			site2 = (SiteImpl) registry.lookup(siteName2);
		} catch (final Exception e) {
			System.err.println("Impossible de touver le site " + siteName2);
			return;
		}

		try {
			site1.addSite(site2);

		} catch (final RemoteException e) {
			System.err.println("Impossible de connecter " + siteName1 + " et "
					+ siteName2);
		}
	}
	
	/**
	 * 
	 * @param args
	 */
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

		// setting local siteName
		if (cmd.hasOption('c')) {
			strCom = cmd.getOptionValue('c');
		}

	}

}
