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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class pou tin xrisimopoiw gia na antalasei minimata o server me ton client.
 * Merikes apo tis methodous den xrisimopoiudai einai eitei gia prosorisi eukolia
 * kata ti diarkeia tis siggrafis tou kwdika eite gia xrisi allwn senariwn analoga
 * me to tin periptwsi xrisis.
 */
public class Chat_UDP implements Serializable{
    private DatagramPacket dp;
    private DatagramSocket sk;
    private InetAddress hostAddress;

    public Chat_UDP(DatagramPacket dp, DatagramSocket sk) {
        try {
            this.dp = dp;
            this.sk = sk;
            hostAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException ex) {
            Logger.getLogger(Chat_UDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public byte[] ReadBytes(){
        
        try {
            byte[] buffer = new byte[5000];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length );
            System.out.println("ReadByte: " + new String(packet.getData(), 0, packet.getLength()));
            sk.receive(packet);
            System.out.println("packet1: " + packet.getData());
            return packet.getData();
        } catch (IOException ex) {
            Logger.getLogger(Chat_UDP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public DatagramPacket ReadPacket(){
        try {
            byte[] buffer = new byte[5000];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length );
            sk.receive(packet);
            System.out.println("ReadByte: " + new String(packet.getData(), 0, packet.getLength()));
            return packet;
        } catch (IOException ex) {
            System.out.println("ReadByte Error");
        }
        return null;
    }
    
    public void SendBytes(byte[] bytes){
        try {
            System.out.println("Send bytes: " + bytes);
            dp  = new DatagramPacket(bytes, bytes.length, hostAddress, 4000);
            sk.send(dp);
        } catch (IOException ex) {
            System.out.println("SendBytes Error");
        }
    }
    
    /**
     * Methodos pou stelnw msg ston server alla to msg einai
     * eidi byte[]
     */
    public void SendMsg(Object obj){
        ObjectOutputStream oos = null;
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            final byte[] data = baos.toByteArray();
            final DatagramPacket packet = new DatagramPacket(data, data.length, hostAddress, 4000);
            sk.send(packet);
            System.out.println("SendMsgs: " + packet);
        } catch (IOException ex) {
            Logger.getLogger(Chat_UDP.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(Chat_UDP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
