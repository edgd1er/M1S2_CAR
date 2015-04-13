/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

import java.util.Collection;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author user
 */
@Stateful
public class BookSessionBean implements BookSessionBeanLocal {

    @PersistenceContext(unitName = "TP4dPU")
    private EntityManager bem;

    public String createBooks() {
        String tempTitle, tempAuthor, strError = "";
        int tempyear;

        tempTitle = "Livre1";
        tempAuthor = "Author1";
        tempyear = 2001;
        try {
            this.createBook(tempTitle, tempAuthor, tempyear);
        } catch (Exception e) {
            strError += "<h1>Error while inserting book named " + tempTitle
                    + " written by " + tempTitle + " in " + tempyear + " (" + e.getMessage() + ")</h1>";
        }
        tempTitle = "Livre2";
        tempAuthor = "Author2";
        tempyear = 2002;
        try {
            this.createBook(tempTitle, tempAuthor, tempyear);
        } catch (Exception e) {
            strError += "<h1>Error while inserting book named " + tempTitle
                    + " written by " + tempTitle + " in " + tempyear + " (" + e.getMessage() + ")</h1>";
        }
        tempTitle = "Livre3";
        tempAuthor = "Author3";
        tempyear = 2003;
        try {
            this.createBook(tempTitle, tempAuthor, tempyear);
        } catch (Exception e) {
            strError += "<h1>Error while inserting book named " + tempTitle
                    + " written by " + tempTitle + " in " + tempyear + " (" + e.getMessage() + ")</h1>";
        }
        tempTitle = "Livre4";
        tempAuthor = "Author4";
        tempyear = 2001;
        try {
            this.createBook(tempTitle, tempAuthor, tempyear);
        } catch (Exception e) {
            strError += "<h1>Error while inserting book named " + tempTitle
                    + " written by " + tempTitle + " in " + tempyear + " (" + e.getMessage() + ")</h1>";
        }
        tempTitle = "Livre5";
        tempAuthor = "Author1";
        tempyear = 2004;
        try {
            this.createBook(tempTitle, tempAuthor, tempyear);
        } catch (Exception e) {
            strError += "<h1>Error while inserting book named " + tempTitle
                    + " written by " + tempTitle + " in " + tempyear + " (" + e.getMessage() + ")</h1>";
        }
        return strError;
    }

    @Override
    public String createBook(String Title, String Author, int iYear) {
        String ret = "";
        if ((Title != null) && (Author != null)) {
            if ((Title.length() > 0) && (Author.length() > 0) && (iYear > 0)) {
                BookEntity book = new BookEntity(Title, Author, iYear);
                bem.persist(book);
            } else {
                ret = "Error while creating book " + Title + " / " + Author + " / " + iYear;

            }
        } else {
            ret = "Error while creating book " + Title + " / " + Author + " / " + iYear;
        }

        return ret;
    }

    @Override
    public String add2db(BookEntity book) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeFromdb(BookEntity book) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String removeAllFromDB() {
        String msg = "";
        try {
            Query q = bem.createNamedQuery("books.deleteall");
            q.executeUpdate();
        } catch (Exception e) {
            msg = e.getMessage();
        }

        return msg;
    }

    @Override
    public void add2basket(BookEntity book) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removefromBasket(BookEntity book) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<BookEntity> getBooks() {
        Query q = bem.createNamedQuery("books.getallbooks");
        Collection<BookEntity> books = q.getResultList();
        return books;
    }

    @Override
    public Collection<String> getAuthors() {
        Query q = bem.createNamedQuery("books.AllAuthors");
        Collection<String> authors = q.getResultList();
        return authors;
    }
    
    @Override
    public Collection<BookEntity> getBasketContent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
