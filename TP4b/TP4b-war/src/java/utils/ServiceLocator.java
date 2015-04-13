/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author user
 */
@Stateful
@LocalBean
public class ServiceLocator {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public static book.BookEntityImpl lookupYourSessionBean() {
        try {
            Context c = new InitialContext();
            return (book.BookEntityImpl) c.lookup("");
        } catch (NamingException ne) {
// Do something proper here -- i.e. not this.
            ne.printStackTrace();
        }
        return null;
    }
}
