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
