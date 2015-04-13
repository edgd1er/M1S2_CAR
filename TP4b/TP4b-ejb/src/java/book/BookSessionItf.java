/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package book;


/**
 *
 * @author user
 */
public interface BookSessionItf  {

    public String getAuthor();

    public String getTitle();

    public int getYear();

    public void setAuthor(String author);

    public void setTitle(String title);

    public void setYear(int year);

    public void initBooks();

    //public List<String> getAuthors();
    //List<BookEntityImpl> getBooks();
    
}
