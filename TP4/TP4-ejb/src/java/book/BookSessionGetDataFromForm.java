/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;


import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author user
 */
@Stateful
@Remote
public class BookSessionGetDataFromForm implements BookSessionItf {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    String title;
    String year;
    String author;

    @EJB
    private BookEntityItf myBook;

    @PersistenceContext(name = "TP4-ejbPU")
    private EntityManager bem;
    
    /**
     * Constructor with parameter
     * @param title
     * @param year
     * @param author 
     */
    public BookSessionGetDataFromForm(String title, String year, String author) {
        
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
    public BookSessionGetDataFromForm() {
        this.title = "Book's title";
        this.year = "Enter edition's year";
        this.author = "author's name";
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getYear() {
        return year;
    }

    @Override
    public void setYear(String year) {
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
    
    
    @Override
    public List<BookEntityImpl> getBooks(){
        List<BookEntityImpl> listBook;
        Query q= bem.createNamedQuery("allbooks");
        listBook = (List<BookEntityImpl>) q.getResultList();
        
        return listBook;
        
    }
    
}
