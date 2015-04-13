<%-- 
    Document   : formulaire
    Created on : 8 avr. 2015, 17:09:32
    Author     : user
--%>

<%@page import="book.BookSessionImpl"%>
<%@page import="utils.ServiceLocator"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="myBookSession" class="book.BookSessionImpl" scope="session"/> 
<jsp:setProperty name="myBookSession" property="*"/> 

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
            <input name="title" value="<%= myBookSession.getTitle()%>" type="text">
            <input name="author" value="<%= myBookSession.getAuthor()%>"  type="text">
            <input name="year" value="<%=myBookSession.getYear()%>" type="text">
            <input type="submit" value="Add new book to library">
    </FORM>   
    </body>
</html>
