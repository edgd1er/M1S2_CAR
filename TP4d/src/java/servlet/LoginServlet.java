package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.annotation.security.PermitAll;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@PermitAll
@WebServlet(name="LoginServlet", urlPatterns={"/loginservlet"})
public class LoginServlet extends HttpServlet {
   

    /**
     * Processes requests for both HTTP <code>GET</code> 
     *    and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void service(HttpServletRequest request, 
            HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet "+this.getServletName()+"</title>");
            out.println("</head>");
            out.println("<body>");
            request.login("User", "Password");
         
            out.println("</body>");
            out.println("</html>");
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            request.logout();
            out.close();
        }
    }
}