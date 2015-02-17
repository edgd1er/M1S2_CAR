#!/bin/bash
#
#
# Script de validation fonctionnel du serveur FTP en Java 
#
#
#
#	Emeline Salomon
#	François Dubiez
#
#	 Master 1 Informatique
#	 Conception d'applications reparties
#

#set -x

datatxt="data.txt"
datatxtmd5="data.md5"
databin="baboon_gray.png"
databinmd5="baboon_gray.md5"
datatxtori="data.txt.ori"
databinori="baboon_gray.png.ori"
tailleasciirecv=20131
tailleascii=20066
taillebin=176679

## Fonctions

function assert {
if [ $totest -eq "" ]; then 
	echo "test"
fi
}

# Verification des fichiers de départ

function restorefile {

echo -e  "\n!!!!Verification des échantillions de départ!!!!"

oracle=$(md5sum -c *.md5 | cut -d':' -f 2 | grep -i "réussi" | wc -l )

if [ "$oracle" != "2" ]; then 
echo -e "Erreur de md5 dans les fichiers, restauration des fichiers originaux."
echo -e "Pouvez vous relancer le scripts ?\n"
 cp -f ${datatxtori} ${datatxt}
 cp -f ${databinori} ${databin}
else 
echo -e "Fichiers de tests non corrompus. on peut continuer\n"
fi
}

# verification de l oracle
function testoracle {
	ftp -dvin < $scr  > temp.log 2>&1
	oracle=$(grep $tval temp.log | cut -d' ' -f1 )
	if [ "$oracle" == "" ]; then 
	res="Erreur: \n"$(<temp.log)
		else 
	res="OK"
fi
echo -e "$txt $res"
}

###################
## 	Main		 ##
###################

#verification des fichiers de tests
restorefile

# tests fonctionnels


echo -e "\n"
echo "#################################################"
echo "##       tests login des utilisateurs          ##"
echo "#################################################"
# utilsateur anon accepté mais ne peut pas upload
res=""
tval=532
scr="features/test_anon_put.txt"
txt="Test de refus d envoi de fichier pour un anonyme: (attendu $tval): "
testoracle

# test user inconnu avec mdp
res=""
tval=430
scr="features/test_user_inconnu.txt"
txt="Test de refus login inconnu / pass: (attendu $tval): "
testoracle

# test user connu avec mdp incorrect
res=""
tval=430
scr="features/test_user_pwd_ko.txt"
txt="Test de refus login connu / pass incorrect: (attendu $tval): "
testoracle

# test user connu avec mdp correct
res=""
tval=230
scr="features/test_user_pwd_ok.txt"
txt="Test d acceptation login connu / pass correct: (attendu $tval): "
testoracle

### test de ls ##
res=""
tval=250
scr="features/test_active_ls.txt"
txt="Test de changement de repertoire: (attendu $tval): "

testoracle



echo -e "\n"
echo "#################################################"
echo "##           test en mode actif ASCII          ##"
echo "#################################################"


### test de ls ##
res=""
tval=226
scr="features/test_active_ls.txt"
txt="\nTest de l affichage du contenu d'un repertoire: (attendu $tval): "
testoracle

######### mode ascii ############
###   test de envoi de fichier ##
res=""
tval="successful"
scr="features/test_put_ascii_active.txt"
txt="Test de l envoi d'un fichier ascii: (attendu $tval): "
testoracle
oracle=$(grep ^${tailleasciirecv} temp.log | cut -d' ' -f 1)
res="ok\n"
if [ "$oracle" = "" ]; then 
	res="echec\n"
fi
echo -e "test de la taille du fichier:"${oracle}: $res"\n"


###   test de reception de fichier ##
res=""
tval="successful"
scr="features/test_retr_ascii_active.txt"
txt="Test de la reception d'un fichier ascii: (attendu $tval): "
testoracle
oracle=$(grep ^${tailleascii} temp.log | cut -d' ' -f 1)
oracle2=$(md5sum -c $datatxtmd5 | cut -d':' -f 2 | grep -i "réussi" | wc -l)

res="ok"
if [[ "$oracle" = "" ]]; then 
	res="echec"
fi
res="ok"
echo -e "test de la taille du fichier:${tailleascii}: $res"
if [[ "$oracle2" = "" ]]; then 
	res="ko"
	more temp.log
fi
	echo -e "test du md5 du fichier:${oracle2}: $res"

echo -e "\n"
echo "#################################################"
echo "##           test en mode actif BINAIRE        ##"
echo "#################################################"


