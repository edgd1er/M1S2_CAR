/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

import java.util.Collection;
import javax.ejb.Local;

/**
 *
 * @author user
 */
@Local
public interface BookSessionBeanLocal {
    
    public String createBooks();
    public String createBook(String Title, String Author, int iYear);
    public String add2db(BookEntity book);
    public void removeFromdb(BookEntity book);
    public String removeAllFromDB();
    public void add2basket(BookEntity book);
    public void removefromBasket(BookEntity book);
    public Collection<BookEntity> getBooks();
    public Collection<BookEntity> getBasketContent();
    public Collection<String> getAuthors();
}
