package com.kurtisisenad.hotelmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author Senad Kurtisi
 */
public class LogsForm extends javax.swing.JFrame {

    /**
     * Creates new form LogsForm
     */
    public LogsForm() {
        initComponents();
        setLocationRelativeTo(null);
        readLogs();
    }
    
    private void readLogs(){
        try{
            File file = new File("logs.txt");
            FileReader fReader = new FileReader(file); 
            BufferedReader br = new BufferedReader(fReader);  
            
            String line = "";
            while((line = br.readLine()) != null){
                logsArea.append(line + "\n");
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        logsArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LOGS");

        logsArea.setEditable(false);
        logsArea.setColumns(20);
        logsArea.setRows(5);
        jScrollPane1.setViewportView(logsArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LogsForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea logsArea;
    // End of variables declaration//GEN-END:variables
}
