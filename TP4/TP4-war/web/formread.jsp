<%-- 
    Document   : formread
    Created on : 8 avr. 2015, 16:37:13
    Author     : user
--%>

<%@page import="javax.naming.Context"%>
<%@page import="javax.naming.InitialContext"%>
<%--
     Context ic = new InitialContext();
     BookSessionGetDataFromForm myBook = (BookSessionGetDataFromForm) ic.lookup("myBook");
     if (myBook==null){
     
     }
            --%>

<%@page import="book.BookSessionGetDataFromForm"%>
<%@page import="utils.ServiceLocator"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="myBook" class="book.BookSessionGetDataFromForm" scope="session"/> 
<jsp:setProperty name="myBook" property="*"/> 
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP: Confirm newly added book</title>
    </head>
    <body>
        <h1>New Book</h1>
        You entered<BR>
    Book's Title: <%= myBook.getTitle() %><BR>
    Author <%= myBook.getAuthor() %><BR>
    Edition year: <%= myBook.getYear() %><BR><% %>
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
  
    <%--
            for( book.BookEntity tempBook : myBook.getBooks()){
    out.print("<TR>"); 
    out.print("<TH>"+ tempBook.getBookAuhtor() +"</TH>"); 
    out.print("<TD>"+ tempBook.getBookTitle() +"</TD>"); 
    out.print("<TD>"+ tempBook.getBookYear()+"</TD>"); 
    out.print("</TR>");           
            }
    --%>
  
</TABLE> 

    <br>
    <br>
    <a href="formulaire.jsp">Go back to the add Book Form</a>

    </body>
</html>
