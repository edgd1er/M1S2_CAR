<%-- 
    Document   : formulaire
    Created on : 13 avr. 2015, 18:55:10
    Author     : user
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    String title = request.getParameter("title");
    String author = request.getParameter("author");
    String year = request.getParameter("year");

    if (title == null) {
        title = "Enter book's title";
    }
    if (author == null) {
        author = "Enter book's Author";
    }
    if (year == null) {
        year = "Enter book's parudtion date";
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <title>Add a new boot to the library</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <div>This form is used to add a new boo to the library</div>
        <!--Écrire un fichier HTML contenant un formulaire permettant de saisir les informations
relatives à un livre représenté par un titre, un nom d’auteur et une année de parution. -->
        <FORM name="form1" method="post"  ENCTYPE="x-www-form-urlencoded" action="formread.jsp">
            <input name="title" value="<%=title%>" type="text">
            <input name="author" value="<%=author%>" type="text">
            <input name="year" value="<%=year%>" type="text">
            <input type="submit" value="Add new book to library">
        </FORM>   
            <br>
            <a href="init">Create 5 books in database (init)</a>
            <br>
            <a href="cleardb">clear Database (cleardb)</a>
            <br>
            <a href="getauthors">get list of authors (init)</a>
            <br>
    </body>
</html>
