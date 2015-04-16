/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cart;

import book.BookEntity;
import java.util.Collection;
import javax.ejb.Local;

/**
 *
 * @author user
 */
@Local
public interface CartSessionBeanItfLocal {
    
    public void add2basket(BookEntity book);
    public void removefromBasket(BookEntity book);
    public Collection<BookEntity> getBasketContent();

    
}
