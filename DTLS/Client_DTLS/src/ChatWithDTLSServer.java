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
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;

/**
 * class pou tin xrisimopoioume gia na kanume efikti tin epikoinwnia
 * clinet-server. Stin class auti ginetai to HandShake tou Client me tou kathe Server.
 */
public class ChatWithDTLSServer implements Serializable{
    private boolean keepWalking = true;
    private Process p1;
    private PublicKey pubKey;
    private SecretKey secKey;
    private Mac mac;
    private byte[] buf = new byte[5000];
    private DatagramPacket dgp;
    private DatagramSocket sk;
    private Cookie cookie;
    private MyTLS dtls = new MyTLS();
    private Chat_UDP chating;

    public ChatWithDTLSServer(DatagramSocket sk ,DatagramPacket dgp) {
        this.dgp = dgp;
        this.sk = sk;
    }
    
    /**
     * Methodos pou kanw to HandShake tou Client me tou Server.
     */
    public void ClientServerHandSHake() throws UnsupportedEncodingException, InterruptedException{
        try {
            chating = new Chat_UDP(dgp, sk);
            Object obj = "Hello Server";
            chating.SendMsg(obj);//Stlenw hello sto server
            DatagramPacket read = chating.ReadPacket();//Diavazw to cookie apo to server
            Cookie cookie = (Cookie) dtls.deserialize(read.getData());//Vriskw to cookie tu server
            System.out.println("Cookie: " + cookie.getName());
            chating.SendMsg(cookie);//Stelnw to cookie pisw sto server.
            read = chating.ReadPacket(); //diavazw tin ca se morfi DatagramPacket
            X509Certificate caFromServer = (X509Certificate) dtls.deserialize(read.getData());
            pubKey = dtls.VerifyAndGetPubFromCA(caFromServer.getEncoded()); // Vriskw to Public Key apo tin CA
            secKey = dtls.CreateSecretKey(); // Dimiourgw ena Secret Key.
            System.out.println("Sec Key: " + secKey);
            byte[] encryptedKey = dtls.EncryptSecKeyWithPubKey(secKey, pubKey);//Encrypt to Secret Key me to Pub Key
            chating.SendMsg(encryptedKey);//Stlenw to encrypt Sec Key
            //Create HMAC:
            mac = Mac.getInstance("HmacSHA256");
            mac.init(secKey);
            System.out.println("handshake complete. HMAC is ready.");
            Chat_Thread.start();
        } catch (InvalidKeyException ex) {
            Logger.getLogger(ChatWithDTLSServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ChatWithDTLSServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateEncodingException ex) {
            Logger.getLogger(ChatWithDTLSServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //HMAC Validation & Decrypt ta byte[] pou exume parei apo to server.
    private Object HMacValidationAndDecrypt(byte[] ciphertext){
        
//        System.out.println("HMacValidationAndDecrypt 1" + ciphertext);
        byte[] macBytes = mac.doFinal(ciphertext);
        
        if(MessageDigest.isEqual(macBytes, mac.doFinal(ciphertext))){
//            System.out.println("ciphertext: " + ciphertext);
            Object readObj = dtls.DecryptWithSecKey(ciphertext, secKey);
            //System.out.println("HMacValidationAndDecrypt return: " + readObj.toString());
            return readObj;
        }
        
        System.out.println("HMacValidationAndDecrypt FAILED");
        return null;
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
     * Chat metaksi tou Client k twn DTLS server.
     */
    private Thread Chat_Thread = new Thread(() -> {
        System.out.println("chat is rdy.");
        //Diavzw tp DatagramPacket
        DatagramPacket packet = chating.ReadPacket();
        byte[] firstMsg = (byte[]) packet.getData();//pairnw ta bytes tou packet.
        firstMsg = dtls.deserializeFromBytesToBytes(firstMsg);//Pairnw ta bytes twn bytes pou pira apo to get.Data();
        Object readObj = HMacValidationAndDecrypt(firstMsg);  //Kanw Validate to HMAC.
        System.out.println("Decrypted msg: " + readObj);
        
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
