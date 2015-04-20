/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cart;

import book.BookEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

/**
 *
 * @author user
 */
@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
public class CartSessionBeanImpl implements CartSessionBeanItfLocal {

    @PersistenceContext(unitName = "TP4dPU")
    private EntityManager bem;

    @Resource
    private UserTransaction ut;

    @Override
    @Remove
    public String add2Order(Object clientId, CartEntity myCart) {
        String ret = "";
        Long lClientId = null;
        FinalizedCartEntity tempFinalizedCartEntity;

        if (myCart.getContents().size() < 1) {
            return "<H2>Your basket is empty, add items before proceeding to checkout!</H2>";
        }

        try {
            lClientId = Long.parseLong(clientId.toString());
        } catch (Exception e) {
            clientId = null;
        }

        //Client not existing or Error while parsing = New Client
        if (clientId == null) {
            lClientId = this.getNextCLientId();
            // a real new client should be created here.
        }

        try {
            ut.begin();
            FinalizedCartEntity basket = new FinalizedCartEntity();
            BookEntity tempEntity =null;
            List<BookEntity> BookSet = new ArrayList<>();

            for(String titleId : myCart.getContents()){
            tempEntity = bem.find(BookEntity.class, titleId);
            BookSet.add(tempEntity);
            }
            basket.setBooks(BookSet);
            basket.setClientId(lClientId);
            bem.persist(basket);
            ut.commit();
            ret = "";
        } catch (Exception e) {
            ret = "Error while creating Order for client " + clientId
                    + ": " + e.getMessage() + "// " + e.getCause() + "<br>";
        }

        return ret;
    }

    @Override
    public FinalizedCartEntity getLastOrder() {
        FinalizedCartEntity LastOrder = null;
        Query q = bem.createNamedQuery("order.lastorder").setMaxResults(1);
        try {
            LastOrder = (FinalizedCartEntity) q.getSingleResult();
        } catch (NoResultException nre) {
            LastOrder = null;
        }
        return LastOrder;
    }

    @Override
    public Long getNextCLientId() {
        FinalizedCartEntity LastOrder = null;
        Query q = bem.createNamedQuery("order.lastuser").setMaxResults(1);
        try {
            LastOrder = (FinalizedCartEntity) q.getSingleResult();
        } catch (NoResultException nre) {
            LastOrder = null;
        }

        return (LastOrder != null) ? LastOrder.getClientId() + 1 : 1;
    }

}
