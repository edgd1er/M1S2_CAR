/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

import java.io.Serializable;
import javax.ejb.Remote;

@Remote
/**
 *
 * @author user
 */
public interface BookEntityItf extends Serializable {

    boolean equals(Object object);

    String getBookAuhtor();

    String getBookTitle();

    String getBookYear();

    int hashCode();

    void setBookAuhtor(String bookAuhtor);

    void setBookTitle(String bookTitle);

    void setBookYear(String bookYear);

    String toString();
    
}
