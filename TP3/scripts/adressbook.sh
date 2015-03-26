#!/bin/bash


## Colors
RED="\033[0;31m"
GRN="\033[0;32m"
BLU="\033[0;34m"
WHT="\033[0;37m"

## Backgrounds
B_RED=""
B_GRN=""
B_BLU=""
B_WHT=""

# Parameters for fork/fg
set -m

# Global variables
TERMINAL="gnome-terminal" # xterm
JAR_PATH="../jar"
CLASS_PATH="`pwd`/jar/adressbook.jar:fr/car/rmi"
AJAR="adressbook.jar"
APORT="2100"



export CLASSPATH=$CLASS_PATH

function killAll {
    # Kill remaining process
    echo -e "$GRN Killing Java... $WHT"
    #kill `ps -ef | grep "$AJAR" | awk '{print $2}'` > /dev/null 2&>1
    kill `ps -ef | grep "$NJAR" | awk '{print $2}'` > /dev/null 2&>1
    kill `ps -ef | grep "$MJAR" | awk '{print $2}'` > /dev/null 2&>1
    kill `ps -ef | grep "rmiregistry" | awk '{print $2}'` > /dev/null 2&>1
}

function startAdressBook {
    echo -e "$GRN Starting AdressBook... $WHT"
    if [ -z "$AJAR" ]; then
        echo -e "\n $RED Error, AdressBook jar not found. Please check path and/or Java installation. $WHT \n"
        killAll
        exit
    fi 
    
    java -jar $JAR_PATH/${AJAR} -p ${APORT} 
    sleep 1
}


#Main
set -x
startAdressBook

