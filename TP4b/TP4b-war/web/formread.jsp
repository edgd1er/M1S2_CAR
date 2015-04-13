<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
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

<%@page import="book.BookSessionImpl"%>
<%@page import="utils.ServiceLocator"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="myBookSession" class="book.BookSessionImpl" scope="session"/> 
<jsp:setProperty name="myBookSession" property="*"/> 
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP: Confirm newly added book</title>
    </head>
    <body>
        <h1>New Book</h1>
        You entered<BR>
    Book's Title: <%= myBookSession.getTitle() %><BR>
    Author <%= myBookSession.getAuthor() %><BR>
    Edition year: <%= myBookSession.getYear() %><BR>
    <% myBookSession.addDB(); %>
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
  
  <%
            for( book.BookEntityImpl tempBook : myBookSession.getBooks()){
    out.print("<TR>"); 
    out.print("<TH>"+ tempBook.getBookAuhtor() +"</TH>"); 
    out.print("<TD>"+ tempBook.getBookTitle() +"</TD>"); 
    out.print("<TD>"+ tempBook.getBookYear()+"</TD>"); 
    out.print("</TR>");           
            }
    %>

</TABLE> 
<br>
<a href="servlet/initServlet">Add 5 books to DB</a>
    <br>
    <br>
    <a href="formulaire.jsp">Go back to the add Book Form</a>

    </body>
</html>
