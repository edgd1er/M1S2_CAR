<%-- 
    Document   : formulaire
    Created on : 8 avr. 2015, 17:09:32
    Author     : user
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="myBook" class="book.BookSessionItf" scope="session"/> 


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Form to add a book</title>
    </head>
    <body>
        <div>This form is used to add a new boo to the library</div>
        <!--Écrire un fichier HTML contenant un formulaire permettant de saisir les informations
relatives à un livre représenté par un titre, un nom d’auteur et une année de parution. -->
        <FORM name="form1" method="post"  ENCTYPE="x-www-form-urlencoded" action="formread.jsp">
            <input name="title" value="<%= myBook.getTitle()%>" type="text">
            <input name="author" value="<%= myBook.getAuthor()%>"  type="text">
            <input name="year" value="<%=myBook.getYear()%>" type="text">
            <input type="submit" value="Add new book to library">
    </FORM>   
    </body>
</html>
