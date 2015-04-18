/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toolsServlet;

import book.BookEntity;
import book.BookSessionBeanItfLocal;
import cart.CartEntity;
import cart.CartSessionBeanImpl;
import cart.CartSessionBeanItfLocal;
import cart.FinalizedCartEntity;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

/**
 * Singleton Class to generate all kind of html code used by servlets
 *
 * @author Francois Dubiez
 */
public class servletTools {

    private static servletTools instance;

    private servletTools() {
    }

    /**
     * return the instance of the singleton.
     *
     * @return a new instance
     */
    public synchronized static servletTools getinstance() {

        if (instance == null) {
            instance = new servletTools();
        }
        return instance;
    }

    /**
     * Provide Html code after book insertion into database or the relevant
     * error message.
     *
     * @param title Book's Title
     * @param author Author's name
     * @param strYear Edition year
     * @param myBookBean SessionBean to handle a book Object
     * @return String representing html code
     */
    public String htmlAdd2Database(String title, String author, String strYear, BookSessionBeanItfLocal myBookBean) {

        String ret = "", msg = "";
        int iYear = 0;

        if ((author != null) && (title != null) && (strYear != null)) {
            if ((author.length() > 0) && title.length() > 0) {
                msg = "Book named " + title + " written by "
                        + author + " in "
                        + strYear;
                try {
                    iYear = Integer.parseInt(strYear);

                } catch (Exception e) {
                    msg += " : error Year parameter is not a number. Please try again. ";
                    return msg;
                }

                try {
                    ret = myBookBean.createBook(title, author, iYear);
                } catch (Exception e) {
                    msg += ". Error:  " + e.getMessage() + e.getCause();

                }
                if (ret.length() < 1) {
                    msg += " was inserted in the database";

                } else {
                    msg += ret;
                }

            } else {

                msg += (author.length() < 1) ? "Error Author is not entered<br>" : "";
                msg += (title.length() < 1) ? "Error Title is not entered<br>" : "";
                msg += (strYear.length() < 1) ? "Error Year is not entered or invalid <br>" : "";

            }
        } else {
            msg += (author == null) ? "Error Author is not entered<br>" : "";
            msg += (title == null) ? "Error Title is not entered<br>" : "";
            msg += (strYear == null) ? "Error Year is not entered or invalid <br>" : "";
        }

        return msg;
    }

    /**
     * Produce a table with Books existing in database and available to sell.
     *
     * @param servletUrlToAddToCart Url to use to add a book to basket
     * @param myBookBean EJB to access to database
     * @return String: html code
     */
    public String getHtmlBooksToBuy(String servletUrlToAddToCart, BookSessionBeanItfLocal myBookBean) {

        Collection<BookEntity> books = myBookBean.getBooks();

        String html = "";

        html = "<h2>No books found in database</h2>";
        if (books.size() > 0) {

            html = "<table border ='1'><tr><TH>Titre</TH><TH>Author</TH><TH>Year</TH><th>Click to add to basket</TH></tr>";
            for (BookEntity tempbook : books) {
                html += "<tr><td>" + tempbook.getBookTitle() + "</td>";
                html += "<td>" + tempbook.getBookAuthor() + "</td>";
                html += "<td>" + String.valueOf(tempbook.getBookYear()) + "</td>";
                html += "<td><a href=\"" + servletUrlToAddToCart + "?id=" + tempbook.getBookTitle() + "&action=add\">Add to cart</td>";

            }
            html += "</table>";
        }

        return html;
    }

    /**
     * Return an html code to display books existing in database.
     *
     * @param myBookBean EJB to acces to database
     * @return html code
     */
    public String getHtmlBooksToDisplay(BookSessionBeanItfLocal myBookBean) {

        String html = "";
        Collection<BookEntity> books = myBookBean.getBooks();
        html = "<h2>No books found in database</h2>";
        if (books.size() > 0) {

            html = "<table><tr><TH>Titre</TH><TH>Author</TH><TH>Year</TH></tr>";
            for (BookEntity tempbook : books) {
                html += "<tr><td>" + tempbook.getBookTitle() + "</td>";
                html += "<td>" + tempbook.getBookAuthor() + "</td>";
                html += "<td>" + String.valueOf(tempbook.getBookYear()) + "</td>";
                html += "</td></tr>";
            }
            html += "</table>";
        }
        return html;
    }

