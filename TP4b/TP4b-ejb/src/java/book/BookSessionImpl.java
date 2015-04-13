/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author user
 */
@Stateful
public class BookSessionImpl implements BookSessionItf, Serializable {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    String title;
    int year;
    String author;

    @EJB
    private BookEntityImpl myBook;
    
    //@PersistenceContext  protected EntityManager bem;
    @PersistenceContext(unitName = "TP4b-ejbPU")
    protected EntityManager bem;

    /**
     * Constructor with parameter
     *
     * @param title
     * @param year
     * @param author
     */
    public BookSessionImpl(String title, int year, String author) {

        //creator a Book in the session
        this.title = title;
        this.year = year;
        this.author = author;

        //create the book in the database.
        myBook = new BookEntityImpl(title, author, year);
        bem.persist(myBook);

    }

    /**
     * Empty constructor
     */
    public BookSessionImpl() {
        this.title = "Book's title";
        this.year = 2015;
        this.author = "author's name";
        myBook = new BookEntityImpl(title, author, year);
        bem.persist(myBook);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    public List<BookEntityImpl> getBooks() {
        List<BookEntityImpl> listBook=null;
        try {
            Query q = bem.createQuery("select OBJECT(b) from BookEntityImpl b");
            System.out.println(q);
            listBook = (List<BookEntityImpl>) q.getResultList();

        } catch (NullPointerException ne) {
        }
        return listBook;

    }

    @Override
    public void initBooks() {

        //myBookSessionBeanInit
        final BookEntityImpl book1 = new BookEntityImpl("Soumission", "Michel Houellebecq", 2015);
        final BookEntityImpl book2 = new BookEntityImpl("Mr Mercedes", "Stephen King", 2015);
        final BookEntityImpl book3 = new BookEntityImpl("Central Park", "Guillaume Musso", 2015);
        final BookEntityImpl book4 = new BookEntityImpl("Muchachas Tome3", "Katherine Pancol", 2014);
        final BookEntityImpl book5 = new BookEntityImpl("Le Gardien De Phare", "Camilla Läckberg", 2013);
        bem.persist(book1);
        bem.persist(book2);
        bem.persist(book3);
        bem.persist(book4);
        bem.persist(book5);
    }

    public List<String> getAuthors() {

        List<String> Authors = new ArrayList<>();
        List<BookEntityImpl> Books = getBooks();
        for (BookEntityImpl tempBook : Books) {
            if (!Authors.contains(tempBook.getBookAuhtor())) {
                Authors.add(tempBook.getBookAuhtor());
            }
        }
        return Authors;
    }

    public void addDB(){
     if (this.myBook!=null)
     {bem.persist(myBook);
     }
    }
    
}
