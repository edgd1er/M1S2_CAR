/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author user
 */
@Entity
@Table(name="book")
@NamedQuery(name = "allbooks", query = "select object(b) from BookEntityImpl b")
public class BookEntityImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String bookTitle;
    private String bookAuhtor;
    private int bookYear;

    public BookEntityImpl() {
    }

    public BookEntityImpl(String bookTitle, String bookAuhtor, int bookYear) {
        this.bookTitle = bookTitle;
        this.bookAuhtor = bookAuhtor;
        this.bookYear = bookYear;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuhtor() {
        return bookAuhtor;
    }

    public void setBookAuhtor(String bookAuhtor) {
        this.bookAuhtor = bookAuhtor;
    }

    public int getBookYear() {
        return bookYear;
    }

    public void setBookYear(int bookYear) {
        this.bookYear = bookYear;
    }

    public int hashCode() {
        int hash = 0;
        hash += (bookTitle != null ? bookTitle.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BookEntityImpl)) {
            return false;
        }
        BookEntityImpl other = (BookEntityImpl) object;
        if ((this.bookTitle == null && other.bookTitle != null) || (this.bookTitle != null && !this.bookTitle.equals(other.bookTitle))) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "book.BookEntity[ id=" + bookTitle + " ]";
    }
    
}
