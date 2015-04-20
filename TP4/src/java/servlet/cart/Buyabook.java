/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet.cart;

import book.BookSessionBeanItfLocal;
import cart.CartEntity;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import toolsServlet.servletTools;

/**
 * Servlet to request confirmation of an order
 *
 * @author user
 */
@WebServlet(name = "buyabook", urlPatterns = {"/buyabook"})
public class Buyabook extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @EJB
    private BookSessionBeanItfLocal myBookBean;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        CartEntity myCart = null;
        String book2buy = request.getParameter("id");
        String action = request.getParameter("action");
        
        action = (action == null)?"none":action;

        if (!action.isEmpty()) {
            HttpSession session = request.getSession(true);
            myCart = (CartEntity) session.getAttribute("myCart");
            if (myCart == null) {
                myCart = new CartEntity();
            }
            
            if (action.equals("add")){ myCart.add(book2buy);}
            if (action.equals("remove")){ myCart.remove(book2buy);}
            if (action.equals("clearall")){ myCart = new CartEntity();}
            session.setAttribute("myCart", myCart);
        }

        String html = servletTools.getinstance().getHtmlBooksToBuy(this.getServletName(), myBookBean);
        String html2 = servletTools.getinstance().getHtmlCartContents(this.getServletName(),myCart);

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet buyabook</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet buyabook at " + request.getContextPath() + "</h1>");
            out.println(html);
            out.println("<br><a href='formulaire.jsp'>Back to form</a>");
            out.println("<br><a href=\"" + this.getServletName() + "?action=clearall\">Remove all items from basket</td>");
            out.println("<br><a href='confirmordercart?action=paying'>Proceed to checkout</a>");
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
        return "Short description";
    }// </editor-fold>

}
