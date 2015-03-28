#!/bin/sh
#
#
#
#java -jar adressbook.jar -p 2100

java -jar node.jar -a 127.0.0.1 -p 2100 -s 1
java -jar node.jar -a 127.0.0.1 -p 2100 -s 2
java -jar node.jar -a 127.0.0.1 -p 2100 -s 3
java -jar node.jar -a 127.0.0.1 -p 2100 -s 4
java -jar node.jar -a 127.0.0.1 -p 2100 -s 5

java -jar nodeconnect.jar -a 127.0.0.1 -p 2100 -s 1 -d 2
java -jar nodeconnect.jar -a 127.0.0.1 -p 2100 -s 2 -d 3
java -jar nodeconnect.jar -a 127.0.0.1 -p 2100 -s 4 -d 5

