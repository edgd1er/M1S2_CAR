/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

import java.util.Collection;
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
public class BookSessionBeanIT {
    
      String Title = "Titre1", Author = "Author1";
    int iYear = 2001;
    
    public BookSessionBeanIT() {
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
     * Test of createBooks method, of class BookSessionBean.
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateBooks() throws Exception {
        System.out.println("createBooks");
          try (EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer()) {
              BookSessionBeanItfLocal instance = (BookSessionBeanItfLocal)container.getContext().lookup("java:global/classes/BookSessionBean");
              String expResult = "";
              String result = instance.createBooks();
              assertEquals(expResult, result);
          }

    }

    /**
     * Test of createBook method, of class BookSessionBean.
     */
    @Test
    public void testCreateBook() throws Exception {
        System.out.println("createBook");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        BookSessionBeanItfLocal instance = (BookSessionBeanItfLocal)container.getContext().lookup("java:global/classes/BookSessionBean");
        String expResult = "";
        String result = instance.createBook(Title, Author, iYear);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeFromdb method, of class BookSessionBean.
     */
    @Test
    public void testRemoveFromdb() throws Exception {
        System.out.println("removeFromdb");
        
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        BookSessionBeanItfLocal instance = (BookSessionBeanItfLocal)container.getContext().lookup("java:global/classes/BookSessionBean");
        BookEntity book = null;
        instance.removeFromdb(book);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeAllFromDB method, of class BookSessionBean.
     */
    @Test
    public void testRemoveAllFromDB() throws Exception {
        System.out.println("removeAllFromDB");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        BookSessionBeanItfLocal instance = (BookSessionBeanItfLocal)container.getContext().lookup("java:global/classes/BookSessionBean");
        String expResult = "";
        String result = instance.removeAllFromDB();
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }


    /**
     * Test of getBooks method, of class BookSessionBean.
     */
    @Test
    public void testGetBooks() throws Exception {
        System.out.println("getBooks");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        BookSessionBeanItfLocal instance = (BookSessionBeanItfLocal)container.getContext().lookup("java:global/classes/BookSessionBean");
        Collection<BookEntity> expResult = null;
        Collection<BookEntity> result = instance.getBooks();
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAuthors method, of class BookSessionBean.
     */
    @Test
    public void testGetAuthors() throws Exception {
        System.out.println("getAuthors");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        BookSessionBeanItfLocal instance = (BookSessionBeanItfLocal)container.getContext().lookup("java:global/classes/BookSessionBean");
        Collection<String> expResult = null;
        Collection<String> result = instance.getAuthors();
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

 
    
}
