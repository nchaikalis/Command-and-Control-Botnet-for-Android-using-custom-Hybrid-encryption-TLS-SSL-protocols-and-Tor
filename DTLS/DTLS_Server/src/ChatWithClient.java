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

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;


/**
 * class pou tin xrisimopoioume gia na kanume efikti tin epikoinwnia
 * server-client. Stin class auti ginetai to HandShake tou Server me tou kathe Client.
 */
public class ChatWithClient extends Thread implements Serializable{
    private boolean keepWalking = true;//otan thelei na spasei to chat.
    private String IP, PCname;
    private Process p1;
    private MyTLS dtls = new MyTLS();
    private PublicKey pubKey;
    private SecretKey secKey;
    private Mac mac; //gia to HMAC
    private byte[] buf = new byte[5000];
    private DatagramPacket dp;
    private DatagramSocket sk;
    private Cookie cookie;
    private MyTLS myDTLS = new MyTLS();//object diki mas class gia custom ssl/tls
    private int NumberOfCookie; //pairnei to sum apo ti main.
    private Chat_UDP chating;

    public ChatWithClient(DatagramSocket sk ,DatagramPacket dp, int NumberOfCookie) {
        this.dp = dp;
        this.sk = sk;
        this.NumberOfCookie = NumberOfCookie;
    }

    
    /**
     * synchronized void run gia na trehei kathe fora pou bainei kapoios
     * neos client sto server alla k se periptwsi pou bun polloi mazi na synhronizei
     * ta nimata tus.
     */
    @Override
    public synchronized void run(){
        chating = new Chat_UDP(dp, sk);//anikeimeno tis Chat gia to Chat server-client
        chating.ReadPacket();//Diavazw hello apo ton client
        CreateCookie();//Dimiourgw ena cookie
        chating.SendMsg(cookie);//Stelnw Cookie pou dimiourgisa ston client
        System.out.println("w8ing for cookie");
        //Perimenw na apadisi me cookie o client.
        DatagramPacket inputCookie = chating.ReadPacket();//Perimenw na diavaswt to cookie apo client
        Object tmp = (Object) myDTLS.deserialize(inputCookie.getData());//kanw deserialize ta bytes se object.
        /*An elava cookie ksekinaw ti diadikasia me ta certificates hmac etc (custom ssl/tls)
        * An den lavw cookie tote dn ksekinaw tin epikoinwnia tou server me tou client gt einai eite apo spoof IP 
        * eite den exei steilei to swsto cookie.
        * Stin periptwsi auti einai pou o dtls server trwwei dos/ddos. Skopos tou dos/ddos autou
        * einai na paragw oso to dynato perissotera cookies gia na desmeusw porous tis CPU/Ram
        * Mexris otu o server na einai se simeio na mn borei na eksipiretisei neous clients.
        */
        
        if(tmp instanceof Cookie){
            Cookie CookieToBe = (Cookie) tmp;
            CookieValidate(CookieToBe);
            System.out.println("Cookie from Client: " + CookieToBe.getName() +" " + CookieToBe.getValue());
            ServerClientHandSHake();
            Chat_Thread.start();
        }
        else{
            System.out.println("Cookie validation failed");
        }
    }
        
    /**
     * Methodos pou tin xrisimopoiume gia na kleisume to connection me kapoion 
     * client.
     */
    private void StopConnection(){
        keepWalking = false;
        Chat_Thread.interrupt();
        sk.close();
        
    }
    
