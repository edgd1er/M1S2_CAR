/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entity Bean to Handle a basket being paid.
 * 
 * 
 * @author Francois Dubiez
 */
@Entity
public class CartEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Collection<String> booksId;

    /**
     *Default Constructor
     */
    public CartEntity() {
        booksId = new ArrayList();
    }

    /**
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CartEntity)) {
            return false;
        }
        CartEntity other = (CartEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cart.CartEntity[ id=" + id + " ]";
    }

    /**
     *Add a Book to buy in the basket
     * 
     * @param book2buy
     * @return true or false
     */
    public boolean add(String book2buy) {
        if (book2buy == null) {
            return false;
        }
        if (book2buy.isEmpty()) {
            return false;
        }
        booksId.add(book2buy);
        return true;
    }

    /**
     * Remove a book from Basket
     * @param book2buy
     * @return true or false
     */
    public boolean remove(String book2buy) {
        if (book2buy == null) {
            return false;
        }
        
        if (!booksId.contains(book2buy)){return false;}
        
        if (book2buy.isEmpty()) {
            return false;
        }
        booksId.remove(book2buy);
        return true;

    }

    /**
     * Get All books in the basket
     * @return
     */
    public Collection<String> getContents() {
        return booksId;
    }

}
