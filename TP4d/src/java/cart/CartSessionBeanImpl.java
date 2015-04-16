/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cart;

import book.BookEntity;
import java.util.Collection;
import javax.ejb.Stateful;

/**
 *
 * @author user
 */
@Stateful
public class CartSessionBeanImpl implements CartSessionBeanItfLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
        @Override
    public void add2basket(BookEntity book) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removefromBasket(BookEntity book) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
       @Override
    public Collection<BookEntity> getBasketContent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
