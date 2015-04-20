/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cart;

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
public class CartSessionBeanItfLocalIT {
    
    public CartSessionBeanItfLocalIT() {
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
     * Test of add2Order method, of class CartSessionBeanItfLocal.
     */
    @Test
    public void testAdd2Order() {
        System.out.println("add2Order");
        Object clientId = null;
        CartEntity myCart = null;
        CartSessionBeanItfLocal instance = new CartSessionBeanItfLocalImpl();
        String expResult = "";
        String result = instance.add2Order(clientId, myCart);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastOrder method, of class CartSessionBeanItfLocal.
     */
    @Test
    public void testGetLastOrder() {
        System.out.println("getLastOrder");
        CartSessionBeanItfLocal instance = new CartSessionBeanItfLocalImpl();
        FinalizedCartEntity expResult = null;
        FinalizedCartEntity result = instance.getLastOrder();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNextCLientId method, of class CartSessionBeanItfLocal.
     */
    @Test
    public void testGetNextCLientId() {
        System.out.println("getNextCLientId");
        CartSessionBeanItfLocal instance = new CartSessionBeanItfLocalImpl();
        Long expResult = null;
        Long result = instance.getNextCLientId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class CartSessionBeanItfLocalImpl implements CartSessionBeanItfLocal {

        public String add2Order(Object clientId, CartEntity myCart) {
            return "";
        }

        public FinalizedCartEntity getLastOrder() {
            return null;
        }

        public Long getNextCLientId() {
            return null;
        }
    }
    
}
