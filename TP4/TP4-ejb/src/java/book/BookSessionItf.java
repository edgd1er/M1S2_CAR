/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;

import java.util.List;
import javax.ejb.Remote;

@Remote
/**
 *
 * @author user
 */
public interface BookSessionItf {

    String getAuthor();

    List<BookEntityImpl> getBooks();

    String getTitle();

    String getYear();

    void setAuthor(String author);

    void setTitle(String title);

    void setYear(String year);
    
}
