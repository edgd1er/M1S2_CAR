/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import book.BookEntity;
import book.BookSessionBeanItfLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import javax.ejb.EJB;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to request database add of 5 books.
 *
 * @author user
 */
@WebServlet(name = "initbooks", urlPatterns = {"/initbooks"})
@TransactionManagement(TransactionManagementType.BEAN)
public class InitBooks extends HttpServlet {

    @EJB
    private BookSessionBeanItfLocal myBookBean;

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
        String html, msg = "";

        msg = myBookBean.createBooks();
        html = toolsServlet.servletTools.getinstance().getHtmlBooksToDisplay(myBookBean);

        try (PrintWriter out = response.getWriter()) {
            response.setHeader("Refresh", "5; URL=formulaire.jsp");
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet init</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet init at " + request.getContextPath() + ": Existing books in DB</h1>");
            out.println(msg);
            out.println(html);
            out.println("<br><a href='formulaire.jsp'>Back to form</a>");
            out.println("</body>");
            out.println("</html>");
        } catch (Exception e) {
            PrintWriter out = response.getWriter();
            out.println(e.getMessage() + ":" + e.getCause());
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
