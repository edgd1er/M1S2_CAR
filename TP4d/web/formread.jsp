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
        Edition year: <%= request.getParameter("year")%><BR><%%>
        <br>
        <br>
        <h1>Book(s) in Database</h1>
        
        
        
        <TABLE BORDER="1"> 
            <CAPTION> Book's in database </CAPTION> 
            <TR> 
                <TH> Author </TH> 
                <TH> Title </TH> 
                <TH> Year </TH> 
            </TR> 

            <js:
                    for( book.BookEntity tempBook : myBook.getBooks()){
            out.print("<TR>"); 
            out.print("<TH>"+ tempBook.getBookAuhtor() +"</TH>"); 
            out.print("<TD>"+ tempBook.getBookTitle() +"</TD>"); 
            out.print("<TD>"+ tempBook.getBookYear()+"</TD>"); 
            out.print("</TR>");           
                    }
           %>

        </TABLE> 
        <br>
        <br>
        <a href="formulaire.jsp">Go back to the add Book Form</a>
        <br>
        <a href="addtodb">Go back to the add Book Form</a>
        <br>
        <br>
        <%response.setHeader("Refresh", "5; URL=formulaire.jsp");%>
        <jsp:include page="formulaire.jsp">
            <jsp:param name="title" value="<%=request.getParameter("title")%>"/>
            <jsp:param name="author" value="<%=request.getParameter("author")%>"/>
            <jsp:param name="year" value="<%=request.getParameter("year")%>"/>
        </jsp:include>
    </body>
</html>
