/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

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
@WebServlet(name = "addtodb", urlPatterns = {"/addtodb"})
public class addtodb extends HttpServlet {

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
        String ret = "", msg = "", tempTitle = "", tempAuthor = "", tempYear = "";
        int iYear = 0;
        try (PrintWriter out = response.getWriter()) {
            tempTitle = request.getSession().getAttribute("title").toString();
            tempAuthor = request.getSession().getAttribute("author").toString();
            tempYear = request.getSession().getAttribute("year").toString();

            if ((tempAuthor != null) && (tempTitle != null) && (tempYear != null)) {
                if ((tempAuthor.length() > 0) && tempTitle.length() > 0) {
                    try {
                        msg = "Book named " + tempTitle + " written by "
                                + tempAuthor + " in "
                                + tempYear;
                        iYear = Integer.parseInt(tempYear);
                        ret = myBookBean.createBook(tempTitle, tempAuthor, iYear);
                        if (ret.length() < 1) {
                            msg += " was inserted in the database";
                            request.getSession().setAttribute("title", null);
                            request.getSession().setAttribute("author", null);
                            request.getSession().setAttribute("year", null);
                        } else {
                            msg += ret;
                        }
                    } catch (Exception e) {
                        msg += " : error Year parameter is not a number. Please try again. ";
                    }
                } else {

                    msg += (tempAuthor.length() < 1) ? "Error Author is not entered<br>" : "";
                    msg += (tempTitle.length() < 1) ? "Error Title is not entered<br>" : "";
                    msg += (tempYear.length() < 1) ? "Error Year is not entered or invalid <br>" : "";

                }
            } else {
                msg += (tempAuthor == null) ? "Error Author is not entered<br>" : "";
                msg += (tempTitle == null) ? "Error Title is not entered<br>" : "";
                msg += (tempYear == null) ? "Error Year is not entered or invalid <br>" : "";
            }


            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet addtodb</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet addtodb at " + request.getContextPath() + "</h1>");
            out.println(msg);
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
