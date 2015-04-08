/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

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
@NamedQuery(name = "allbooks", query = "select object(b) from Book b")
public class BookEntityImpl implements BookEntityItf  {
    private static final long serialVersionUID = 1L;
    @Id
    private String bookTitle;
    private String bookAuhtor;
    private String bookYear;

    public BookEntityImpl() {
    }

    
    
    public BookEntityImpl(String bookTitle, String bookAuhtor, String bookYear) {
        this.bookTitle = bookTitle;
        this.bookAuhtor = bookAuhtor;
        this.bookYear = bookYear;
    }

    @Override
    public String getBookTitle() {
        return bookTitle;
    }

    @Override
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    @Override
    public String getBookAuhtor() {
        return bookAuhtor;
    }

    @Override
    public void setBookAuhtor(String bookAuhtor) {
        this.bookAuhtor = bookAuhtor;
    }

    @Override
    public String getBookYear() {
        return bookYear;
    }

    @Override
    public void setBookYear(String bookYear) {
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
        if (!(object instanceof BookEntityImpl)) {
            return false;
        }
        BookEntityImpl other = (BookEntityImpl) object;
        if ((this.bookTitle == null && other.bookTitle != null) || (this.bookTitle != null && !this.bookTitle.equals(other.bookTitle))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "book.BookEntity[ id=" + bookTitle + " ]";
    }
    
}
