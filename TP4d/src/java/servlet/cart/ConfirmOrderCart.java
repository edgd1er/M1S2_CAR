/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.cart;

import cart.CartEntity;
import cart.CartSessionBeanItfLocal;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author user
 */
@WebServlet(name = "ConfirmOrderCart", urlPatterns = {"/confirmordercart"})
public class ConfirmOrderCart extends HttpServlet {

    @EJB
    private CartSessionBeanItfLocal myCartBean;

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
        String html="", action ="";

        HttpSession session = request.getSession(true);

        CartEntity myCart = (CartEntity) session.getAttribute("myCart");
        html = toolsServlet.servletTools.getinstance().getHtmlCartContents(this.getServletName(),myCart);
        
        action=request.getParameter("action");
        if (action.equals("paid")){
        //TODO 
            //register order into database, knowing the user.
        
        } 

        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ConfirmOrderCart</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ConfirmOrderCart at " + request.getContextPath() + "</h1>");
            out.println(html);
            out.println("</body>");
            out.println("</html>");
            out.println("<br><a href='formulaire.jsp'>Back to form</a>");
            out.println("<br><a href='buyabook?action=clearall'>Remove all items from basket.</a>");
            out.println("<br><a href='confirmordercart?action=paid'>Buy all items in basket.</a>");

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
