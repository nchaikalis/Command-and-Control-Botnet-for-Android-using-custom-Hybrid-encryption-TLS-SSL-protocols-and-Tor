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
    private byte[] buf = new byte[5000];


    public Chat_UDP(DatagramPacket dp, DatagramSocket sk) {
        this.dp = dp;
        this.sk = sk;
    }
    
    /**
     * Methodos pou tin xrisimopoiw gia na diavasw ena packet apo ton client
     * kai kanw return pisw auto to packet.
     * @reutn DataframPacket packet
     */
    public DatagramPacket ReadPacket(){
        try {
            byte[] buffer = new byte[5000];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length );
            sk.receive(packet);
            //System.out.println("ReadByte: " + new String(packet.getData(), 0, packet.getLength()));
            return packet;
        } catch (IOException ex) {
            System.out.println("ReadByte Error");
        }
        return null;
    }
    
    /**
     * Methodos pou tin xrisimpoiw gia na diavazw to DatagramPacket pu stelenei o server
     * kai na epistrefw ta bytes.
     * @param DatagramPacket dp
     * @return byte[]
     */
    public byte[] ReadByte(DatagramPacket dp){
        System.out.println("ReadByte: " + new String(dp.getData(), 0, dp.getLength()));
        return dp.getData();
    }
    
    /**
     * Methodos pou dexomai os orisma ta bytes enos minimatos pou thelw na steilw
     * kai to metatrepw se DatagramPacket kai to stelnw.
     * @param bytes 
     */
    public void SendBytes(byte[] bytes){
        try {
            System.out.println("Send bytes: " + bytes);
            dp  = new DatagramPacket(bytes, bytes.length, dp.getAddress(), 4000);
            sk.send(dp);
        } catch (IOException ex) {
            System.out.println("SendBytes Error");
        }
    }
    
    /**
     * Methodos pou dexomai ws orisma ena object kai to metatrepw se DatagramPacket
     * kai to stelnw ston client, douleuei kai an san object dwseis byte[] apla 
     * meta stin mergia tou client tha prepei na kaneis deserialize ta bytes[] 
     * se bytes[] kai ta deutera bytes[] na ta kaneis object i oti allo thes.
     * 
     * @param obj 
     */
    public void SendMsg(Object obj){
        ObjectOutputStream oos = null;
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            final byte[] data = baos.toByteArray();
            dp = new DatagramPacket(data, data.length, dp.getAddress(), dp.getPort());
            sk.send(dp);
            System.out.println("SendMsg: " + dp);
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
