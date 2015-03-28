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

import fr.car.rmi.core.MessageItf;
import fr.car.rmi.core.MessageImpl;
import fr.car.rmi.core.SiteItf;

/**
 * Send message from a sit to its child.
 * 
 * @author Emeline SALOMON & Francois DUBIEZ
 * 
 */
public class SendMessage {

	static String host;
	static int port;
	static String siteName;
	static String message;

	public static void main(final String[] args) {

		Registry registry = null;
		SiteItf site = null;

		getArgs(args);

		try {
			registry = LocateRegistry.getRegistry(host, port);
		} catch (final RemoteException e1) {
			System.err.println("Error, cannot contact local registry on port "
					+ String.valueOf(port) + " :" + e1.getMessage());
			System.exit(-1);

		}

		try {
			site = (SiteItf) registry.lookup(siteName);
			sendMessage(site, message);
		} catch (final Exception e) {
			System.err.println("Site " + siteName + " non trouvé");
			System.exit(-1);
		}

	}

	private static void sendMessage(SiteItf site, final String message) {
		MessageItf m = new MessageImpl(message, site);
		try {
			site.send(m);
		} catch (final Exception e) {
			throw new RuntimeException("Impossible d'envoyer le message");
		}
	}

	public static void getArgs(String[] args) {
		// create option port
		Option oSource = new Option("s", "source", true,
				"sitename 1 as source node.");
		Option oMessage = new Option("m", "message", true,
				"message to be send (String).");
		Option oHost = new Option("a", "addressbook", true,
				"Name of the local registry");
		Option oPort = new Option("p", "port", true,
				"port number of the local addressBook (port between 1024 and 65535");
		Option oHelp = new Option("h", "help", false, "Get this help message");
		// create Options object
		Options options = new Options();
		oSource.setRequired(true);
		oMessage.setRequired(true);
		oHost.setRequired(true);
		oPort.setRequired(true);
		options.addOption(oSource);
		options.addOption(oMessage);
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
			formatter.printHelp("sendMessage", options);
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

		// setting sender siteName
		if (cmd.hasOption('s')) {
			siteName = cmd.getOptionValue('s');
		}

		// setting message
		if (cmd.hasOption('m')) {
			message = cmd.getOptionValue('m');
		}
	}
}
