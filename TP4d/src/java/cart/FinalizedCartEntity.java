/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cart;

import book.BookEntity;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Entity Bean to handle operation on Order object and its representation in
 * database.
 *
 *
 * @author François Dubiez
 */
@Entity
@Table(name = "orderTable")
@NamedQueries({
    @NamedQuery(name = "order.deleteall", query
            = "delete from  FinalizedCartEntity f"),
    @NamedQuery(name = "order.lastorder", query
            = "select OBJECT(f) from  FinalizedCartEntity f ORDER BY f.orderId desc"),
    @NamedQuery(name = "order.lastuser", query
            = "select OBJECT(f) from  FinalizedCartEntity f ORDER BY f.clientId desc")
})
public class FinalizedCartEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ORDER_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long orderId;

    /**
     *client identification number
     */
    @Column(name = "CLIENT_ID")
    protected Long clientId;

    /**
     *Association table: order_items
     */
    @ManyToMany
    @JoinTable(name = "Order_Items", joinColumns
            = @JoinColumn(name = "OI_ORDER_ID", referencedColumnName = "ORDER_ID"),
            inverseJoinColumns
            = @JoinColumn(name = "OI_BOOK_ID", referencedColumnName = "title")
    )
    protected List<book.BookEntity> books;

    /**
     *
     * @return
     */
    public List<BookEntity> getBooks() {
        return books;
    }

    /**
     * Add a book to the order.
     *
     * @param be BookEntity
     * @throws Exception
     */
    public void addBook(BookEntity be) throws Exception {
        if (be == null) {
            throw new Exception();
        }
        this.books.add(be);
    }

    /**
     * Remove a Book from the order
     *
     * @param be BookEntity
     * @throws Exception
     */
    public void removeBook(BookEntity be) throws Exception {
        if (be == null) {
            Exception e = new Exception("Error, Parameter cannot be null.");
            throw e;
        }
        this.books.remove(be);
    }

    /**
     * add a complete set of books to the order.
     *
     * @param books Set of BookEntity
     */
    public void setBooks(List<BookEntity> books) {
        this.books = books;
    }

    /**
     * return the clientId
     *
     * @return
     */
    public Long getClientId() {
        return clientId;
    }

    /**
     * Set the clientId
     *
     * @param clientId
     */
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    /**
     * return OrderId
     *
     * @return
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * Set orderId
     *
     * @param orderId
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderId != null ? orderId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FinalizedCartEntity)) {
            return false;
        }
        FinalizedCartEntity other = (FinalizedCartEntity) object;
        if ((this.orderId == null && other.orderId != null) || (this.orderId != null && !this.orderId.equals(other.orderId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "book.BasketEntity[ id=" + orderId + " ]";
    }
}
