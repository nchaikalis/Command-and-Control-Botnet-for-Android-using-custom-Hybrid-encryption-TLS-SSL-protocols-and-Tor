package botnet.botmasterandroidclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alcohealism
 */
public class EncryptDecrypt implements Serializable{
    private String Creator;
    private PrivateKey Priv;
    private PublicKey PubKey;

    public EncryptDecrypt(String Creator) {
        this.Creator = Creator;
    }
    public EncryptDecrypt() {}
    
    
    public void CreateKeys(){
        try {
            //Dimiourgia twn Private and Public keys:
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair keypair = keyGen.genKeyPair();
            Priv = keypair.getPrivate();
            PubKey = keypair.getPublic();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Adynameia stin dimiourgeia private / public keys.");
        }
    }
    public void CreatePrivPubKeysToFile(){
        try {
            //Dimiourgia twn Private and Public keys:
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(128);
            KeyPair keypair = keyGen.genKeyPair();
            Priv = keypair.getPrivate();
            PubKey = keypair.getPublic();
            System.out.println("My Priv: " + Priv + "\nMy Pub: " + PubKey);

            ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(Creator+"PublicKeyForTOR.key"));
            publicKeyOS.writeObject(PubKey);
            publicKeyOS.close();

            ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(Creator+"PrivateForTOR.key"));
            privateKeyOS.writeObject(Priv);
            privateKeyOS.close();
        
        } catch (NoSuchAlgorithmException | FileNotFoundException ex) {
            System.out.println("Adynameia stin dimiourgeia private / public keys.");
        } catch (IOException ex) {
            System.out.println("Adynameia stin dimiourgeia private / public keys.");
        }
    }
    /**
     * Dimiourgw ena SecretKey.
     * Xrisimopoiite apo ta BotMaster.
     * @return 
     */
    public SecretKey CreateSecretKey(){
        try {
            KeyGenerator aes = KeyGenerator.getInstance("AES");
            aes.init(128);
            SecretKey SecKey = aes.generateKey();
            return SecKey;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Methodos gia Decrypt. Pairnw os orisma byte[] kai to PrivateKey k 
     * kanw decrypt ta byte[] k ta metatrepo se SecretKey.
     * Xrisimopoiite apo to Server.
     * @param encryptMSG
     * @param privKey
     * @return 
     */
    public SecretKey DecryptSecKey(byte[] encryptMSG, PrivateKey privKey){
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            byte[] decrypted = cipher.doFinal(encryptMSG);
            SecretKey SecKey = new SecretKeySpec(decrypted, "AES");
            return SecKey;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
    
    /**
     * Dexome os orisma tO SecretKey k to Public kai kriptografw to 
     * SecretKey me to Public kai ta metatrepo se byte[] ta byte auta ta kanw
     * return.
     * I methodos auti xrisimopoiite apo ta Bots.
     */
    public byte[] EncryptSecKeyWithPubKey(SecretKey SecKey, PublicKey pubkey){
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubkey);
            byte[] cipherSecKey = cipher.doFinal(SecKey.getEncoded());
            return cipherSecKey;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    /**
     * Methodos pou dexetai os orisma ena String msg kai to SecretKey
     * kai kriprografw to String me to SecretKey kai epistrefw ta bytep[]
     * Xrisimopoiite kai apo ton Server kai apo tin Client.
     */
    public byte[] EncryptWithSecKey(String forEncrypt, SecretKey SecKey){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, SecKey);

            byte[] cipherMSG = cipher.doFinal(forEncrypt.getBytes());//encrypt with sec key
            return cipherMSG;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
     /**
     * Methodos pou dexetai os orisma ena Object msg kai to SecretKey
     * kai kriprografw to Object me to SecretKey kai epistrefw ta bytep[]
     * Xrisimopoiite kai apo ton Server kai apo tin Client.
     */
    public byte[] EncryptWithSecKey(Object obj, SecretKey SecKey){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, SecKey);
            byte[] tmp = serialize(obj);
            byte[] cipherMSG = cipher.doFinal(tmp);//encrypt with sec key
            return cipherMSG;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Dexomai os orisma byte[] kai to SecretKey kai me to SecretKey apokriptografw
     * ta byte[]. Ta byte auta meta ta metatrepw se Objects kai ta kanw return.
     */
    public byte[] DecryptWithSecKey(byte[] cipherMSG, SecretKey SecKey){
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, SecKey);
            byte[] plainText = cipher.doFinal(cipherMSG);
            System.out.println("readByte" + plainText);
            return plainText;
//            String str = new String(plainText, "UTF-8");
//            System.out.println("Decrypt msg: " );
//            System.out.println(str);
//            return deserialize(plainText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
//catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(MySSL.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return null;
    }
    
    /**
     * Methodos pouy pairnw ena object kai to metatrepo se byte[]
     */
    public byte[] serialize(Object obj) {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(out);
            oos.writeObject(obj);
            return out.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }
    
    /**
     * Methodos pou metrapw ta byte[] se Object.
     */
    public Object deserialize(byte[] data) {

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            System.out.println("in: " + in);
            ObjectInputStream is = new ObjectInputStream(in);
            return is.readObject();
        } catch (IOException ex) {
            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) { 
            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "failed";
}
//    //Decrypt to msg pu esteile o C&C
//    public Object decrypt(byte[] msg, PrivateKey key) //Dehomai ws orisma ta byte (tou kriptografimenou msg -> byte tou public key dld) kai to private key.
//    {
//
//        byte[] dectyptedText = null;
//        try 
//        {
//            System.out.println("msg: " + msg);
//            final Cipher cipher = Cipher.getInstance("RSA");
//            cipher.init(Cipher.DECRYPT_MODE, key);
//            dectyptedText = cipher.doFinal(msg);
//            System.out.println("De:"+dectyptedText.toString());
//            return dectyptedText;
//        } catch (NoSuchAlgorithmException ex) {
//            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchPaddingException ex) {
//            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalBlockSizeException ex) {
//            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (BadPaddingException ex) {
//            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidKeyException ex) {
//            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
//        } 
//        return "failed";
//    }
//    
//    //Kanw encrypt to msg pou tha steilw ston C&C
//    public byte[] encrypt(Object obj, PublicKey key) throws IOException 
//    {
//        byte[] text = serialize(obj);
//        System.out.println("text: " + text);
//        byte[] cipherText = null;//byte metavliti pou tha periexei ta bytes tou kriptografimenou pass.
//        try 
//        {
//            final Cipher cipher = Cipher.getInstance("RSA");
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            cipherText = cipher.doFinal(text);
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
//            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalBlockSizeException ex) {
//            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (BadPaddingException ex) {
//            Logger.getLogger(EncryptDecrypt.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return cipherText;
//    }
//    
//    
//    /**
//     * Methodos pouy pairnw ena object kai to metatrepo se byte[]
//     */
//    public byte[] serialize(Object obj) throws IOException {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        ObjectOutputStream os = new ObjectOutputStream(out);
//        os.writeObject(obj);
//        return out.toByteArray();
//    }
//    /**
//     * Methodos pou metrapw ta byte[] se Object.
//     */
//    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
//        ByteArrayInputStream in = new ByteArrayInputStream(data);
//        ObjectInputStream is = new ObjectInputStream(in);
//        return is.readObject();
//    }



    public String getCreator() {
        return Creator;
    }

    public void setCreator(String Creator) {
        this.Creator = Creator;
    }

    public PrivateKey getPriv() {
        return Priv;
    }

    public void setPriv(PrivateKey Priv) {
        this.Priv = Priv;
    }

    public PublicKey getPubKey() {
        return PubKey;
    }

    public void setPubKey(PublicKey PubKey) {
        this.PubKey = PubKey;
    }
    
    
}
