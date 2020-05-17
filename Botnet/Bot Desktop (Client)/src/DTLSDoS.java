
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
public class DTLSDoS {
    private DatagramSocket s;
    private DatagramPacket dp;

    public DTLSDoS(DatagramSocket s, DatagramPacket dp) {
        this.s = s;
        this.dp = dp;
    }
    
    public DTLSDoS(){}
    
    public void Attack() throws SocketException, UnknownHostException, IOException{
        byte[] buf = new byte[1000];

        InetAddress hostAddress = InetAddress.getByName("localhost");


         String outMessage="Hello";
         buf = outMessage.getBytes();

         DatagramPacket out = new DatagramPacket(buf, buf.length, hostAddress, 4000);
         out = new DatagramPacket(buf, buf.length, hostAddress, 4000);
         s.send(out);
    }
}
