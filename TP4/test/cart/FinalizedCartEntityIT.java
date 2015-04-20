/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cart;

import book.BookEntity;
import java.util.List;
import java.util.Set;
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
public class FinalizedCartEntityIT {
    
    public FinalizedCartEntityIT() {
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
     * Test of getBooks method, of class FinalizedCartEntity.
     */
    @Test
    public void testGetBooks() {
        System.out.println("getBooks");
        FinalizedCartEntity instance = new FinalizedCartEntity();
        Set<BookEntity> expResult = null;
        List<BookEntity> result = instance.getBooks();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addBook method, of class FinalizedCartEntity.
     */
    @Test
    public void testAddBook() throws Exception {
        System.out.println("addBook");
        BookEntity be = null;
        FinalizedCartEntity instance = new FinalizedCartEntity();
        instance.addBook(be);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeBook method, of class FinalizedCartEntity.
     */
    @Test
    public void testRemoveBook() throws Exception {
        System.out.println("removeBook");
        BookEntity be = null;
        FinalizedCartEntity instance = new FinalizedCartEntity();
        instance.removeBook(be);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBooks method, of class FinalizedCartEntity.
     */
    @Test
    public void testSetBooks() {
        System.out.println("setBooks");
        List<BookEntity> books = null;
        FinalizedCartEntity instance = new FinalizedCartEntity();
        instance.setBooks(books);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getClientId method, of class FinalizedCartEntity.
     */
    @Test
    public void testGetClientId() {
        System.out.println("getClientId");
        FinalizedCartEntity instance = new FinalizedCartEntity();
        Long expResult = null;
        Long result = instance.getClientId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setClientId method, of class FinalizedCartEntity.
     */
    @Test
    public void testSetClientId() {
        System.out.println("setClientId");
        Long clientId = null;
        FinalizedCartEntity instance = new FinalizedCartEntity();
        instance.setClientId(clientId);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOrderId method, of class FinalizedCartEntity.
     */
    @Test
    public void testGetOrderId() {
        System.out.println("getOrderId");
        FinalizedCartEntity instance = new FinalizedCartEntity();
        Long expResult = null;
        Long result = instance.getOrderId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setOrderId method, of class FinalizedCartEntity.
     */
    @Test
    public void testSetOrderId() {
        System.out.println("setOrderId");
        Long orderId = null;
        FinalizedCartEntity instance = new FinalizedCartEntity();
        instance.setOrderId(orderId);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class FinalizedCartEntity.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        FinalizedCartEntity instance = new FinalizedCartEntity();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class FinalizedCartEntity.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object object = null;
        FinalizedCartEntity instance = new FinalizedCartEntity();
        boolean expResult = false;
        boolean result = instance.equals(object);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class FinalizedCartEntity.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        FinalizedCartEntity instance = new FinalizedCartEntity();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