    /**
     * Methodos pou Dimiourgw to Cookie pou tha stilw stin Client kai perimenw
     * na mu to stilei pisw.
     */
    public Cookie CreateCookie(){
        try {
            /**
             * to kathe cookie exei to onoma DTLSServerCookie_NumberOfCookie
             * to NumberOfCookie einai i metavliti sum pou exw parei ston 
             * constructor kai dihnw ousiastika poio cookie einai auto. 
             */
            cookie = new Cookie("DTLSServerCookie_"+NumberOfCookie, Inet4Address.getLocalHost().getHostAddress());
            System.out.println("Cookie: " + cookie);
            return cookie;
        } catch (UnknownHostException ex) {
            Logger.getLogger(ChatWithClient.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    //Cookie Validation:
    public boolean CookieValidate(Cookie ClientCookie){
        //checkarw an to name k i ip tou cookie einai idia me to cookie pu tu esteila.
        if(cookie.getValue().equals(ClientCookie.getValue()) && cookie.getName().equals(ClientCookie.getName()))
            return true;
        return false;
    }
    
    /**
     * Methodos pou kanw ksekinaw to ipoloipo HandShake afu exei teleiwsei to
     * validate tou cookie epitixos.
     */
    private void ServerClientHandSHake(){
        try {
            System.out.println("HandShake Method: ");
            X509Certificate ca = dtls.GetCA();//Pairnw tin CA.
            //System.out.println("ca: " + ca);
            chating.SendMsg(ca);//stelnw tin ca.
            DatagramPacket SecKeyPacket = chating.ReadPacket();//Diavazw to DatagramPacket pou periexei ta bytes tou Secret Key encypt me to Public.
            //Object tmp = (Object) myDTLS.deserialize(SecKeyPacket.getData());
            byte[] SecKeyBytes = (byte[]) SecKeyPacket.getData();//vriskw ta bytes DatagramPacket
            SecKeyBytes = myDTLS.deserializeFromBytesToBytes(SecKeyBytes);//apo ta bytes tou DatagramPacket vriksw ta bytes tou encrypt Sec Key
            //System.out.println("Encrypt SecKeyBytes" + SecKeyBytes);
            
            //Vriksw to private key gia na kanw Decrypt ta bytes kai na vrw to Sec Key.
            KeyPair keypair = dtls.GetKeyPair();
            PrivateKey privKey = keypair.getPrivate();
            
            secKey = dtls.DecryptSecKey(SecKeyBytes, privKey);//Vriksw to sec key.
            System.out.println("Sec Key: " + secKey);
            //Create HMAC:
            mac = Mac.getInstance("HmacSHA256");//setarw ena hmac.
            mac.init(secKey);//kanw init to hmac.
            System.out.println("HMAC is rdy for use.");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ChatWithClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(ChatWithClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    //HMAC Validation:
    public Object HMacValidationAndDecrypt(byte[] ciphertext){
        System.out.println("HMacValidationAndDecrypt 1");
        byte[] macBytes = mac.doFinal(ciphertext);
        if(MessageDigest.isEqual(macBytes, mac.doFinal(ciphertext))){
            //System.out.println("Encrypt MSG: " + ciphertext);
            Object readObj = dtls.DecryptWithSecKey(ciphertext, secKey);
            System.out.println("HMacValidationAndDecrypt: " + readObj.toString());

            return readObj;
        }
        
        System.out.println("HMacValidationAndDecrypt FAILED");
        return null;
    }  
    
    /**
     * Checkarume an to socket einai kleisto an einai kleisto epistefi true
     * ara o client autos ehei aposindethei apo to server mas i ehei pesei.
     * An girisei false tote client sinehizei na einai syndedemenos.
     */
    public boolean ConnectionIsClosed(){
        return sk.isClosed();
    }
    
    /**
     * Chat metaksi tou DTLS k twn Clients.
     */
    private Thread Chat_Thread = new Thread(() -> {
        System.out.println("Chat is rdy.");
        
        Object hello = "hello client";
        byte[] firstmsg = dtls.EncryptWithSecKey(hello, secKey); //encryp to msg me to sec key
        System.out.println("first: " + firstmsg);
//        System.out.println(dtls.DecryptWithSecKey(firstmsg, secKey));
        chating.SendMsg(firstmsg);
        
        /**
         * Sto simeio auto afu pleon exume kanei validate olo to connection
         * kai exume adalaksei ena encrypt msg epitixos einai to simeio pou to 
         * programma synexizei analoga me to ti thelei o kathenas na kanei
         * emeis to afisame keno se ena apeiro while wste i epikoinwnia 
         * na mn klinei etsi k alliws skopos einai na kanume DoS/DDoS ston DTLS server
         * kai oxi na ftiaksume ena olokliro DTLS server me oloklromenes leiturgies
         * ektos tou security.
         */
        while(keepWalking == true){

        }
    });  
    
    
    
}
