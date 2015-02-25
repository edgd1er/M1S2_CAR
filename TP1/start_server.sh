#!/bin/bash
#
#
# Script to launchdu JAVA FTP server
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

#Location of the JAR File
LOCATION=$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)/
# Jar Filename
JAR="ServerFtp.jar"
# users homedir
HDIR="/tmp/homedir"
DBG="1"
JAVA=$(which java)
JAVA="/usr/lib/jvm/java-7-oracle/bin/java"

## Fonctions

function launchjar(){

cmd="$JAVA -jar ${LOCATION}${JAR} $HDIR $DBG"
echo $cmd
$($cmd)

}

function createUserDir {
	mkdir -p ${HDIR}/anonymous
	mkdir -p ${HDIR}/user
}

## Main Start Java Server

echo "!!!!!!!!!!!!"$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)/`basename "${BASH_SOURCE[0]}"` 

createUserDir
launchjar
