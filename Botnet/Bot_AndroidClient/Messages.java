package botnet.botmasterandroidclient; /**
 *    Ασφάλεια Δικτύων Υπολογιστών & Τεχνολογίες Προστασίας της Ιδιωτικότητας
 *                                   Άσκηση 2
 * Δημιουργία ασφαλούς διαύλου διαχείρισης botnet (C&C) με αξιοποίηση Tor και 
 * χρήση πρωτοκόλλου διασφάλισης εμπιστευτικότητας και ακεραιότητας
 * 
 *                      Πέππας Κωνσταντίνος 321/2011134
 *                      Σωτηρέλης Χρήστος   321/2012182
 *                      Χαϊκάλης Νικόλαος   321/2012200
 * 
 */

import java.io.Serializable;


/**
 *class pou dimiourgw mia methodo gia kathe automato minima pou thelw 
 * na steilw apo ton Server pros tous Clients (Bots & BotMaster) i kai apo 
 * tous Clients pros tous Server. Ta minimata auta iparxun ola mazi
 * gt i class auti einai metos enos ksexoristou project pou periehei
 * tis class (Libraries) pou ehun koina ta ypoloipa project.
 */
public class Messages implements Serializable{
    
    public Messages(){}
    
    public String SendAsServer(){
        return "C&C Server> ";
    }
    
    public String SendAsBotMaster(){
        return "BotMaster> ";
    }
    
    public String SendAsBot(){
        return "Bot> ";
    }
    public String StopConnection(){
        return "STOP_CONNECTION_WITH_THE_SERVER";
    }
    
    public String StartAttack(){
        return "START_ATTACK";
    }
    public String StopAttack(){
        return "STOP_ATTACK";
    }
    
    public String ByeBot(){
        return "Goodbye Bot";
    }
    
    public String ByeMaster(){
        return "Goodbye my Master";
    }
    
    public String GreetingsServer(){
        return "Hello proxy :D !!!";
    }
    
    public String BotSendMeYourInfos(){
        return "Hi bot plz send me your informations.";
    }
    
    public String WelcomeBot(){
        return "Welcome Bot !!!";
    }
    
    public String WelcomeMaster(){
        return "Welcome my Master.";
    }
    
    public String Hello(){
        return "Hello";
    }
    
}//End class
