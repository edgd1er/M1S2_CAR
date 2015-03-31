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
REGISTRY=`which rmiregistry`
AJAR="adressbook.jar"
NJAR="node.jar"
AHOST="127.0.0.1"
APORT="2100"
MJAR="sendmessage.jar"
CJAR="nodeconnect.jar"



export CLASSPATH=$CLASS_PATH

function killAll {
    # Kill remaining process
    echo -e "$GRN Killing Java... $WHT"
    #kill `ps -ef | grep "$AJAR" | awk '{print $2}'` > /dev/null 2&>1
    kill `ps -ef | grep "$NJAR" | awk '{print $2}'` > /dev/null 2&>1
    kill `ps -ef | grep "$MJAR" | awk '{print $2}'` > /dev/null 2&>1
    kill `ps -ef | grep "rmiregistry" | awk '{print $2}'` > /dev/null 2&>1
}

function startRmi {
    echo -e "$GRN Starting rmi-registry... $WHT"
    if [ -z "$REGISTRY" ]; then
    	echo -e "\n $RED Error, rmiregistry not found. Please check path and/or Java installation. $WHT \n"
    	killAll
    	exit
    fi 
    $REGISTRY ${APORT} &
    rmi_registry_pid=$!
    sleep 1
}

function startAdressBook {
    echo -e "$GRN Starting AdressBook... $WHT"
    if [ -z "$AJAR" ]; then
        echo -e "\n $RED Error, AdressBook jar not found. Please check path and/or Java installation. $WHT \n"
        killAll
        exit
    fi 
    
    ${TERMINAL} -e "java -jar ${JAR_PATH}/${AJAR} -p ${APORT} "&
    sleep 3
}



function startMessage {
    echo -e "$GRN Starting message app in another terminal... $WHT"
    java -jar $JAR_PATH/${MJAR} -a 127.0.0.1 -p 2100 -s 1 -m testMessage
    sleep 0.3
}

function startNode {
    echo -e "$GRN Creating sites (nodes)... $WHT"
    for i in {1..6}
    do
        java -jar $JAR_PATH/${NJAR} -s $i -a ${AHOST} -p ${APORT} &
    done
    sleep 1
}

function createTree {
    echo -e "$GRN Starting registry and create connections between sites... $WHT"
    java -jar $JAR_PATH/${CJAR} -a ${AHOST} -p ${APORT} -s 1 -d 2
    java -jar $JAR_PATH/${CJAR} -a ${AHOST} -p ${APORT} -s 1 -d 5
    java -jar $JAR_PATH/${CJAR} -a ${AHOST} -p ${APORT} -s 5 -d 6
    java -jar $JAR_PATH/${CJAR} -a ${AHOST} -p ${APORT} -s 2 -d 3
    java -jar $JAR_PATH/${CJAR} -a ${AHOST} -p ${APORT} -s 2 -d 4
    sleep 0.3
}

function createCircle {
    echo -e "$GRN Starting registry and create connections between sites... $WHT"
    java -jar $JAR_PATH/${CJAR} -a ${AHOST} -p ${APORT} -s 1 -d 2
    java -jar $JAR_PATH/${CJAR} -a ${AHOST} -p ${APORT} -s 2 -d 3
    java -jar $JAR_PATH/${CJAR} -a ${AHOST} -p ${APORT} -s 3 -d 4
    java -jar $JAR_PATH/${CJAR} -a ${AHOST} -p ${APORT} -s 4 -d 5
    java -jar $JAR_PATH/${CJAR} -a ${AHOST} -p ${APORT} -s 5 -d 6
    java -jar $JAR_PATH/${CJAR} -a ${AHOST} -p ${APORT} -s 6 -d 1
    sleep 0.3
}



# kill all instances of previous runs
killAll


#AdressBook should be start on another terminal
# start the rmi registry
# startRmi
# start AdressBook
startAdressBook

# Spawn a fixed number of nodes
startNode

# Connects the nodes
createTree
#createCircle

# start the message jar in another terminal
startMessage

echo -e "$RED Type quit to leave the program... $WHT"
#java -jar $JAR_PATH/${AJAR}.jar

# kill everything related to java when leaving
#killJava
