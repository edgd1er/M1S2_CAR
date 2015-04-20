/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author user
 */
@Entity
@Table(name = "book")
@NamedQueries({
    @NamedQuery(name = "books.getallbooks", query = "select object(b) from BookEntity b"),
    @NamedQuery(name = "books.deleteall", query = "delete from BookEntity b"),
    @NamedQuery(name = "books.AllAuthors", query = "SELECT distinct b.bookAuthor FROM BookEntity b"),
    @NamedQuery(name = "books.findByAuthor", query = "SELECT b FROM BookEntity b WHERE lower(b.bookAuthor) LIKE :author"),
    @NamedQuery(name = "books.findByTitle", query = "SELECT b FROM BookEntity b WHERE lower(b.bookTitle) LIKE :title"),
    @NamedQuery(name = "books.findByYear", query = "SELECT b FROM BookEntity b WHERE b.bookYear = :year"),
    
}
)
public class BookEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "title", nullable = false)
    private String bookTitle;
    @Column(name = "author", nullable = false)
    private String bookAuthor;
    @Column(name = "book_year", nullable = false)
    private int bookYear;
    
    @ManyToMany
    protected List<BookEntity> books;

    /**
     * Empry constructor (default)
     */
    public BookEntity() {
    }

    /**
     * Constructor with parameters. Used by JSP.Servlet to create immediately
     * the Book object.
     *
     * @param bookTitle String to give a title to the book
     * @param bookAuthor String to name the author of the book
     * @param bookYear  integer to set the publication's year.
     */
    public BookEntity(String bookTitle, String bookAuthor, int bookYear) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookYear = bookYear;
    }

    /**
     * Getter for book's title
     *
     * @return String book's Title
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Book's Title Setter
     *
     * @param String bookTitle
     */
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    /**
     * Book's Author getter
     *
     * @return book's author
     */
    public String getBookAuthor() {
        return bookAuthor;
    }

    /**
     * Book's author setter
     *
     * @param bookAuthor
     */
    public void setBookAuhtor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    /**
     * Book's year getter
     *
     * @return bookYear book's edited year
     */
    public int getBookYear() {
        return bookYear;
    }

    /**
     * Book's year setter
     *
     * @param bookYear book's edited year
     */
    public void setBookYear(int bookYear) {
        this.bookYear = bookYear;
    }

    /**
     * Provide a hashcode for the book object.
     * 
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookTitle != null ? bookTitle.hashCode() : 0);
        return hash;
    }

    /**
     * Provide a way to compare to book object.
     * 
     * @param object book to compare
     * @return boolean true if are equals.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BookEntity)) {
            return false;
        }
        BookEntity other = (BookEntity) object;
        return !((this.bookTitle == null && other.bookTitle != null) || (this.bookTitle != null && !this.bookTitle.equals(other.bookTitle)));
    }

    /**
     * provide a way to get book's name. 
     * 
     * @return book's title
     */
    @Override
    public String toString() {
        return "book.BookEntity[ id=" + bookTitle + " ]";
    }
}
