/**
 *    Ασφάλεια Δικτύων Υπολογιστών & Τεχνολογίες Προστασίας της Ιδιωτικότητας
 *                                   Άσκηση 1
 * Δημιουργία ασφαλούς διαύλου διαχείρισης botnet (C&C) με χρήση IPsec & TLS/SSL πρωτοκόλλων.
 * 
 *                      Πέππας Κωνσταντίνος 321/2011134
 *                      Σωτηρέλης Χρήστος   321/2012182
 *                      Χαϊκάλης Νικόλαος   321/2012200
 * 
 */

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import javax.net.ssl.SSLSocket;

public class Bot_Properties implements Serializable{
    private String BotIP, BotPCname; //Ip tou bot k to PC name tou Bot antistoixa.
    private SSLSocket socket;
    private ObjectOutputStream oos;

    public Bot_Properties(String BotIP, String BotPCname) {
        this.BotIP = BotIP;
        this.BotPCname = BotPCname;
    }
    
    public Bot_Properties(){}

    
    //GETS:
    public String getBotIP() {
        return BotIP;
    }

    public String getBotPCname() {
        return BotPCname;
    }

    public Socket getSSLSocket() {
        return socket;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }
    
    
    
    //SETS:
    public void setBotPCname(String BotPCname) {
        this.BotPCname = BotPCname;
    }

    public void setBotIP(String BotIP) {
        this.BotIP = BotIP;
    }

    public void setSSLSocket(SSLSocket socket) {
        this.socket = socket;
    }

    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }
    
    
    
}//End class
