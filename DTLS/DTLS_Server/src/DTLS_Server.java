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
import java.io.IOException;
import java.io.Serializable;
import static java.lang.Thread.sleep;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
public class DTLS_Server implements Serializable{

    /**
     * I efarmogi auti anoigei enan UDP server me custom SSL/TLS diladi enan DTLS server
     * pou antalasei kriptogrfimena minimata me tus clients tu.
     * Gia to skopo tis ergasias (na kanume DDoS ston DTLS server) antalasetai 
     * mono ena encrypt msg k meta apla i syndesi tou server - client paramenei 
     * anoiktei mexris otu na termatisun k oi dyo.
     */
    public static void main(String[] args) throws SocketException, IOException, InterruptedException {
    
        int PORT = 4000;//dilwnw to port tu server
        byte[] buf = new byte[5000]; //dilwnw ta bytes pu tha einai ena minima.
        DatagramPacket dgp = new DatagramPacket(buf, buf.length);//Setarw to megethos tou kathe paketu
        DatagramSocket sk =(DatagramSocket) new DatagramSocket(PORT);//Setarw to socket sto 4000 port.

        
        System.out.println("Server started");  
        int sum = 0;//sum gia to plithos ton online xristwn
        //Ksekinaw enan Multi Thread UDP server.
        while (true) {
            sk.receive(dgp);
            sum++;
            new Thread(new ChatWithClient(sk, dgp, sum)).start(); //Neos client.
            sleep(100);
        }
    
    }
}
