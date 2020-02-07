package com.kurtisisenad.hotelmanager;

import java.sql.*;
import java.awt.*;

import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import com.kurtisisenad.hotelmanager.classes.*;

/**
 *
 * @author Senad Kurtisi
 */
public class LogIn extends javax.swing.JFrame {

    Connection con;
    Employee loggedInEmployee;
    
    
    public LogIn() {
        initComponents();
        setLocationRelativeTo(null);
        setSize(650,350);
        
        setInitialGui();
        
        createConnection();
    }

    
    /**
     * Sets up log in form GUI
     */
    private void setInitialGui(){
        String pleaseText = "<html>Please enter your<br>"
                + "log in credentials</html>";
        pleaseLabel.setText(pleaseText);
        logInBtn.setContentAreaFilled(false);
        
        // sets up form icon
        String path = "logo_icon.png";
        setIconImage(Toolkit.getDefaultToolkit().
                getImage(getClass().getResource(path)));
    }
    
    
    /**
     * Creates connection to the database
     * locally stored on the PC
     */
    private void createConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hotel_manager_db",
                    "root","adminroot");
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pleaseLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        inputUsername = new javax.swing.JTextField();
        logInBtn = new javax.swing.JButton();
        inputPassword = new javax.swing.JPasswordField();
        logo = new javax.swing.JLabel();
        border = new javax.swing.JLabel();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Log In");
        setPreferredSize(new java.awt.Dimension(600, 350));
        setResizable(false);
        setSize(new java.awt.Dimension(600, 350));
        getContentPane().setLayout(null);

        pleaseLabel.setFont(new java.awt.Font("Verdana", 1, 17)); // NOI18N
        pleaseLabel.setForeground(new java.awt.Color(255, 255, 255));
        pleaseLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pleaseLabel.setText("Please enter your log in credentials");
        getContentPane().add(pleaseLabel);
        pleaseLabel.setBounds(410, 50, 220, 80);

        usernameLabel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        usernameLabel.setForeground(new java.awt.Color(255, 255, 255));
        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usernameLabel.setText("Username: ");
        getContentPane().add(usernameLabel);
        usernameLabel.setBounds(340, 140, 100, 20);

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Password: ");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(340, 190, 90, 20);

        inputUsername.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        inputUsername.setForeground(new java.awt.Color(255, 255, 255));
        inputUsername.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inputUsername.setToolTipText("Enter username...");
        inputUsername.setAlignmentX(1.0F);
        inputUsername.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        inputUsername.setOpaque(false);
        getContentPane().add(inputUsername);
        inputUsername.setBounds(440, 140, 170, 25);

        logInBtn.setBackground(new java.awt.Color(204, 102, 0));
        logInBtn.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        logInBtn.setForeground(new java.awt.Color(255, 255, 255));
        logInBtn.setText("Log In");
        logInBtn.setToolTipText("Click to log in..");
        logInBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        logInBtn.setOpaque(false);
        logInBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logInBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logInBtnMouseExited(evt);
            }
        });
        logInBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logInBtnActionPerformed(evt);
            }
        });
        getContentPane().add(logInBtn);
        logInBtn.setBounds(480, 253, 100, 30);

        inputPassword.setForeground(new java.awt.Color(255, 255, 255));
        inputPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inputPassword.setToolTipText("Enter password...");
        inputPassword.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        inputPassword.setOpaque(false);
        getContentPane().add(inputPassword);
        inputPassword.setBounds(440, 190, 170, 25);

        logo.setFont(new java.awt.Font("Felix Titling", 1, 90)); // NOI18N
        logo.setForeground(new java.awt.Color(255, 255, 255));
        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setText("HM");
        getContentPane().add(logo);
        logo.setBounds(90, 100, 190, 160);

        border.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(border);
        border.setBounds(100, 90, 190, 150);

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kurtisisenad/hotelmanager/images/background-log-in.jpeg"))); // NOI18N
        background.setMaximumSize(new java.awt.Dimension(600, 350));
        background.setMinimumSize(new java.awt.Dimension(600, 350));
        background.setPreferredSize(new java.awt.Dimension(600, 350));
        getContentPane().add(background);
        background.setBounds(0, 0, 650, 350);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    /**
     * 
     * Checks for log in credentials
     * and logs in valid user
     */
    private void logInBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logInBtnActionPerformed
        // username acquired from username text box
        String logUsername = inputUsername.getText().trim();
        // password acquired from password text box
        String logPassword = inputPassword.getText().trim();
        
        if(logUsername.isEmpty()){
            JOptionPane.showMessageDialog(null, "Username can't be empty!",
            "Invalid input", JOptionPane.ERROR_MESSAGE);            
        } else if(logPassword.isEmpty()){
            JOptionPane.showMessageDialog(null, "Password can't be empty!",
            "Invalid input", JOptionPane.ERROR_MESSAGE);             
        }
        else{
            try{
                // create SQL statement pattern
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM "
                        + "EMPLOYEES WHERE username=? AND password=?");
                // set username check
                stmt.setString(1, logUsername);
                // set password check
                stmt.setString(2, logPassword);
                stmt.execute();
            
                // get search results
                ResultSet resSet = stmt.getResultSet();
            
                // check if there are any results
                if(resSet.next()){
                    int id = resSet.getInt("id");
                    new HotelManager(id).setVisible(true);
                    this.setVisible(false);
                }else{
                    // Show invalid input message
                    JOptionPane.showMessageDialog(null, "Wrong log "
                            + "in credentials!", "Invalid input", 
                            JOptionPane.ERROR_MESSAGE);
                }
                // closing the statement
                stmt.close();
            } catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }//GEN-LAST:event_logInBtnActionPerformed
  
    
    private void logInBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logInBtnMouseEntered
        logInBtn.setForeground(Color.black);
        logInBtn.setBorder(
                new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_logInBtnMouseEntered

    
    private void logInBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logInBtnMouseExited
        logInBtn.setForeground(Color.white);
        logInBtn.setBorder(
                new javax.swing.border.LineBorder(Color.white, 1, true)
        );       
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_logInBtnMouseExited

    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LogIn().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JLabel border;
    private javax.swing.JPasswordField inputPassword;
    private javax.swing.JTextField inputUsername;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton logInBtn;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel pleaseLabel;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
