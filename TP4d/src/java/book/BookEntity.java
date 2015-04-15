/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
    @NamedQuery(name = "books.findByTitle", query = "SELECT b FROM BookEntity b WHERE b.bookTitle LIKE :title"),
    @NamedQuery(name = "books.AllAuthors", query = "SELECT distinct b.bookAuhtor FROM BookEntity b"),
    @NamedQuery(name = "books.findByAuthor", query = "SELECT b FROM BookEntity b WHERE b.bookAuhtor = :author")
}
)
public class BookEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "title", nullable = false)
    private String bookTitle;
    @Column(name = "author", nullable = false)
    private String bookAuhtor;
    @Column(name = "book_year", nullable = false)
    private int bookYear;

    /**
     * Empry constructor (default)
     */
    public BookEntity() {
    }

    /**
     * Constructor with parameters. Used by JSP.Servlet to create immediately
     * the Book object.
     *
     * @param bookTitle
     * @param bookAuhtor
     * @param bookYear
     */
    public BookEntity(String bookTitle, String bookAuhtor, int bookYear) {
        this.bookTitle = bookTitle;
        this.bookAuhtor = bookAuhtor;
        this.bookYear = bookYear;
    }

    /**
     * Getter for book's title
     *
     * @return book's Title
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * Book's Title Setter
     *
     * @param bookTitle
     */
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    /**
     * Book's Autohor getter
     *
     * @return
     */
    public String getBookAuhtor() {
        return bookAuhtor;
    }

    /**
     * Book's author setter
     *
     * @param bookAuhtor
     */
    public void setBookAuhtor(String bookAuhtor) {
        this.bookAuhtor = bookAuhtor;
    }

    /**
     * Book's year getter
     *
     * @return
     */
    public int getBookYear() {
        return bookYear;
    }

    /**
     * Book's year setter
     *
     * @param bookYear
     */
    public void setBookYear(int bookYear) {
        this.bookYear = bookYear;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookTitle != null ? bookTitle.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BookEntity)) {
            return false;
        }
        BookEntity other = (BookEntity) object;
        return !((this.bookTitle == null && other.bookTitle != null) || (this.bookTitle != null && !this.bookTitle.equals(other.bookTitle)));
    }

    @Override
    public String toString() {
        return "book.BookEntity[ id=" + bookTitle + " ]";
    }
}
