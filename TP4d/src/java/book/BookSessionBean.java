/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

import java.util.Collection;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

/**
 * BookSessionBean exposes all services to handle a BookEntity
 *
 * @author Francois Dubiez
 */
@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
public class BookSessionBean implements BookSessionBeanItfLocal {

    @PersistenceContext(unitName = "TP4dPU")
    private EntityManager bem;

    @Resource
    private UserTransaction ut;

    /**
     * Populate database with 5 books
     *
     * @return creation error message
     */
    //@RolesAllowed("users") // only members of users role can access
    public String createBooks() {
        String ret, msg = "";
        String[][] aBooks = new String[][]{
            {"Livre1", "Author1", "2001"},
            {"Livre2", "Author2", "2002"},
            {"Livre3", "Author3", "2003"},
            {"Livre4", "Author4", "2001"},
            {"Livre5", "Author1", "2004"}};

        for (String[] abook : aBooks) {
            ret = this.createBook(abook[0], abook[1], Integer.parseInt(abook[2]));

            if (ret.length() < 1) {
                msg += "Book named " + abook[0] + " written by "
                        + abook[1] + " in " + abook[2];
                msg += " was inserted in the database<br>";
            } else {
                msg += ret + "<br>";
            }

        }
        return msg;
    }

    /**
     * create an object book with the given parameters using Database
     * information listed in persistence.xml. (object is mapped to database.)
     *
     * @param Title Book's title
     * @param Author Book's author
     * @param iYear Book's editing year
     * @return Creation error message. Void if creation is properly handled.
     */
    @Override
    //@RolesAllowed("users") // only members of users role can access
    public String createBook(String Title, String Author, int iYear) {
        String ret = "Error while creating book named " + Title + " written by  "
                + Author + " in " + String.valueOf(iYear) + ": improper parameters";
        if ((Title != null) && (Author != null)) {
            if ((Title.length() > 0) && (Author.length() > 0) && (iYear > 0)) {
                try {
                    ut.begin();
                    BookEntity book = new BookEntity(Title, Author, iYear);
                    bem.persist(book);
                    ut.commit();
                    ret = "";
                } catch (Exception e) {
                    ret = "Error while creating book named " + Title + " written by " + Author + " in "
                            + String.valueOf(iYear) + ": " + e.getMessage() + "<br>";
                }
            }
        }

        return ret;
    }

    /**
     * The object in parameter is removed from database.
     *
     * @param book - book to remove
     */
    @Override
    //@RolesAllowed("users") // only members of users role can access
    public void removeFromdb(BookEntity book) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Clear database from all existing books.
     *
     * @return database operation error.
     */
    @Override
    //@RolesAllowed("admin") // only members of users role can access
    public String removeAllFromDB() {
        String msg = "";
        try {
            ut.begin();
            Query q1 = bem.createNamedQuery("order.deleteall");
            q1.executeUpdate();
            Query q2 = bem.createNamedQuery("books.deleteall");
            q2.executeUpdate();
            ut.commit();
        } catch (Exception e) {
            msg = e.getMessage();
        }

        return msg;
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
    public Collection<BookEntity> getBooksByAuthors(String Author) {
        Query q = bem.createNamedQuery("books.findByAuthor");
        q.setParameter("author", "%" + Author + "%");
        Collection<BookEntity> books = q.getResultList();
        return books;
    }

    @Override
    public Collection<BookEntity> getBooksByTitle(String Title) {
        Query q = bem.createNamedQuery("books.findByTitle");
        q.setParameter("title", "%" + Title + "%");
        Collection<BookEntity> books = q.getResultList();
        return books;
    }

    @Override
    public Collection<BookEntity> getBooksByYear(int iYear) {
        Query q = bem.createNamedQuery("books.findByYear");
        q.setParameter("year", iYear);
        Collection<BookEntity> books = q.getResultList();
        return books;
    }

}
