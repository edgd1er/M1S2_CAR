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
 * Session Bean to handle an basket(add, remove) and to set/get order to/from
 * database
 *
 * @author François Dubiez
 */
@Local
public interface CartSessionBeanItfLocal {

    /**
     * create on order with the given books for the client clientId
     *
     * @param clientId 
     * @param myCart
     * @return Error creation status message.
     */
    public String add2Order(Object clientId, CartEntity myCart);

    /**
     * get the last order object.
     *
     * @return
     */
    public FinalizedCartEntity getLastOrder();

    /**
     * Get the Last ClientId to create the next one.
     *
     * @return id for the next client
     */
    public Long getNextCLientId();

}
