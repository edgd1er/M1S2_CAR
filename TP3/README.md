# M1S2_CAR

Conception application répartie

Implémentation d'une application permettant de transférer en RMI des données à un ensemble de données organisé selon une topologie en arbre.

28/03/2015

*** 0/ README

Ce projet permet via un terminal de transférer en RMI des données à des objets sur un arbre ou un graphe.
Les noeuds envoient les messages aux noeuds fils en parallèle.

Les quatres jar se lancent en ligne de commande avec des variables. Un java -jar xx.jar -h indique les paramètres attendus.

AdressBook: Serveur RMI qui va enregistrer les noeuds dans le registry.

usage: adressbook, registry qui permet d'enregister les noeuds afin qu'ils puissent se trouver les uns les autres.
 -h,--help         Get this help message
 -p,--port <arg>   number to define listening port between 1024 and 65535

Node: Serveur RMI qui permet l'initialisation de l'objet RMI et son inscription dans le registry. prend en parametre, l'@ IP et le port ou s'execute adressBook, troisieme parametre le nom du noeud.

usage: node
 -a,--addressbook <arg>   Name of the local registry (aka adressBook.jar)
 -h,--help                Get this help message
 -p,--port <arg>          port number of the local addressBook (port
                          between 1024 and 65535
 -s,--sitename <arg>      Name of the local node.

Nodeconnect: client RMI, qui accède aux noeuds afin de créer les liens entre eux.(ajout de fils)

usage: NodeConnect
 -a,--addressbook <arg>   Name of the local registry (aka adressBook.jar)
 -d,--destination <arg>   sitename 2 as child node.
 -h,--help                Get this help message
 -p,--port <arg>          port number of the local addressBook (port
                          between 1024 and 65535
 -s,--source <arg>        sitename 1 as parent node.

SendMessage: client RMI qui permet de demander a un noeud de transmettre un message.

usage: sendMessage
 -a,--addressbook <arg>   Name of the local registry
 -h,--help                Get this help message
 -m,--message <arg>       message to be send (String).
 -p,--port <arg>          port number of the local addressBook (port
                          between 1024 and 65535
 -s,--source <arg>        sitename 1 as source node.


*** 3/ EXEMPLE DE CODE


Exemple montrant l'utilisation d'une classe interne définissant un thread

	for (SiteItf child : this.childNodes) {
					// inner class to run thread thread
					class myThread extends Thread {

						private SiteItf child;
						private MessageItf message;

						public myThread(SiteItf child, MessageItf message) {
							this.child = child;
							this.message = message;
						}

						public void run() {
							try {
								child.send(message);
							} catch (RemoteException e) {
								try {
									System.err.println("Error, node "
											+ child.getName()
											+ " did not send its message!!!");
								} catch (RemoteException e1) {
								}
								e.printStackTrace();
							}
						}

					}
					myThread t = new myThread(child, message);
					t.start();

					System.out.println("node " + this.getName()
							+ " sent a message(" + message.getContent()
							+ ") to " + child.getName());
				}


Utilisation d'une verrou afin de bloquer les messages deja envoyés:

		synchronized (this.receivedMessages) {
			for (MessageItf m : this.receivedMessages) {
				if (m.getUUID().equals(message.getUUID())) {
					contained = true;
					alreadySendUUID = message.getUUID().toString();
				}
			}
		}

		if (!contained) {......



Idée de fonctions a implementer

- retrait d'un noeud avec reconstitution de l'arbre/graphe.(manque l'algo de reconnection)
- Ajout d'un pere pour un noeud. (fct addfatherNode faite)
- massive quit pour que tout les noeuds existants se terminent. (fonction individuelle quit faite)
- ...

*** 4/ TESTS UNITAIRES

Une partie des classes du projet ont été testé, les autres ne pouvant pas être tester faute de code testable.
Voici la liste des classes testée :
- MessageImpl ;
- SiteImpl.



