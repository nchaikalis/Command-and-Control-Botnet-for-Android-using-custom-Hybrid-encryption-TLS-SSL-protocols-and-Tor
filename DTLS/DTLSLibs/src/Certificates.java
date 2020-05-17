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

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

public class Certificates {
    
    private String jks;

    public Certificates() {
    }

    public Certificates(String jks) {
        this.jks = jks;
    }

    public KeyManagerFactory FindPubKey(){
        KeyManagerFactory serverKeyManager = null;
        try {
            //load server private key
            KeyStore serverKeys = KeyStore.getInstance("JKS","SUN");
            serverKeys.load(new FileInputStream(jks + ".jks"),"123456".toCharArray());
            serverKeyManager = KeyManagerFactory.getInstance("SunX509");
            serverKeyManager.init(serverKeys,"123456".toCharArray());
            return serverKeyManager;
        } catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | IOException | CertificateException | UnrecoverableKeyException ex) {

        }
        return null;
    }
    
    public TrustManagerFactory FindCaKey(){
        TrustManagerFactory trustManager = null;
        try {
            //load ca
            KeyStore caPubKey = KeyStore.getInstance("JKS","SUN");
            caPubKey.load(new FileInputStream("ca.jks"),"123456".toCharArray());
            trustManager = TrustManagerFactory.getInstance("SunX509");
            trustManager.init(caPubKey);
            return trustManager;
        } catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | IOException | CertificateException ex) {
            return null;
        } 
    }
      
}
