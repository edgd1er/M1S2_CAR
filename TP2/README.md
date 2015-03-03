# M1S2_CAR
Conception application repartie


﻿Implémentation d’une passerelle REST pour accèder au serveur FTP en Java

02/03/2015

*** 0/ README

Ce projet permet d'accèder via un navigateur Web d'accéder au travers d'une passerelle ç un serveur FTP.
Les principales opérations FTP sont implémentées
- Loging
- Quit
- retrieve
- Store
- delete
- Liste

La liste des services exposées peut etre obtenues a l'URL ci-dessous:
Liste des services: http://localhost:8080/rest/
Descriptions methodes du service api/ftp : http://localhost:8080/rest/api?_wadl



La RFC 2616 indique les utilisations des différents verbes: http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html

La liste des codes se trouve: http://tools.ietf.org/rfcmarkup?rfc=7231#section-6.2.1

Le Get permet de récupérer une information spécifié par l'URI.
POST permet de déposer un nouvel élément sur le serveur. Trois codes retour sont possibles pour POST, 200 OK, 204 No content, 201 Created.

Le PUT permet une mise a jour d'un élement, si l'élément ne devait pas exister sur le serveur alors un status 201 (created) DOIT etre retourné.
Le principe du PUT n'est que de mettre a jour une ressource et de demander explicitement au client de modifier sa requete pour qu'elle corresponde à un PUT. Nous avons mis en place une implémentation stricte de la RFC.

Le Delete demande la suppression d'un élément communiquer dans l'URI.
Trois états sont possible ( 200 Ok avec un retour possible, 202 Accepted mais non réalisée, 204 réalisé mais aucun retour n'est à faire.



