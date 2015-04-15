<%-- 
    Document   : formread
    Created on : 8 avr. 2015, 16:37:13
    Author     : user
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>


<jsp:useBean id="myBook" class="book.BookSessionBean" scope="session"/> 


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP: Confirm newly added book</title>
    </head>
    <body>
        <h1>New Book</h1>
        You entered<BR>
        Book's Title: <%= request.getParameter("title")%><BR>
        Author <%= request.getParameter("author")%><BR>
        Edition year: <%= request.getParameter("year")%><BR>

        
        <% //adding parameters to session for future use
            session.setAttribute("year", request.getParameter("year"));
            session.setAttribute("title", request.getParameter("title"));
            session.setAttribute("author", request.getParameter("author"));
        %>
        <br>
        <br>
        <a href="addtodb">add this book to database</a>

        <br>
        <h1>Book(s) in Database</h1>
        <jsp:include page="displaybooks"/>

        <br>
        <a href="formulaire.jsp">Go back to the add Book Form</a>
        <br>
        <br>
        <br>
        <%response.setHeader("Refresh", "15; URL=formulaire.jsp");%>
        <!-- example of jsp include page with parameters send in addition -->
        <jsp:include page="formulaire.jsp">
            <jsp:param name="title" value="<%=request.getParameter("title")%>"/>
            <jsp:param name="author" value="<%=request.getParameter("author")%>"/>
            <jsp:param name="year" value="<%=request.getParameter("year")%>"/>
        </jsp:include>
    </body>
</html>
