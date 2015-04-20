/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toolsServlet;

import book.BookSessionBean;
import book.BookSessionBeanItfLocal;
import cart.CartEntity;
import cart.CartSessionBeanImpl;
import cart.FinalizedCartEntity;
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
public class servletToolsIT {
    
    public servletToolsIT() {
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
     * Test of getinstance method, of class servletTools.
     */
    @Test
    public void testGetinstance() {
        System.out.println("getinstance");
        servletTools expResult = null;
        servletTools result = servletTools.getinstance();
        assertNotNull(result);
       
    }

    /**
     * Test of htmlAdd2Database method, of class servletTools.
     */
    @Test
    public void testHtmlAdd2Database() {
        System.out.println("htmlAdd2Database");
        String title = "Livre1";
        String author = "Author1";
        String strYear = "2001";
        BookSessionBeanItfLocal myBookBean = null;
        servletTools instance = null;
        String expResult = "";
        String result = instance.htmlAdd2Database(title, author, strYear, myBookBean);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHtmlBooksToBuy method, of class servletTools.
     */
    @Test
    public void testGetHtmlBooksToBuy() {
        System.out.println("getHtmlBooksToBuy");
        String servletUrlToAddToCart = "";
        BookSessionBeanItfLocal myBookBean = null;
        servletTools instance = null;
        String expResult = "";
        String result = instance.getHtmlBooksToBuy(servletUrlToAddToCart, myBookBean);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHtmlBooksToDisplay method, of class servletTools.
     */
    @Test
    public void testGetHtmlBooksToDisplay() {
        System.out.println("getHtmlBooksToDisplay");
        BookSessionBeanItfLocal myBookBean = null;
        servletTools instance = servletTools.getinstance();
        String expResult = "";
        String result = instance.getHtmlBooksToDisplay(myBookBean);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHtmlToDisplayAuthors method, of class servletTools.
     */
    @Test
    public void testGetHtmlToDisplayAuthors() {
        System.out.println("getHtmlToDisplayAuthors");
        BookSessionBeanItfLocal myBookBean = null;
        servletTools instance = null;
        String expResult = "";
        String result = instance.getHtmlToDisplayAuthors(myBookBean);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHtmlCartContents method, of class servletTools.
     */
    @Test
    public void testGetHtmlCartContents() {
        System.out.println("getHtmlCartContents");
        String servletUrlToAddToCart = "myurl";
        CartEntity myCart = new CartEntity();
        myCart.add("Livre1");
        servletTools instance = servletTools.getinstance();
        String expResult = "Livre1";
        String result = instance.getHtmlCartContents(servletUrlToAddToCart, myCart);
        assertTrue(result, result.contains("Livre1"));
        assertTrue(result, result.contains("myurl"));
    }

    /**
     * Test of getHtmlLastOrder method, of class servletTools.
     */
    @Test
    public void testGetHtmlLastOrder() {
        System.out.println("getHtmlLastOrder");
        CartSessionBeanImpl myCartSession = null;
        servletTools instance = null;
        String expResult = "";
        String result = instance.getHtmlLastOrder(myCartSession);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHtmlBooksBought method, of class servletTools.
     */
    @Test
    public void testGetHtmlBooksBought() {
        System.out.println("getHtmlBooksBought");
        FinalizedCartEntity myCart = null;
        servletTools instance = null;
        String expResult = "";
        String result = instance.getHtmlBooksBought(myCart);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of htmlGetSearchForms method, of class servletTools.
     */
    @Test
    public void testHtmlGetSearchForms() {
        System.out.println("htmlGetSearchForms");
        String urlSearch = "";
        String title = "";
        String author = "";
        String year = "";
        servletTools instance = null;
        String expResult = "";
        String result = instance.htmlGetSearchForms(urlSearch, title, author, year);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of htmlGetResultsForAuhtor method, of class servletTools.
     */
    @Test
    public void testHtmlGetResultsForAuhtor() {
        System.out.println("htmlGetResultsForAuhtor");
        BookSessionBeanItfLocal myBookBean = null;
        String author = "";
        servletTools instance = null;
        String expResult = "";
        String result = instance.htmlGetResultsForAuhtor(myBookBean, author);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of htmlGetResultsForTitle method, of class servletTools.
     */
    @Test
    public void testHtmlGetResultsForTitle() {
        System.out.println("htmlGetResultsForTitle");
        BookSessionBeanItfLocal myBookBean = null;
        String title = "";
        servletTools instance = null;
        String expResult = "";
        String result = instance.htmlGetResultsForTitle(myBookBean, title);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of htmlGetResultsForYear method, of class servletTools.
     */
    @Test
    public void testHtmlGetResultsForYear() {
        System.out.println("htmlGetResultsForYear");
        BookSessionBeanItfLocal myBookBean = null;
        String year = "";
        servletTools instance = null;
        String expResult = "";
        String result = instance.htmlGetResultsForYear(myBookBean, year);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of htmlGetResultsForSearch method, of class servletTools.
     */
    @Test
    public void testHtmlGetResultsForSearch() {
        System.out.println("htmlGetResultsForSearch");
        BookSessionBeanItfLocal myBookBean = null;
        String title = "";
        String author = "";
        String year = "";
        String crit = "";
        servletTools instance = null;
        String expResult = "";
        String result = instance.htmlGetResultsForSearch(myBookBean, title, author, year, crit);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMenuLinks method, of class servletTools.
     */
    @Test
    public void testGetMenuLinks() {
        System.out.println("getMenuLinks");
        servletTools instance = null;
        String expResult = "";
        String result = instance.getMenuLinks();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of persist method, of class servletTools.
     */
    @Test
    public void testPersist() {
        System.out.println("persist");
        Object object = null;
        servletTools instance = null;
        instance.persist(object);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
