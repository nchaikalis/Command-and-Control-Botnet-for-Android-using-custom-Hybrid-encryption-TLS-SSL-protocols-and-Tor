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

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

public class Certificates {
    private Context context;
    private String jks;

    public Certificates() {
    }

    public Certificates(String jks, Context context) {
        this.jks = jks;
        this.context = context;
    }

    public KeyManagerFactory FindPubKey(){
        KeyManagerFactory serverKeyManager = null;
        try {
            //load server private key
            InputStream keyin = context.getResources().openRawResource(R.raw.bot1);
            KeyStore serverKeys = KeyStore.getInstance("BKS");
            serverKeys.load(keyin, "123456".toCharArray());
            serverKeyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            serverKeyManager.init(serverKeys, "123456".toCharArray());
            Log.d("1:", serverKeyManager.toString());
            return serverKeyManager;
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException | UnrecoverableKeyException ex) {

        }
        return null;
    }

    public TrustManagerFactory FindCaKey(){
        TrustManagerFactory trustManager = null;
        InputStream keyin = context.getResources().openRawResource(R.raw.ca1);
        try {
            //load ca
            KeyStore ks = KeyStore.getInstance("BKS");
            ks.load(keyin, "123456".toCharArray());
            trustManager = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            trustManager.init(ks);
            Log.d("2:", trustManager.toString());

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException  ex) {

        }
        return trustManager;
    }

}
