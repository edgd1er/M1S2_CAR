
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

 @WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
 public class LogoutServlet extends HttpServlet {
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   HttpSession session = request.getSession(false);

   // Destroys the session for this user.
   if (session != null)
        session.invalidate();

   // Redirects back to the initial page.
   response.sendRedirect(request.getContextPath());
  }
 }