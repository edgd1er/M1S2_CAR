/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cart;

import javax.ejb.embeddable.EJBContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author user
 */
public class CartSessionBeanImplIT {
    
    public CartSessionBeanImplIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of add2Order method, of class CartSessionBeanImpl.
     */
    @Test
    public void testAdd2Order() throws Exception {
        System.out.println("add2Order");
        Object clientId = null;
        CartEntity myCart = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        CartSessionBeanItfLocal instance = (CartSessionBeanItfLocal)container.getContext().lookup("java:global/classes/CartSessionBeanImpl");
        String expResult = "";
        String result = instance.add2Order(clientId, myCart);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastOrder method, of class CartSessionBeanImpl.
     */
    @Test
    public void testGetLastOrder() throws Exception {
        System.out.println("getLastOrder");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        CartSessionBeanItfLocal instance = (CartSessionBeanItfLocal)container.getContext().lookup("java:global/classes/CartSessionBeanImpl");
        FinalizedCartEntity expResult = null;
        FinalizedCartEntity result = instance.getLastOrder();
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNextCLientId method, of class CartSessionBeanImpl.
     */
    @Test
    public void testGetNextCLientId() throws Exception {
        System.out.println("getNextCLientId");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        CartSessionBeanItfLocal instance = (CartSessionBeanItfLocal)container.getContext().lookup("java:global/classes/CartSessionBeanImpl");
        Long expResult = null;
        Long result = instance.getNextCLientId();
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
