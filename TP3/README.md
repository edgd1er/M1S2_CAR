# M1S2_CAR

Conception application répartie

Implémentation d'une application permettant de transférer en RMI des données à un ensemble de données organisé selon une topologie en arbre.

28/03/2015

*** 0/ README

Ce projet permet via un terminal de transférer en RMI des données à des objets sur un arbre.


*** 3/ EXEMPLE DE CODE


*** 4/ TESTS UNITAIRES

Une partie des classes du projet ont été testé, les autres ne pouvant pas être tester faute de code testable.
Voici la liste des classes testée :
	* MessageImpl ;
	* SiteImpl ;
	* ObjetRMI.


/* idée de tests
    Bonne création d'un objet 'Noeud'.
    Bonne création d'une suite d'objets 'Noeud'.
    Bonne implémentation de ces objets:
        récupération de l'objet Pere et Fils pour un objet Noeud intermédiaire,
        non récupération de l'objet Pere si le Noeud étudié n'en a pas,
        non récupération des objets Fils si le Noeud étudié n'en a pas.
    Bonne récupération des données transférées:
        sur tous les fils de l'objet Noeud initiateur.
*/
