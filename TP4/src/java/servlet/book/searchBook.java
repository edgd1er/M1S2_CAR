/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.book;

import book.BookSessionBeanItfLocal;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "searchBook", urlPatterns = {"/searchbook"})
public class searchBook extends HttpServlet {

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
        String title, author, year, crit, html, html2;
        title = request.getParameter("title");
        author = request.getParameter("author");
        year = request.getParameter("year");
        crit = request.getParameter("type");

        try {
            title = title.isEmpty() ? "" : title;
        } catch (Exception e) {
            title = "";
        }
        try {
            author = author.isEmpty() ? "" : author;
        } catch (Exception e) {
            author = "";
        }
        try {
            year = year.isEmpty() ? "" : year;
        } catch (Exception e) {
            year = "";
        }
        try{
            crit = crit.isEmpty() ? "" : crit;
        } catch (Exception e) {
            crit = "none";
        
        }

// html code for crteria search
         html = toolsServlet.servletTools.getinstance().htmlGetSearchForms(this.getServletName().toLowerCase(), title, author, year);
         
         html2=toolsServlet.servletTools.getinstance().htmlGetResultsForSearch(myBookBean,title,author,year, crit);


        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet searchBook</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet searchBook at " + request.getContextPath() + "</h1>");
            out.println(html);
            out.println("results");
            out.println(html2);
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
        return "This page allows to search for a book according different criteria";
    }// </editor-fold>

}
