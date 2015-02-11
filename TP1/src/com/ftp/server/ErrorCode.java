package com.ftp.server;

import java.io.IOException;
import java.util.HashMap;

public final class ErrorCode {

	private static HashMap<String, String> ftpErrorMap;

	static  {
		ftpErrorMap = new HashMap<String, String>();
		populateCode();
	}

	public static String getMessage(String _ErrorCode, String _paramCode) {

		String message="";
		try {
			message = _ErrorCode+ " " + ftpErrorMap.get(_ErrorCode);}
		catch(Exception e ){
			message = _ErrorCode+ " " + ftpErrorMap.get("501");
			_paramCode="";
			System.err.println("ErrorCode: erreur non identifié: "+ _ErrorCode +"\n");			
			e.printStackTrace();
		}
		finally{
			if (!(_paramCode == null)) {
				return message.replace("PARAM", _paramCode);

		}

		}
		
		//System.out.println(message);
		return message;

	}
	
	public static void sendErrorMessage(Tools _mytools,String errorcode,String message) throws IOException{
		System.out.println("Erreur: " + errorcode + " " + message);
		_mytools.sendMessage(message);
	} 


	private static void populateCode() {
		ftpErrorMap
				.put("100",
						"L'action demandée est lancée, attendre une autre réponse avant de procéder à une nouvelle commande.");
		ftpErrorMap.put("110", "Restart marker replay . In this case, the text is exact and not left to the particular implementatio");
		ftpErrorMap.put("120", "Service prêt dans nnn minutes.");
		ftpErrorMap.put("125",
				"Connexion établie, transfert en cours de démarrage.");
		ftpErrorMap.put("150", "File status okay; about to open data connection.");
		ftpErrorMap.put("200", "PARAM.");
		ftpErrorMap.put("202", "Commande non prise en charge par ce site.");
		ftpErrorMap.put("211", " 	System status, or system help reply.");
		ftpErrorMap.put("212", "Statut de répertoire.");
		ftpErrorMap.put("213", "Statut de fichier.");
		ftpErrorMap
				.put("214",
						"Message d'aide sur l'utilisation du serveur ou la signification d'une commande particulière non-standard. Cette réponse est uniquement utile à un utilisateur humain.");
		ftpErrorMap.put("215", "Master 1 CAR 2014-2015 Serveur FTP en Java. PARAM");
		ftpErrorMap
				.put("220",
						"Bienvenue dans le serveur PARAM.Identifiez vous avec la commande USER");
		ftpErrorMap.put("221", "Service closing control connection.PARAM");
		ftpErrorMap.put("225", "Data connection open; no transfer in progress.PARAM");
		ftpErrorMap.put("226", "Transfert terminé avec succès.PARAM");
		ftpErrorMap.put("227", "Entering Passive Mode (PARAM).");
		ftpErrorMap.put("228", "Entering Long Passive Mode (PARAM).");
		ftpErrorMap.put("229", "Entering Extended Passive Mode (|||port|).");
		ftpErrorMap.put("230", "User logged in, proceed. Logged out if appropriate.PARAM");
		ftpErrorMap.put("231", "Utilisateur déconnecté. Fin de service.PARAM");
		ftpErrorMap
				.put("232",
						"Commande de déconnexion enregistrée. S'effectuera à la fin du transfert.");
		ftpErrorMap.put("250", "PARAM");
		ftpErrorMap.put("257", "PARAM");
		ftpErrorMap
				.put("300",
						"La commande a été acceptée, mais l'action demandée est en attente de plus amples informations.");
		ftpErrorMap.put("331",
				"Utilisateur reconnu. En attente du mot de passe.PARAM");
		ftpErrorMap.put("332", "Besoin d'un compte de connexion.");
		ftpErrorMap.put("350", "PARAM");
		ftpErrorMap
				.put("400",
						"La commande n'a pas été acceptée et l'action demandée n'a pas eu lieu, mais l'erreur est temporaire et l'action peut être demandée à nouveau.");
		ftpErrorMap.put("421", "PARAM");
		ftpErrorMap
				.put("425", "Impossible d'établir une connexion de données.");
		ftpErrorMap.put("426", "Connexion fermée ; transfert abandonné.");
		ftpErrorMap.put("430", "Identifiant ou mot de passe incorrect");
		ftpErrorMap.put("434", "Hôte demandé indispoible.");
		ftpErrorMap.put("450", "");
		ftpErrorMap.put("451", "");
		ftpErrorMap.put("452", "");
		ftpErrorMap
				.put("500",
						"Erreur de syntaxe ; commande non reconnue et l'action demandée n'a pu s'effectuer.");
		ftpErrorMap.put("501","Erreur de syntaxe dans les paramètres ou les arguments.PARAM");
		ftpErrorMap.put("502", "Commande non implémentée.");
		ftpErrorMap.put("503", "Mauvaise séquence de commande");
		ftpErrorMap.put("504", "Commande non implémentée pour ces paramètres");
		ftpErrorMap.put("530", "Connexion non établie");
		ftpErrorMap.put("532", "Besoin d'un compte pour charger des fichiers.");
		ftpErrorMap
				.put("550",
						"Requête non exécutée. Fichier indisponible (ex., fichier introuvable, pas d'accès).PARAM");
		ftpErrorMap.put("551", "");
		ftpErrorMap.put("552", "");
		ftpErrorMap.put("553",
				"Action non effectuée. Nom de fichier non autorisé.");
		ftpErrorMap.put("600", " Series");
		ftpErrorMap.put("631", "Réponse d'intégrité protégée");
		ftpErrorMap.put("632",
				"Réponse d'intégrité et de confidentialité protégées");
		ftpErrorMap.put("633", "Réponse de confidentialité protégée");
		ftpErrorMap.put("10000", "");
		ftpErrorMap.put("10054", "");
		ftpErrorMap.put("10060", "Connexion impossible au serveur distant.");
		ftpErrorMap
				.put("10061",
						"Connexion impossible au serveur distant : Connexion refusée par le serveur.");
		ftpErrorMap.put("10066", "Répertoire non vide.");
		ftpErrorMap.put("10068", "Trop d'utilisateurs connectés.");

	}

}