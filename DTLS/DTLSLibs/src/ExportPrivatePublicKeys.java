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

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import sun.misc.BASE64Encoder;

/**
 * Class pou tin xrisimopoiume gia na kanume Export
 * ta Public & Private Keys apo to .jks.
 */
public class ExportPrivatePublicKeys implements Serializable{
    private File keystoreFile;
    private String keyStoreType;
    private char[] KeystorePassword;
    private char[] aliasPassword;

    public ExportPrivatePublicKeys(File keystoreFile, String keyStoreType, char[] KeystorePassword, char[] aliasPassword) {
        this.keystoreFile = keystoreFile;
        this.keyStoreType = keyStoreType;
        this.KeystorePassword = KeystorePassword;
        this.aliasPassword = aliasPassword;
    }
    

    private static KeyPair getKeyPair(KeyStore keystore, char[] aliasPassword) {
        try {
            Enumeration enumeration = keystore.aliases();
            String alias = (String)enumeration.nextElement();
            Key key=keystore.getKey(alias, aliasPassword);
            if(key instanceof PrivateKey) {
                    Certificate cert=keystore.getCertificate(alias);
                    PublicKey publicKey=cert.getPublicKey();
                    return new KeyPair(publicKey,(PrivateKey)key);
            }
        } catch (UnrecoverableKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyStoreException e) {
        }
        return null;
    }

    public KeyPair MagicKeys() throws Exception{
            KeyStore keystore = KeyStore.getInstance(keyStoreType);
            BASE64Encoder encoder = new BASE64Encoder();
            keystore.load(new FileInputStream(keystoreFile), KeystorePassword);
            KeyPair keyPair = getKeyPair(keystore, aliasPassword);
            PrivateKey privateKey = keyPair.getPrivate();
            String encoded = encoder.encode(privateKey.getEncoded());
            return keyPair;
    }
}