    /**
     * Produce a table with Author of books existing in database
     *
     * @param myBookBean EJB to access Database
     * @return html code.
     */
    public String getHtmlToDisplayAuthors(BookSessionBeanItfLocal myBookBean) {

        String html;
        Collection<String> authors;
        try {
            authors = myBookBean.getAuthors();
            html = "<h2>No authors found in database</h2>";
            if (authors.size() > 0) {
                html = "<table><tr><TH>Author</TH></tr>";
                for (String author : authors) {
                    html += "<tr><td>" + author + "</td></tr>";
                }
                html += "</table>";
            }

        } catch (Exception e) {
            html = "<h1>Error while retrieving authors in database (" + e.getMessage() + ")</h1>";
        }

        return html;
    }

    /**
     * Produce a table with the content of a Basket
     *
     * @param servletUrlToAddToCart url used to add/remove an item
     * @param myCart basket object that contains list of books to buy.
     * @return html code
     */
    public String getHtmlCartContents(String servletUrlToAddToCart, CartEntity myCart) {
        String html;
        Collection<String> booksInCart = myCart.getContents();
        html = "<h2>Cart is empy.</h2>";

        if (booksInCart.size() > 0) {
            html = "<table><tr><TH>Books in Cart</TH></tr>";
            for (String book : booksInCart) {
                html += "<tr><td><a href=\"" + servletUrlToAddToCart + "?id="
                        + book
                        + "&action=remove\" alt=\"Remove this item from basket\">" + book + "</a></td></tr>";

            }
            html += "</table>";
        }

        return html;
    }

    /**
     * Produce an html code of the items contained in a finished Order.
     *
     * @param myCart
     * @return html code
     */
    public String getHtmlBooksBought(FinalizedCartEntity myCart) {
        String html;
        Collection<BookEntity> booksInCart = myCart.getBooks();
        html = "<h2>Cart is empy.</h2>";

        if (booksInCart.size() > 0) {
            html = "Order No:" + myCart.getOrderId() + "<BR>ClientId: " + myCart.getClientId()
                    + "<table><tr><TH>Books in Cart</TH></tr>";
            for (BookEntity book : booksInCart) {
                html += "<tr><td>" + book.getBookTitle() + "</td>";
                html += "<td>" + book.getBookAuthor() + "</td>";
                html += "<td>" + String.valueOf(book.getBookYear()) + "</td>";
                html += "</td></tr>";
            }
            html += "</table>";
        }
        return html;
    }

    /**
     * Return the content of an order
     *
     * @param myCart
     * @return html Code
     */
    public String getHtmlLastOrder(CartSessionBeanItfLocal myCartSession) {
        String html = null;
        Collection<BookEntity> books = null;
        FinalizedCartEntity tempFinCartEntity = null;
        html="<h2>No order found</h2>";

        tempFinCartEntity = myCartSession.getLastOrder();
        
        if (tempFinCartEntity!=null){
        html = "<table><tr><TH>Clientid</th><th>OrderId</th><th>Title</TH><TH>Author</TH><TH>Year</TH></tr>";

        for (BookEntity be : tempFinCartEntity.getBooks()) {
            html += "<tr><td>" + tempFinCartEntity.getClientId()
                    + "</td><td>" + tempFinCartEntity.getOrderId()
                    + "</td><td>" + be.getBookTitle()
                    + "</td><td>" + be.getBookAuthor()
                    + "</td><td>" + String.valueOf(be.getBookYear()) + "</td></tr>";
        }

        html += "</table>";}
        return html+getMenuLinks();
    }

    public String htmlGetSearchForms(String urlSearch, String title, String author, String year) {

        String html = "<FORM name=\"formSearchTitle\" method=\"post\"  ENCTYPE=\"x-www-form-urlencoded\" action=\"" + urlSearch + "\">"
                + "search by title<br>"
                + "<input type=\"radio\" name=\"type\" value=\"title\" checked/>Titre"
                + "<input name=\"title\" value=\"" + title + "\" type=\"text\"><br>"
                + "<input type=\"radio\" name=\"type\" value=\"author\" />Author"
                + "<input name=\"author\" value=\"" + author + "\" type=\"text\"><br>"
                + "<input type=\"radio\" name=\"type\" value=\"year\" />Year"
                + "<input name=\"year\" value=\"" + year + "\" type=\"text\"><br>"
                + "<input type=\"submit\" value=\"Search for a book by a title\">"
                + "</FORM>";

        return html;
    }

