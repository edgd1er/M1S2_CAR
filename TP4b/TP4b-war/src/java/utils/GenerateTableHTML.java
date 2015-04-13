/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author user
 */
public class GenerateTableHTML {

    String strTbl;
    private static GenerateTableHTML instance;

    private GenerateTableHTML() {
    }

    public static GenerateTableHTML getInstance() {
        if (instance == null) {
            synchronized (GenerateTableHTML.class) {
                instance = new GenerateTableHTML();
            }
        }
        return instance;
    }

    public static String getHeader() {

        String strTbl = "<TABLE BORDER=\"1\">\n";
        strTbl = "<CAPTION> Book's in database </CAPTION> \n";
        strTbl += "<TR> \n";
        strTbl += "<TH> Author </TH> \n";
        strTbl += "<TH> Title </TH> \n";
        strTbl += "<TH> Year </TH> \n";
        strTbl += " </TR> \n";

        return strTbl;
    }

    String getFooter() {
        strTbl = " < /TABLE >";
        return strTbl;
    }
}
