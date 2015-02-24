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
location=":~/Documents/S2_CAR/M1S2_CAR/TP1/"
# Jar Filename
JAR="ServerFtp.jar"
# users homedir
HDIR="/tmp/homedir"
DBG="1"

## Fonctions

function launchjar(){
	
	java -jar ${LOCATION}${JAR} $HDIR $DBG

}

function createUserDir {
	mkdir -p ${HDIR}/anonymous
	mkdir -p ${HDIR}/user
}

## Main Start Java Server

createUserDir
launchjar
