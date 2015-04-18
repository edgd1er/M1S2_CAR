/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cart;

import java.util.ArrayList;
import java.util.Collection;
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
public class CartEntityIT {
    
    public CartEntityIT() {
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
     * Test of getId method, of class CartEntity.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        CartEntity instance = new CartEntity();
        Long expResult = null;
        Long result = instance.getId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setId method, of class CartEntity.
     */
    @Test
    public void testSetId() {
        System.out.println("setId");
        Long id = Long.parseLong("2");
        CartEntity instance = new CartEntity();
        instance.setId(id);
        if(instance.getId()!=id){fail("Error SetId did not returned the proper value");}
    }

    /**
     * Test of hashCode method, of class CartEntity.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        CartEntity instance = new CartEntity();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class CartEntity.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object object = null;
        CartEntity instance = new CartEntity();
        boolean expResult = false;
        boolean result = instance.equals(object);
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class CartEntity.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        CartEntity instance = new CartEntity();
        String expResult ="cart.CartEntity[ id=null ]";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of add method, of class CartEntity.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        String book2buy = "Livre1";
        CartEntity instance = new CartEntity();
        boolean expResult = true;
        boolean result = instance.add(book2buy);
        assertEquals(expResult, result);
    }

    /**
     * Test of remove method, of class CartEntity.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        String book2buy = "";
        CartEntity instance = new CartEntity();
        boolean expResult = false;
        instance.add("Livre1");
        boolean result = instance.remove(book2buy);
        assertEquals(expResult, result);
    }

    /**
     * Test of getContents method, of class CartEntity.
     */
    @Test
    public void testGetContents() {
        System.out.println("getContents");
        CartEntity instance = new CartEntity();
        Collection<String> expResult = new ArrayList<String>();
        Collection<String> result = instance.getContents();
        assertEquals(expResult, result);

    }
    
}