    public String htmlGetResultsForAuhtor(BookSessionBeanItfLocal myBookBean, String author) {

        String html = null;
        Collection<BookEntity> books = null;

        books = myBookBean.getBooksByAuthors(author.toLowerCase());
        html = "<h2>No book found with author whose name contains " + author + ".</h2>";

        if (books.size() > 0) {
            html = "<table><tr><TH>Title</TH><TH>Author</TH><TH>Year</TH></tr>";
            for (BookEntity book : books) {
                html += "<tr><td>" + book.getBookTitle() + "</td>";
                html += "<td>" + book.getBookAuthor() + "</td>";
                html += "<td>" + String.valueOf(book.getBookYear()) + "</td>";
                html += "</td></tr>";
            }
            html += "</table>";
        }
        return html;
    }

    public String htmlGetResultsForTitle(BookSessionBeanItfLocal myBookBean, String title) {
        String html = null;
        Collection<BookEntity> books = null;

        books = myBookBean.getBooksByTitle(title.toLowerCase());
        html = "<h2>No book found whose title contains " + title + ".</h2>";

        if (books.size() > 0) {
            html = "<table><tr><TH>Title</TH><TH>Author</TH><TH>Year</TH></tr>";
            for (BookEntity book : books) {
                html += "<tr><td>" + book.getBookTitle() + "</td>";
                html += "<td>" + book.getBookAuthor() + "</td>";
                html += "<td>" + String.valueOf(book.getBookYear()) + "</td>";
                html += "</td></tr>";
            }
            html += "</table>";
        }
        return html;
    }

    public String htmlGetResultsForYear(BookSessionBeanItfLocal myBookBean, String year) {
        String html = null;
        int iYear = 0;
        Collection<BookEntity> books = null;

        try {
            iYear = Integer.parseInt(year);
        } catch (Exception e) {
            return "<h2>Year input is not a number.</h2>";
        }

        books = myBookBean.getBooksByYear(iYear);
        html = "<h2>No book found edited in  " + year + ".</h2>";

        if (books.size() > 0) {
            html = "<table><tr><TH>Title</TH><TH>Author</TH><TH>Year</TH></tr>";
            for (BookEntity book : books) {
                html += "<tr><td>" + book.getBookTitle() + "</td>";
                html += "<td>" + book.getBookAuthor() + "</td>";
                html += "<td>" + String.valueOf(book.getBookYear()) + "</td>";
                html += "</td></tr>";
            }
            html += "</table>";
        }
        return html;
    }

    public String htmlGetResultsForSearch(BookSessionBeanItfLocal myBookBean, String title, String author, String year, String crit) {
        // html list a founf books
        String html2 = "";

        switch (crit) {

            case "title":
                html2 = toolsServlet.servletTools.getinstance().htmlGetResultsForTitle(myBookBean, title);
                break;
            case "author":
                html2 = toolsServlet.servletTools.getinstance().htmlGetResultsForAuhtor(myBookBean, author);
                break;
            case "year":
                html2 = toolsServlet.servletTools.getinstance().htmlGetResultsForYear(myBookBean, year);
                break;
            default:
                break;
        }

        html2 += getMenuLinks();
        return html2;
    }

    public String getMenuLinks() {

        return "<br>"
                + "<a href=\"cleardb\">clear Database (cleardb)</a>"
                + "<br>"
                + "<a href=\"initbooks\">Create 5 books in database (init)</a>"
                + "<br>"
                + "<a href=\"getauthors\">get list of authors (getauthors)</a>"
                + "<br>"
                + "<a href=\"displaybooks\">get list of books (displaybooks)</a>"
                + " <br>"
                + "<a href=\"searchbook\">search for a book (searchbook)</a>"
                + "<br>"
                + "<a href=\"buyabook\">get list of books to buy (buyabook)</a>"
                + "<br>"+ 
                "<a href=\"getlastorder\">get detail of the last order (getlastorder)</a>"
                + "<br>"
                + "<a href=\"logout\">logout (logout)</a>"
                + "<br>";

    }

    public void persist(Object object) {
        /* Add this to the deployment descriptor of this module (e.g. web.xml, ejb-jar.xml):
         * <persistence-context-ref>
         * <persistence-context-ref-name>persistence/LogicalName</persistence-context-ref-name>
         * <persistence-unit-name>TP4dPU</persistence-unit-name>
         * </persistence-context-ref>
         * <resource-ref>
         * <res-ref-name>UserTransaction</res-ref-name>
         * <res-type>javax.transaction.UserTransaction</res-type>
         * <res-auth>Container</res-auth>
         * </resource-ref> */
        try {
            Context ctx = new InitialContext();
            UserTransaction utx = (UserTransaction) ctx.lookup("java:comp/env/UserTransaction");
            utx.begin();
            EntityManager em = (EntityManager) ctx.lookup("java:comp/env/persistence/LogicalName");
            em.persist(object);
            utx.commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }
    }
}
