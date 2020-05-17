/**
 *                     Πανεπιστήμιο Αιγαίου
 *      Τμήμα Μηχανικών Πληροφοριακών & Επικοινωνιακών Συστημάτων
 * 
 *          Ασφάλεια Ασύρματων & Κινητών Δικτύων Επικοινωνιών
 *                     Εαρινό Εξάμηνο 2016
 *                      Project Μαθήματος
 * 
 * Δημιουργία secure botnet με android clients & Dos/DDoS σε DTLS Server.
 * 
 *          Πέππας Κωνσταντίνος 321/2011134 - icsd11134@icsd.aegean.gr
 *          Σωτηρέλης Χρήστος   321/2012182 - icsd12182@icsd.aegean.gr
 *          Χαϊκάλης Νικόλαος   321/2012200 - icsd12200@icsd.aegean.gr
 * 
 *          Καθηγητής Μαθήματος: Γ. Καμπουράκης 
 * 
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client_DTLS implements Serializable{

    public static void main(String[] args) {
        try {
            DatagramSocket sk =(DatagramSocket)new DatagramSocket();
            byte[] buf = new byte[5000];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            ChatWithDTLSServer client = new ChatWithDTLSServer(sk, dp);
            InetAddress hostAddress = InetAddress.getByName("localhost");
            buf = "hello".getBytes();
            dp  = new DatagramPacket(buf, buf.length, hostAddress, 4000);
            sk.send(dp);
            client.ClientServerHandSHake();
            
                    // Test tis epithesis:
//            
//        while(true){
//            DatagramSocket sk =(DatagramSocket)new DatagramSocket();
//            byte[] buf = new byte[5000];
//            DatagramPacket dp = new DatagramPacket(buf, buf.length);
//            InetAddress hostAddress = InetAddress.getByName("localhost");
//            buf = "hello".getBytes();
//            dp  = new DatagramPacket(buf, buf.length, hostAddress, 4000);
//            sk.send(dp);
//            DTLSDoS dos = new DTLSDoS(sk , dp);
//            dos.Attack();
//            sk.close();
//        }

        } catch (SocketException ex) {
            Logger.getLogger(Client_DTLS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client_DTLS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Client_DTLS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
