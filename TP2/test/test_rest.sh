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
e1file="test.jpg"
nefile="baboon_gray.png1"

## Fonctions

function assert {
if [ $totest -eq "" ]; then 
	echo "test"
fi
}

# verification de l oracle
function testoracle {
	rm temp.log
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
cp ${e1file} /tmp/homedir/anonymous/

curl -s -o  /dev/null ${urlstart}ftp/logout

###############################
# tests fonctionnels

## curl parameters i: with header s:silent
param="-is"

echo -e "\n"
echo "#################################################"
echo "##       tests welcome msg REST                ##"
echo "#################################################"

param="-is -X GET "
url="${urlstart}ftp"
expect="This is a Web gateway"
txt="\nRest Welcome message: "
testoracle

param="-is -X GET "
url="${urlstart}ftp/welcome"
expect="220  	Service ready for new user on"
txt="\nFTP Through Rest Welcome message: "
testoracle


echo -e "\n"
echo "#################################################"
echo "##       test login/list   msg                 ##"
echo "#################################################"

param="-is -X GET "
url="${urlstart}ftp/login/${login}/password/zz"
expect="no login, password found."
txt="unrecognized user, should be warned about the error"
testoracle

param="-is -X GET "
url="${urlstart}ftp/login/${login}/password/${password}"
expect="Location: http://localhost:8080/rest/api/ftp/list"
txt="logged user, should be redirected to list page"
testoracle

param="-is -X GET "
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

param="-is -X GET "
url="${urlstart}ftp/getfile?path=%2Ftmp%2Fhomedir%2Fanonymous%2F${e1file}"
expect="Content-Type: application/octet-stream"
txt="Logged user, should be able to download a file giving a different path and name:"
testoracle

#### TO CORRECT ###
param="-is --data-binary fname=${e1file} --data-binary filename@{e1file} " 
url="${urlstart}ftp/uploadfilepost?file=${e1file}"
expect="File Uploaded to FTP server"
txt="Logged user, should be able to upload a file (POST, --data-urlencode )"
testoracle

#### TO CORRECT ###
param="-is -X PUT --data-binary ${e1file} "
url="${urlstart}ftp/uploadput?file=%2Ftmp%2Fhomedir%2Fanonymous%2F${e1file}"
expect="XXContent-Type: application/octet-stream"
txt="Logged user, should not be able to upload a file (PUT, -data-binary):"
#testoracle

#### TO CORRECT ###
param="-is -X POST --form key1=value1 -F press=submit  -H file=1 -F fform=@/tmp/homedir/anonymous/${e1file}"
#param="-is --data-urlencode name@/tmp/homedir/anonymous/${e1file}"
url="${urlstart}ftp/uploadfilepost"
expect="HTTP/1.1 200 OK"
txt="Logged user, should be able to upload a file (POST, --data-urlencode ):"
#testoracle
 
# curl -F "web=@index.html;type=text/html" url.com
# curl --form upload=@localfilename --form press=OK [URL]
#### TO CORRECT ###
param="-is -X PUT "
url="${urlstart}ftp/uploadput?file=${efile}"
expect="File not found: 550"
txt="Logged user, should not be able to post an existing file(PutFile):"
#testoracle

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

param="-is -X PUT --data /tmp/homedir/${efile}"
url="${urlstart}ftp/uploadput?file=%2ftmp%2fhomedir%2f${efile}"
expect='No login, password found'
txt="Not logged user, should not be able to delete a file through get method:"
testoracle
 

param="-is -X DELETE"
url="${urlstart}ftp/delete/${efile}"
expect='No login, password found'
txt="Not logged user, should not be able to delete a file through DELETE method:"
testoracle

echo -e "\n"
echo "#################################################"
echo "## test des formulaires                        ##"
echo "#################################################"

param="-is -X GET "
url="${urlstart}ftp/LoginForm"
expect='action="loginPost"'
txt="Not logged user, should be able to see the loginForm:"
testoracle

param='-is -X POST -H Content-type:application/x-www-form-urlencoded --data lname=user&lpass=a'
url="${urlstart}ftp/loginPost"
expect='Location: http://localhost:8080/rest/api/ftp/list'
txt="Not logged user, should be able to login:"
testoracle

echo -e "\n"
echo "#################################################"
echo "## test du logout via un delete                 ##"
echo "#################################################"


param='-is -X DELETE '
url="${urlstart}ftp/logout"
expect='Location: http://localhost:8080/rest/api/ftp/LoginForm'
txt="logged user, should be able to logout through a delete request:"
testoracle


echo -e "\033[1;34m#################################################"
echo -e "#################################################\033[0m"



