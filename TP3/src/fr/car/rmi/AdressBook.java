package fr.car.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.car.rmi.core.SiteItf;

public class AdressBook {

	/**
	 * @param args
	 */

	public static void main(String[] args) {

		int port = 3131;

		// create option port
		Option oPort = new Option("p", "port", true, "number to define listening port between 1024 and 65535");
		Option oHelp = new Option("h","help",false,"Get this help message" );
		// create Options object
		Options options = new Options();
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
			formatter.printHelp("annuaire", options);
			System.exit(-11);
		}

		if ((args.length< 1) || (cmd.hasOption("h"))) {
			formatter.printHelp("annuaire", options);
			System.exit(-1);
		}

		if (cmd.hasOption("p")) {
			try {
				port = Integer.parseInt(cmd.getOptionValue("p"));
			} catch (NumberFormatException e) {
				System.out.println("Error, port is not a number");
				System.exit(1);
			}
			if (port < 1024 || port > 65535) {
				System.out.println("Error, port is not a number");
				formatter.printHelp("annuaire", options);
				System.exit(1);
			}
		}

		Registry registry;
		// starting registry and check instance
		try {
			registry = LocateRegistry.createRegistry(port);
			System.out.println("L'annuaire a démarré sur le port " + port);
		} catch (final ExportException e) {
			try {
				registry = LocateRegistry.getRegistry(port);
			} catch (final RemoteException e1) {
				throw new RuntimeException(
						"Impossible de récupérer l'annuaire", e);
			}
		} catch (final RemoteException e) {
			throw new RuntimeException("Impossible de démarrer l'annuaire", e);
		}

		// waiting for command in std in
		String line = "";
		final Scanner s = new Scanner(System.in);
		while (s.hasNextLine() && (line = s.nextLine()) != null) {
			if (line.startsWith("help")) {
				System.out
						.println("Les commandes disponibles sont: help, quit et list");
			} else if (line.startsWith("quit")) {
				s.close();
				System.exit(0);
			} else if (line.startsWith("connect")) {
				final String[] splittedLine = line.split(" ");
				for (int i = 1; i < splittedLine.length; i++) {
					final Pattern pattern = Pattern.compile("(.*)(<-|->)(.*)");
					final Matcher matcher = pattern.matcher(splittedLine[i]);
					if (matcher.find()) {
						final String siteName1 = matcher.group(1);
						SiteItf site1;
						SiteItf site2;
						// get node name 1
						try {
							site1 = (SiteItf) registry.lookup(siteName1);
						} catch (final Exception e) {
							System.out.println("Impossible de touver le site "
									+ siteName1);
							continue;
						}

						// get node name 2
						final String siteName2 = matcher.group(3);
						try {
							site2 = (SiteItf) registry.lookup(siteName2);
						} catch (final Exception e) {
							System.out.println("Impossible de touver le site "
									+ siteName2);
							continue;
						}

						// get connection direction
						try {
							if (matcher.group(2).equals("->")) {
								site1.addSite(site2);
							} else {
								site2.addSite(site1);
							}
						} catch (final RemoteException e) {
							System.out.println("Impossible de connecter "
									+ siteName1 + " et " + siteName2);
						}
					}
				}
			} else if (line.startsWith("list")) {
				// list know local node
				try {
					if (registry.list().length == 0) {
						System.out.println("Annuaire vide");
					}
					for (final String string : registry.list()) {
						System.out.println(string);
					}
				} catch (final Exception e) {
					throw new RuntimeException(
							"Impossible d'accéder à la liste", e);
				}
			} else {
				System.out.println("Commande non trouvée.");
			}
		}
		// empecher le programme de s'arrêter
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.currentThread();
					Thread.sleep(99999999);
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}
	
	
	
	
}