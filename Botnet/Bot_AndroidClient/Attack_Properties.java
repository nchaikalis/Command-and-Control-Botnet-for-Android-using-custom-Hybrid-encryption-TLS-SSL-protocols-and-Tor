/**
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

public class Attack_Properties implements Serializable{
    private String IP_Victim, Attack_Type;
    private int VictimPort;

    public Attack_Properties(){}
    public Attack_Properties(String IP_Victim, String Attack_Type, int VictimPort) {
        this.IP_Victim = IP_Victim;
        this.Attack_Type = Attack_Type;
        this.VictimPort = VictimPort;
    }

    public String getIP_Victim() {
        return IP_Victim;
    }

    public String getAttack_Type() {
        return Attack_Type;
    }

    public int getPort() {
        return VictimPort;
    }

    public void setIP_Victim(String IP_Victim) {
        this.IP_Victim = IP_Victim;
    }

    public void setAttack_Type(String Attack_Type) {
        this.Attack_Type = Attack_Type;
    }

    public void setPort(int VictimPort) {
        this.VictimPort = VictimPort;
    }
    
    
}
