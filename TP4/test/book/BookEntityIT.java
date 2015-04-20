/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

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
public class BookEntityIT {

    String title = "Titre1", author = "Author1";
    int iyear = 2001;

    public BookEntityIT() {

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
     * Test of getBookTitle method, of class BookEntity.
     */
    @Test
    public void testGetBookTitle() {
        System.out.println("getBookTitle");
        BookEntity instance = new BookEntity(title, author, iyear);
        String expResult = title;
        String result = instance.getBookTitle();
        assertEquals(expResult, result);
    }

    /**
     * Test of setBookTitle method, of class BookEntity.
     */
    @Test
    public void testSetBookTitle() {
        System.out.println("setBookTitle");
        String bookTitle = title;
        BookEntity instance = new BookEntity();
        instance.setBookTitle(bookTitle);
        String result = instance.getBookTitle();
        assertEquals(bookTitle, result);
    }

    /**
     * Test of getBookAuthor method, of class BookEntity.
     */
    @Test
    public void testGetBookAuhtor() {
        System.out.println("getBookAuhtor");
        BookEntity instance = new BookEntity(title, author, iyear);
        String expResult = author;
        String result = instance.getBookAuthor();
        assertEquals(expResult, result);

    }

    /**
     * Test of setBookAuhtor method, of class BookEntity.
     */
    @Test
    public void testSetBookAuhtor() {
        System.out.println("setBookAuhtor");
        String bookAuhtor = author;
        BookEntity instance = new BookEntity();
        instance.setBookAuhtor(bookAuhtor);
        String result = instance.getBookAuthor();
        assertEquals(bookAuhtor, result);
    }

    /**
     * Test of getBookYear method, of class BookEntity.
     */
    @Test
    public void testGetBookYear() {
        System.out.println("getBookYear");
        BookEntity instance = new BookEntity(title, author, iyear);
        int expResult = iyear;
        int result = instance.getBookYear();
        assertEquals(expResult, result);
    }

    /**
     * Test of setBookYear method, of class BookEntity.
     */
    @Test
    public void testSetBookYear() {
        System.out.println("setBookYear");
        int bookYear = iyear;
        BookEntity instance = new BookEntity();
        instance.setBookYear(bookYear);
        int result = instance.getBookYear();
        assertEquals(iyear, result);
    }

    /**
     * Test of hashCode method, of class BookEntity.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        BookEntity instance = new BookEntity(title, author, iyear);
        int expResult = title.hashCode();
        int result = instance.hashCode();
        assertEquals(expResult, result);
       
    }

    /**
     * Test of equals method, of class BookEntity.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object object = null;
        BookEntity instance = new BookEntity();
        boolean expResult = false;
        boolean result = instance.equals(object);
        assertEquals(expResult, result);
   
    }

    /**
     * Test of toString method, of class BookEntity.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        BookEntity instance = new BookEntity(title, author, iyear);
        String expResult = "book.BookEntity[ id=" + title + " ]";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

}
