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
AJAR="addressbook.jar"
NJAR="node.jar"
AHOST="127.0.0.1"
APORT="2100"
MJAR="sendMessage.jar"
CJAR="nodeConnect.jar"



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

function startMessage {
    echo -e "$GRN Starting message app in another terminal... $WHT"
    $TERMINAL -e "java -jar $JAR_PATH/${MJAR} 1 testMessage" . &
    sleep 0.3
}

function startNode {
    echo -e "$GRN Creating sites (nodes)... $WHT"
    for i in {1..6}
    do
        java -jar $JAR_PATH/${NJAR} -s $i -a ${AHOST} -p ${APORT} &
    done
    sleep 0.3
}

function createConnections {
    echo -e "$GRN Starting registry and create connections between sites... $WHT"
    java -jar $JAR_PATH/${CJAR} "-s 1 -d 2"
    java -jar $JAR_PATH/${CJAR} "-s 1 -d 5"
    java -jar $JAR_PATH/${CJAR} "-s 5 -d 6"
    java -jar $JAR_PATH/${CJAR} "-s 2 -d 3"
    java -jar $JAR_PATH/${CJAR} "-s 2 -d 4"
    sleep 0.3
}


# kill all instances of previous runs
killAll

set -x
# start the rmi registry
startRmi


# Spawn a fixed number of nodes
startNode

exit
# Connects the nodes
createConnections

# start the message jar in another terminal
startMessage

echo -e "$RED Type quit to leave the program... $WHT"
java -jar $JAR_PATH/${AJAR}.jar

# kill everything related to java when leaving
killJava
