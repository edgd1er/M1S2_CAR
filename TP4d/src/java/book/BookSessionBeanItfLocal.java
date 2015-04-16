/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

import java.util.Collection;
import javax.ejb.Local;

/**
 * Interface to propose service to handle a bookEntity
 * 
 * @author François Dubiez
 */
@Local
public interface BookSessionBeanItfLocal {
    
     /**
     * Populate database with 5 books
     * 
     * @return creation error message
     */
    public String createBooks();
     /**
     * create an object book with the given parameters using Database
     * information listed in persistence.xml. (object is mapped to database.)
     * 
     * @param Title     Book's title
     * @param Author     Book's author
     * @param iYear     Book's editing year
     * @return          Creation error message. Void if creation is properly handled.
     */
    public String createBook(String Title, String Author, int iYear);
    
    
    /**
 * The object in parameter is removed from database.
 * 
 * @param book - book to remove
 */
    public void removeFromdb(BookEntity book);
    /**
     * Clear database from all existing books.
     * 
     * @return database operation error.
     */
    public String removeAllFromDB();
    
    /**
     * Retrieve books from database
     * 
     * @return Collection of Books
     */
    public Collection<BookEntity> getBooks();
    
    /**
     * Retrieve list of distinct authors
     * 
     * @return Collection of String containing the authors.
     */
    public Collection<String> getAuthors();
}
