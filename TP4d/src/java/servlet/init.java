/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import book.BookEntity;
import book.BookSessionBeanLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author user
 */
@WebServlet(name = "init", urlPatterns = {"/init"})
public class init extends HttpServlet {

    @EJB
    private BookSessionBeanLocal myBookBean;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String tempTitle, tempAuthor;
        int tempyear;

        try (PrintWriter out = response.getWriter()) {
            tempTitle = "Livre1";
            tempAuthor = "Author1";
            tempyear = 2001;
            try {
                myBookBean.createBook(tempTitle, tempAuthor, tempyear);
            } catch (Exception e) {
                out.println("<h1>Error while inserting book named " + tempTitle + 
                        " written by " + tempTitle + " in " + tempyear + " ("+e.getMessage()+ ")</h1>");
            }
            myBookBean.createBook("Livre2", "Author2", 2002);
            myBookBean.createBook("Livre3", "Author3", 2003);
            myBookBean.createBook("Livre4", "Author4", 2001);
            myBookBean.createBook("Livre5", "Author1", 2004);

            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet init</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet init at " + request.getContextPath() + ": Existing books in DB</h1>");

            out.println("<table><tr><TH>Titre</TH><TH>Author</TH><TH>Year</TH></tr>");
            Collection<BookEntity> books = myBookBean.getBooks();
            for (BookEntity tempbook : books) {
                out.println("<tr><td>" + tempbook.getBookTitle() + "</td>");
                out.print("<td>" + tempbook.getBookAuhtor() + "</td>");
                out.print("<td>" + String.valueOf(tempbook.getBookYear()) + "</td></tr>");
            }
            out.println("</table>");

            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