### test de ls ##
#res=""
#tval=226
#scr="features/test_active_ls.txt"
#txt="\nTest de l affichage du contenu d'un repertoire: (attendu $tval): "
#testoracle

######### mode ascii ############
###   test de envoi de fichier ##
res=""
tval="successful"
scr="features/test_put_bin_active.txt"
txt="Test de l envoi d'un fichier binaire: (attendu $tval): "
testoracle
oracle=$(grep ^${taillebin} temp.log | cut -d' ' -f 1)
res="ok\n"
if [ "$oracle" = "" ]; then 
	res="echec\n"
	more temp.log
fi
echo -e "test de la taille du fichier:"${oracle}: $res"\n"


###   test de reception de fichier ##
res=""
tval="successful"
scr="features/test_retr_bin_active.txt"
txt="Test de la reception d'un fichier binaire: (attendu $tval): "
testoracle
oracle=$(grep ^${taillebin} temp.log | cut -d' ' -f 1)
oracle2=$(md5sum -c $databinmd5 | cut -d':' -f 2 | grep -i "réussi" | wc -l)

res="ok"
if [[ "$oracle" = "" ]]; then 
	res="echec"
	more temp.log
fi
res="ok"
echo -e "test de la taille du fichier:${taillebin}: $res"
if [[ "$oracle2" = "" ]]; then 
	res="ko"
fi
	echo -e "test du md5 du fichier:${oracle2}: $res"


echo -e "\n"
echo "#################################################"
echo "##           test en mode passif ASCII        ##"
echo "#################################################"


### test de ls ##
res=""
tval=226
scr="features/test_passive_ls.txt"
txt="\nTest de l affichage du contenu d'un repertoire: (attendu $tval): "
testoracle

######### mode ascii ############
###   test de envoi de fichier ##
res=""
tval="successful"
scr="features/test_put_ascii_passive.txt"
txt="Test de l envoi d'un fichier ascii: (attendu $tval): "
testoracle
oracle=$(grep ^${tailleasciirecv} temp.log | cut -d' ' -f 1)
res="ok\n"
if [ "$oracle" = "" ]; then 
	res="echec\n"
fi
echo -e "test de la taille du fichier:"${oracle}: $res"\n"


###   test de reception de fichier ##
res=""
tval="successful"
scr="features/test_retr_ascii_passive.txt"
txt="Test de la reception d'un fichier ascii: (attendu $tval): "
testoracle
oracle=$(grep ^${tailleascii} temp.log | cut -d' ' -f 1)
oracle2=$(md5sum -c $datatxtmd5 | cut -d':' -f 2 | grep -i "réussi" | wc -l)

res="ok"
if [[ "$oracle" = "" ]]; then 
	res="echec"
fi
res="ok"
echo -e "test de la taille du fichier:${tailleascii}: $res"
if [[ "$oracle2" = "" ]]; then 
	res="ko"
	more temp.log
fi
	echo -e "test du md5 du fichier:${oracle2}: $res"




echo -e "\n"
echo "#################################################"
echo "##           test en mode passif BINAIRE        ##"
echo "#################################################"


### test de ls ##
#res=""
#tval=226
#scr="features/test_passive_ls.txt"
#txt="\nTest de l affichage du contenu d'un repertoire: (attendu $tval): "
#testoracle

######### mode binaire ############
###   test de envoi de fichier ##
res=""
tval="successful"
scr="features/test_put_bin_passive.txt"
txt="Test de l envoi d'un fichier bin: (attendu $tval): "
testoracle
oracle=$(grep ^${taillebin} temp.log | cut -d' ' -f 1)
res="ok\n"
if [ "$oracle" = "" ]; then 
	res="echec\n"
fi
echo -e "test de la taille du fichier:"${oracle}: $res"\n"


###   test de reception de fichier ##
res=""
tval="successful"
scr="features/test_retr_bin_passive.txt"
txt="Test de la reception d'un fichier bin: (attendu $tval): "
testoracle
oracle=$(grep ^${taillebin} temp.log | cut -d' ' -f 1)
oracle2=$(md5sum -c $databinmd5 | cut -d':' -f 2 | grep -i "réussi" | wc -l)

res="ok"
if [[ "$oracle" = "" ]]; then 
	res="echec"
fi
res="ok"
echo -e "test de la taille du fichier:${taillebin}: $res"
if [[ "$oracle2" = "" ]]; then 
	res="ko"
	more temp.log
fi
	echo -e "test du md5 du fichier:${oracle2}: $res"
