/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toolsServlet;

import book.BookEntity;
import book.BookSessionBeanItfLocal;
import cart.CartEntity;
import cart.CartSessionBeanItfLocal;
import java.util.Collection;

/**
 *
 * @author user
 */
public class servletTools {

    private static servletTools instance;

    private servletTools() {
    }

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
                try {
                    msg = "Book named " + title + " written by "
                            + author + " in "
                            + strYear;
                    iYear = Integer.parseInt(strYear);
                    ret = myBookBean.createBook(title, author, iYear);
                    if (ret.length() < 1) {
                        msg += " was inserted in the database";

                    } else {
                        msg += ret;
                    }
                } catch (Exception e) {
                    msg += " : error Year parameter is not a number. Please try again. ";
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
     * Return an html code to display books available to sell.
     *
     * @param myBookBean
     * @return
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

}
