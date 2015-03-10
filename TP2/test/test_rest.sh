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
	
	res=$(curl ${param} ${url} -o temp.log 2>&1)
	
	oracle=$(grep -i "$expect" temp.log)
	if [ "$oracle" == "" ]; then 
	res="\033[0;31m Erreur: \n"$res$(cat temp.log)"\033[0m"
		else 
	res="\033[0;32mOK\033[0m"
fi
echo -e "$txt $res"
}




## copy of test file for download ##
cp ${efile} /tmp/homedir/user/

curl -s -o  /dev/null ${urlstart}ftp/logout

###############################
# tests fonctionnels

## curl parameters i: with header s:silent
param="-is"

echo -e "\n"
echo "#################################################"
echo "##       tests welcome msg REST                ##"
echo "#################################################"


url="${urlstart}ftp"
expect="This is a Web gateway"
txt="\nRest Welcome message: "
testoracle


url="${urlstart}ftp/welcome"
expect="220  	Service ready for new user on"
txt="\nFTP Through Rest Welcome message: "
testoracle


echo -e "\n"
echo "#################################################"
echo "##       test login/list   msg                 ##"
echo "#################################################"


url="${urlstart}ftp/login/${login}/password/zz"
expect="no login, password found."
txt="unrecognized user, should be warned about the error"
testoracle

url="${urlstart}ftp/login/${login}/password/${password}"
expect="Location: http://localhost:8080/rest/api/ftp/list"
txt="logged user, should be redirected to list page"
testoracle

url="${urlstart}ftp/list"
expect="baboon_gray.png"
txt="logged user, should see the file baboon_gray.png in homedir/"${login}
testoracle



echo -e "\n"
echo "#################################################"
echo "##   test get file/ put file for logged user   ##"
echo "#################################################"

param="-is -X GET "
url="${urlstart}ftp/getfile/${efile}"
expect='Content-Disposition: attachment; filename="baboon_gray.png'
txt="Logged user, should be able to download a file:"
testoracle

param="-is -X GET "
url="${urlstart}ftp/getfile/${nefile}"
expect="File not found: 550"
txt="Logged user, should not be able to download a not existing file:"
testoracle

param="-is -X POST "
url="${urlstart}ftp/postfile/${efile}"
expect="File not found: 550"
txt="Logged user, should not be able to upload an existing file:"
testoracle


echo -e "\n"
echo "#################################################"
echo "##       test logout                           ##"
echo "#################################################"

param="-is -X GET "
url="${urlstart}ftp/logout"
expect="Location: http://localhost:8080/rest/api/ftp/LoginForm"
txt="logged out user, should be redirected to login page"
testoracle


echo -e "\n"
echo "#################################################"
echo "## test get file/ put / detete file for not logged user ##"
echo "#################################################"

param="-is -X GET "
url="${urlstart}ftp/getfile/${efile}"
expect='No login, password found'
txt="Not logged user, should not be able to download a file:"
testoracle

param="-is -X GET"
url="${urlstart}ftp/delete?path=%2ftmp%2fhomedir%2f&file=${efile}"
expect='No login, password found'
txt="Not logged user, should not be able to delete a file through get method:"
testoracle


param="-is -X DELETE"
url="${urlstart}ftp/delete/${efile}"
expect='No login, password found'
txt="Not logged user, should not be able to delete a file through DELETE method:"
testoracle



echo -e "\033[1;34m#################################################"
echo "TODO"
echo " login post, lgout delete, getfile queryparam"
echo " getloginform, getuploadForm, uploadfile"
echo "#################################################"
echo -e "#################################################\033[0m"



