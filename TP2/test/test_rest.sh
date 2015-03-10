#!/bin/bash
#
#
# Script de validation fonctionnel du serveur REST sur un accès serveur FTP en Java 
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

login="user"
password="a"
urlstart="http://localhost:8080/rest/api/"
efile="baboon_gray.png"
nefile="baboon_gray.png1"

## Fonctions

function assert {
if [ $totest -eq "" ]; then 
	echo "test"
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



cp ../TP1/tests/${efile} /tmp/homedir/user/


url="${urlstart}ftp"
res=$(curl -s ${url})
echo -e "\nRest Welcome message: "${res}



url="${urlstart}ftp/welcome"
res=$(curl -s ${url})
echo -e "\nFTP Server welcome message: "${res}

url="${urlstart}ftp/login/${login}/password/${password}"
res=$(curl -s ${url})
echo -e "\nFTP Server login: $url"$res

url="${urlstart}ftp/list"
res=$(curl -s ${url})
echo -e "\nFTP Server list"$res


url="${urlstart}ftp/getfile/${efile}"
param="-s -X GET "
res=$(curl $param ${url}-o test_file.png)
echo -e "\nFTP GetFile with GET: $url : $res"


#url="http://localhost:8080/rest/api/ftp/getfile/Class_tools.svg"
#param="-s -d path=/tmp/homedir/user -X POST -d file=Class_tools.svg"
#res=$(curl $param ${url})
#echo -e "\nFTP GetFile with POST : $url : $res"


url="${urlstart}ftp/delete/${nefile}"
param="-s -X delete"
res=$(curl $param ${url})
echo -e "\nFTP delete not existing file with Delete No param : $url : $res"

url="${urlstart}ftp/delete/${efile}"
param="-s -X delete"
res=$(curl $param ${url})
echo -e "\nFTP delete existing file with Delete No param : $url : $res"






