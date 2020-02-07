package com.kurtisisenad.hotelmanager;

import com.kurtisisenad.hotelmanager.classes.*;

import java.sql.*;
import java.awt.*;
import java.util.ArrayList;

import java.time.format.DateTimeFormatter; 
import java.time.LocalDateTime; 
import java.text.SimpleDateFormat;  
import java.util.Date;  

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import javax.swing.*;

import java.io.*;
import java.nio.file.Files;

import com.kurtisisenad.hotelmanager.classes.Employee;

/**
 *
 * @author Senad Kurtisi
 */
public class HotelManager extends javax.swing.JFrame {

    Connection con;
    
    Employee loggedEmployee;
    int loggedLevel;
    int loggedID;
    
    float loadedPay = 0;
    int loadedLevel = 0;
    
    String fileName = null;
    byte[] imageFile = null;
    
    
    public HotelManager() {
        initComponents();
        // connect to the database
        createConnection();
        
        // create current employee object
        setCurrentEmployee(loggedID);  
        
        // Setting up GUI
        setUpGUI();
        
        // acknowledge log in event
        updateLogs(getLog(0,0));
    }
    
    
    public HotelManager(int loggedID) {
        initComponents();
        // connect to the database
        createConnection();
        
         // create current employee object
        setCurrentEmployee(loggedID); 
        
        // Setting up GUI
        setUpGUI();
               
        // acknowledge log in event
        updateLogs(getLog(0,0));
    }
    
    
    /**
     * Sets up GUI for the user
     */
    private void setUpGUI(){
        // updates tables
        updateRoomTable();
        updateItemTable();
        centerTablesContent();
        
        // set up gui based on employee level
        setUpLevelBasedGui(loggedEmployee.getLevel());
        
        setLocationRelativeTo(null);
        setSize(900,500);     
        
        // sets up form icon
        String path = "logo_icon.png";
        setIconImage(Toolkit.getDefaultToolkit().
                getImage(getClass().getResource(path)));
    }  
    
    
    /**
     * Disables/enables GUI components
     * based on employee level
     * @param level Employee level
     */
    private void setUpLevelBasedGui(int level){
        // if the employee is regular employee
        if(level==1){
            // regular employees can't manage other employees
            
            Component[] components = employeePanel.getComponents();
            for(Component cmp: components){
                cmp.setVisible(false);
            }
            backgroundEmployees.setVisible(true);
            infoText.setVisible(true);
            infoText.setText("<html> <div style='text-align: center;'> "
                    + "YOU DON'T HAVE ACCESS<br>"
                    + "TO THE EMPLOYEE INFORMATION</div></html>");
        } else if(level==2){
            //if the employee is the manager
            
            // managers can't add new employees
            addEmployeeBtn.setVisible(false);
            employeeLevel.setVisible(false);
        }
    }
    
    
    /**
     * Creates current employee object 
     * based on his id. Data about that
     * employee is taken from the database
     * @param currentID Logged employee ID
     */
    private void setCurrentEmployee(int currentID){
        // Logged employee info
        String firstName="", lastName="", 
                birthDate="", street="", 
                phoneNumber="", gender="";
        int age=0, level=0, id=0;
        byte[] image = null;
        
        try{
            // Preparing and executing query to select information
            // about currently logged employee
            Statement stmt = con.createStatement();
            String query = "SELECT * FROM EMPLOYEES WHERE id=" + currentID;
            stmt.executeQuery(query);
            
            ResultSet resSet = stmt.getResultSet();
            
            // getting the information about currently logged employee
            if(resSet.next()){
                id = resSet.getInt("id");
                firstName = resSet.getString("first_name");
                lastName = resSet.getString("last_name");
                birthDate = resSet.getString("birth_date");
                age = resSet.getInt("age");
                street = resSet.getString("street");
                phoneNumber = resSet.getString("phone_number");
                gender = resSet.getString("gender");
                level = resSet.getInt("level");
                
                loggedEmployee = new Employee(firstName, lastName, age, 
                        birthDate, street, phoneNumber, gender, level);
                loggedID = id;
            }
           
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    
    /**
     * Center the content of the columns
     * in tables available in the forms
     */
    private void centerTablesContent(){
        DefaultTableCellRenderer cenRenderer = 
                new DefaultTableCellRenderer();
        cenRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // center content of the table for room information
        for(int i=0;i<roomTable.getColumnCount();i++){
            roomTable.getColumnModel().getColumn(i).
                    setCellRenderer(cenRenderer);
        }
        
        // center content of the table for item information
        for(int i=0;i<itemTable.getColumnCount();i++){
            itemTable.getColumnModel().getColumn(i).
                    setCellRenderer(cenRenderer);
        }
        
        // center content of the table for receipt information
        for(int i=0;i<receiptTable.getColumnCount();i++){
            receiptTable.getColumnModel().getColumn(i).
                    setCellRenderer(cenRenderer);
        }
    }
    
    
    /**
     * Adds new log to the 'logs' file
     * @param log A log to be added
     */
    private void updateLogs(String log){
        try{
            // opening file for writing purposes
            File file = new File("logs.txt");
            FileWriter fWriter = new FileWriter(file, true);
            PrintWriter pWriter = new PrintWriter(fWriter);
            
            // writing to a file
            pWriter.println(log);
            
            // closing all previously
            // opened file streams
            pWriter.close();
            fWriter.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    
    /**
     * Creates log to be added to a file based on
     * code of the action that was performed by an
     * employee. 
     * @param actionCode Code of the action that was performed
     * @param targetOfAction Room number/EmployeeID/Dummy:0
     * @return Log to be added to the logs file
     */
    private String getLog(int actionCode, int targetOfAction){
        // Array that contains possible parts of the returned log
        String[] actions = {"logged in", "checked in room ", 
            "checked out room ", "updated receipt for room",
            "updated employee with ID=", 
            "gave raise to an employee with ID=", 
            "deleted an employee with ID=", 
            "added a new employee", "logged out"};
        
        String text = actions[actionCode];
        
        if(actionCode == 0 || actionCode==8){
            // If performed action is log in/log out
            // memorize the exact time of the event
            Date date = new Date();
            String dateText = date.toString();
            
            text += " " + dateText;
        } else if(actionCode !=7){
            // if occured event isn't log in/out or
            // new employee then targetOfAction
            // is taken into consideration
            text += Integer.toString(targetOfAction);
        }
        String log = loggedEmployee.getName() + " ID: " 
                + loggedID + " has ";
        log += text;
        
        return log;
    }
    
    
    /**
     * Logs out currently logged in employee
     * with caution for the accidental click
     * of the log off button
     */
    private void logOut(){
        // Check if log off button was 
        // accidentaly clicked
        int choice = JOptionPane.showConfirmDialog(
                null, "Are you sure you want to "
                        + "log out", "Confirm log out", 
                        JOptionPane.YES_NO_OPTION);

        
        if(choice==0){
            new LogIn().setVisible(true);
            dispose();
            String log = getLog(8, 0);
            updateLogs(log);
        }            
    }
    
    
    /**
     * Updates the table which contains
     * information about rooms.
     */
    private void updateRoomTable(){
        DefaultTableModel tableModel = (DefaultTableModel) 
                roomTable.getModel();
        
        //clears the table
        tableModel.setRowCount(0);
        
        try{
            // preparing and executing SQL statement
            // which selects all needed information
            // about the rooms from the database
            Statement stmt = con.createStatement();
            String query = "SELECT * FROM ACCOMMODATION";
            stmt.executeQuery(query);
            
            ResultSet resSet = stmt.getResultSet();
            
            // getting the results, if they exist
            while(resSet.next()){
                int roomNumber = resSet.getInt("room_number");
                int floor = resSet.getInt("floor");
                String type = resSet.getString("type");
                int numberOfRooms = resSet.getInt("number_of_rooms");
                boolean status = resSet.getBoolean("occupied");
                String roomStatus = (status)? "Occupied" : "Free";
                float price = resSet.getFloat("price");
                
                // adding currently acquired information
                // to the adequate table
                tableModel.addRow(new Object[]{roomNumber, floor, type,
                                    numberOfRooms, roomStatus, price}); 
            }
            
            // closing the statement
            stmt.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    
    /**
     * Adds the acquired money from guest check 
     * out to the cash register.
     * @param room Room number of the checked out guest
     * @param name Full name of the checked out guest
     * @param amount The receipt amount of the checked out guest
     */
    private void addToCashRegister(int room, String name, float amount){
        try{
            ArrayList<String> toWrite = new ArrayList<String>();
            
            // setting up file name and readers for 
            // cash register file reading/writing
            String fileName = "cash-register.txt";
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            
            int count = 1;  // row counter
            float moneyInCashRegister = 0;
            
            // reading the data from the cash register
            while((line = br.readLine())!=null){
                if(count==1){
                   // adding the acquired money
                   // to the previous amount
                   moneyInCashRegister = Float.parseFloat(line) + amount;
                   line = Float.toString(moneyInCashRegister);
                }
                toWrite.add(line);
                count++;
            }
            // Closing file streams
            br.close();
            
            // setting up writer to the cash
            // register file
            FileWriter fWriter = new FileWriter(fileName, false);
            PrintWriter out = new PrintWriter(fWriter);
            
            toWrite.add(Integer.toString(room) + "/" + name
                    + "/" + Float.toString(amount));
            
            // wiriting to a file
            for(String str:toWrite){
                out.println(str);
            }
            
            // closing file streams
            out.close();
            fWriter.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    
    /**
     * Checks if the number is float
     * @param str String representation 
     * of the number to be checked
     * @return Returns if the number is float
     */
    private boolean isFloat(String str){
        try{
            Float.parseFloat(str);
            return true;
        } catch(Exception ex){
            ex.printStackTrace();
            System.out.println("Not a float");
            return false;
        }
    }
    
    
    /**
     * Checks if the number is int
     * @param str String representation 
     * of the number to be checked
     * @return Returns if the number is int
     */
    private boolean isInt(String str){
        try{
            Integer.parseInt(str);
            return true;
        } catch(Exception ex){
            ex.printStackTrace();
            System.out.println("Not an int");
            return false;
        }
    }
    
    
    /**
     * Calculates the sum of the prices of the
     * ordered items. Items are found in the
     * Receipt table.
     */
    private void calculateTotalToAdd(String itemName, 
            int itemAmmount, float amount){
        DefaultTableModel model = (DefaultTableModel) 
                receiptTable.getModel();
        
        float sum = 0;
        // getting the number of ordered items
        int rowCount = receiptTable.getRowCount();
        
        boolean found = false;
        for(int i=0;i<rowCount;i++){
            String tableItemName = (String) model.getValueAt(i, 0);
            if(tableItemName.equals(itemName)){
                float tableItemPrice = 
                        (float) model.getValueAt(i, 2);
                float newPrice = tableItemPrice + amount;
                
                model.setValueAt(newPrice, i, 2);
                found = true;
            }
            sum += (float) model.getValueAt(i, 2);
        }
        
        if(!found){
            model.addRow(new Object[]{itemName, itemAmmount, amount});
        }
        
        // Updating the total amount counter
        float currentValue = 
                Float.parseFloat(totalReceiptValueLabel.getText());
        float newValue = currentValue + sum;
        totalReceiptValueLabel.setText(Float.toString(newValue));
    }
    
    
    /**
     * Detects if the currently logged in employee
     * has privileges to make a certain edit
     * @return 
     */
    private boolean canEdit(){
        // if the employee is the owner
        if(loggedEmployee.getLevel()==3) return true;
        
        // if the logged in employee is the same level
        // as the employee whose information he wishes
        // to edit
        if((loggedEmployee.getLevel()==2 && loadedLevel==3) ||
                loggedEmployee.getLevel() == loadedLevel || 
                loggedEmployee.getLevel()==1){
            return false;
        } else{
            return true;
        }
    }
    
    
    /**
     * Detects if the currently logged in employee
     * has privileges to delete an employee
     * @return 
     */
    private boolean canDelete(){
        if(loggedEmployee.getLevel()==loadedLevel 
                || loadedLevel==3){
            return false;
        } else{
            
            return true;
        }
    }
    
    
    /**
     * Creates a connection to the locally
     * stored database
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

    
    /**
     * Updates the table that contains 
     * information about items
     */
    private void updateItemTable(){
        DefaultTableModel tableModel = 
                (DefaultTableModel) itemTable.getModel();
        // clears the table
        tableModel.setRowCount(0);
        
        try{
            // preparing and executing statement which
            // acquires information about existing
            // items
            Statement stmt = con.createStatement();
            String query = "SELECT * FROM ITEMS";
            stmt.executeQuery(query);
            
            ResultSet resSet = stmt.getResultSet();
            
            // getting the results, if they exist
            while(resSet.next()){
                String name = resSet.getString("name");
                float price = resSet.getFloat("price");
                boolean available = resSet.getBoolean("available");
                String isAvailable = (available)? "Yes":"No";
                
                // adding current item to the item table
                tableModel.addRow(new Object[]{name, price, isAvailable});
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    
    /**
     * Clears text fields which contain information
     * about currently searched employee. This
     * method is usually called after employee
     * deletion.
     */
    private void clearSearchedInfo(){
        inputEmployeeID.setText("");
        employeeID.setText("");
        employeeFirstName.setText("");
        employeeLastName.setText("");
        employeeBirthDate.setText("");
        employeeAge.setText("");
        employeePay.setText("");
        employeeStreet.setText("");
        employeePhoneNumber.setText("");
        employeeGender.setSelectedItem("male");
        
        inputRaise.setText("");
    }
    
    
    /**
     * Saving the initial receipt for newly checked in
     * room. The initial receipt contains only the price
     * of the spent night in the room.
     * @param room
     * @param price 
     */
    private void saveInitialReceipt(int room, float price){
        try{
            // setting up output streams for 
            // file creation
            String fileName = "room-"+room+".txt";
            PrintWriter out = new PrintWriter(fileName);
            String text = Float.toString(price);
            
            // writing to a file
            out.println(text);
            
            // closing the output streams
            out.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    
    /**
     * Updating the receipt file for specified room
     * after new purchase has been verified.
     * @param room Room number for which receipt is updated
     */
    private void updateReceipt(int room){
        DefaultTableModel model = 
                (DefaultTableModel) receiptTable.getModel();
        try{
            // preparing the output stream
            // for a file update
            String fileName = "room-"+room+".txt";
            FileWriter fWriter = new FileWriter(fileName, true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            PrintWriter out = new PrintWriter(bWriter);
            
            // getting the number of ordered items
            // and scanning them
            int rowCount = receiptTable.getRowCount();
            for(int i=0;i<rowCount;i++){
                // getting the information about current item
                String currentName = (String) model.getValueAt(i, 0);
                int currentAmount = (int) model.getValueAt(i, 1);
                float currentPrice = (float) model.getValueAt(i, 2);
                
                String textToAppend = currentName + "/" 
                        + Integer.toString(currentAmount) + "/"
                        + Float.toString(currentPrice);
                
                // appending information of the current item
                out.println(textToAppend);
            }
            
            // closing output streams
            out.close();
            bWriter.close();
            fWriter.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    /**
     * Shows the receipt content on a text area
     * @param room Room number for which we analyze receipt
     * @param checkIn Check in date of the guest
     */
    private void showReceipt(int room, String checkIn){
        try {
            // setting up input stream
            // for file reading
            String fileName = "room-"+room+".txt";
            File file = new File(fileName);
            FileReader fReader = new FileReader(file);
//            InputStream fReader = getClass().
//                    getResourceAsStream(fileName);
            BufferedReader br = new BufferedReader(fReader);
            String line;

            // adding initial text to the receipt area
            receiptArea.setText("");
            receiptArea.append("Room: "+room+"\n");

            // reading from a file
            float sum = 0;
            int count = 1; 
            while ((line = br.readLine()) != null) {
                System.out.println(count);
                String[] itemInfo = line.split("/");
                String text="";
                if(count==1){
                    // calculating the number of spent days in
                    // chosen accommodation
                    Date date2 = new Date(); 
                    Date date1 = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss").parse(checkIn);
                        
                    long diff = date2.getTime() - date1.getTime();
                    int days = (int) Math.floor(diff/(1000*60*60*24));
                    
                    // setting up the lower limit
                    // for number of spent days
                    if(days==0) days = 1;
                        
                    // calculating the price of the stay
                    float roomPrice = Float.parseFloat(itemInfo[0]);
                    float price = days*roomPrice;
                    sum += price;
                        
                    text = "Days:"+days+" | Price: " + price + 
                            " - Accommodation\n";
                } else if(count>1){
                    // calculating the price of ordered items
                    // adding the current item price to the sum
                    text = itemInfo[0]+"| Amount: "+itemInfo[1] + 
                        "| Price: " + itemInfo[2] + "\n";   
                    sum += Float.parseFloat(itemInfo[2]);
                }
                count++;
                
                // adding text description to the 
                // receipt area
                receiptArea.append(text);
                String totalPriceText = Float.toString(sum);
                totalLabelNum.setText(totalPriceText);
            }
            
            // closing output stream
            br.close();
            fReader.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    
    /**
     * Checks if we have all
     * necessary information
     * about the new employee
     * @return 
     */
    private boolean canBeAdded(){
        if(employeeFirstName.getText().equals("") || 
                employeeLastName.getText().equals("")
                || employeeAge.getText().equals("")
                || employeeBirthDate.getText().equals("")
                || employeeAge.getText().equals("")
                || employeePay.getText().equals("")
                || employeeStreet.getText().equals("")
                || employeePhoneNumber.getText().equals("")){
            System.out.println("NE MOZE");
            return false;
        }
        return true;
    }
    
            
    /**
     * Checks if the specified room is available.
     * @param room Room to be checked
     * @return 
     */
    boolean roomAvailable(int room){
        try{
            // preparing the statement to select the
            // occupation status of the room
            // from the database
            Statement stmt = con.createStatement();
            String query = "SELECT occupied FROM ACCOMMODATION"
                    + " WHERE room_number="+room;
            stmt.executeQuery(query);
            
            ResultSet resSet = stmt.getResultSet();
            
            // getting the results, if the exist
            if(resSet.next()){
                boolean taken = resSet.getBoolean("occupied");
                System.out.println(taken);
                
                return (taken)? false:true;
            } else{
                return true;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    
    
    /**
     * Cleans up after a guest has checked out.
     * Cleans GUI that contains information about a room.
     * Removes the guest from the database.
     * Updates the occupation status of the room, 
     * in the database.
     * @param roomNumber Room number of checked out guest
     */
    private void cleanUpAfterCheckOut(int roomNumber){
        // clean
        inputRoomNumber.setText("");
        firstName.setText("");
        lastName.setText("");
        room.setText("");
        checkIn.setText("");
        receiptArea.setText("");
        totalLabelNum.setText("");
        
        try{
            // prepares statement which updates occupation
            // status of the room where guest was staying
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE ACCOMMODATION SET occupied=0 "
                            + "WHERE room_number=?");
            stmt.setInt(1, roomNumber);
            stmt.execute();
            
            // clear statement parameters
            // and close statement
            stmt.clearParameters();
            stmt.close();
            
            
            // prepares statement which removes
            // previous gust from the database
            stmt = con.prepareStatement(
                    "DELETE FROM GUESTS WHERE room=?");
            stmt.setInt(1, roomNumber);
            
            stmt.execute();
            stmt.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        
        try{
            // sets up output stream
            // for deleting the file
            String fileName = "room-"+roomNumber+".txt";
            File file = new File(fileName);
            
            // deleting the receipt file of
            // guest room, if file exists
            Files.deleteIfExists(file.toPath());
            
        } catch(Exception ex){
            ex.printStackTrace();
        }
        updateRoomTable();
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hotelTab = new javax.swing.JTabbedPane();
        bookingPanel = new javax.swing.JPanel();
        roomNumLabel = new javax.swing.JLabel();
        searchBtn = new javax.swing.JButton();
        lastNameLabel = new javax.swing.JLabel();
        roomLabel = new javax.swing.JLabel();
        firstNameLabel = new javax.swing.JLabel();
        checkInLabel = new javax.swing.JLabel();
        currentReceiptLabel = new javax.swing.JLabel();
        receiptTextArea = new javax.swing.JScrollPane();
        receiptArea = new javax.swing.JTextArea();
        firstName = new javax.swing.JLabel();
        lastName = new javax.swing.JLabel();
        room = new javax.swing.JLabel();
        checkIn = new javax.swing.JLabel();
        totalCheckLabel = new javax.swing.JLabel();
        totalLabelNum = new javax.swing.JLabel();
        bookingLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        roomTable = new javax.swing.JTable();
        roomListLabel = new javax.swing.JLabel();
        firstNameCheckInLabel = new javax.swing.JLabel();
        lastNameCheckInLabel = new javax.swing.JLabel();
        firstNameCheckIn = new javax.swing.JTextField();
        lastNameCheckIn = new javax.swing.JTextField();
        addGuestBtn = new javax.swing.JButton();
        checkOutBtn = new javax.swing.JButton();
        logoutBtn2 = new javax.swing.JButton();
        inputRoomNumber = new javax.swing.JFormattedTextField();
        backgroundBooking = new javax.swing.JLabel();
        menuPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        itemTable = new javax.swing.JTable();
        itemsLabel = new javax.swing.JLabel();
        amount = new javax.swing.JSpinner();
        addItemBtn = new javax.swing.JButton();
        jScrollPane17 = new javax.swing.JScrollPane();
        receiptTable = new javax.swing.JTable();
        totalReceiptLabel = new javax.swing.JLabel();
        totalReceiptValueLabel = new javax.swing.JLabel();
        roomToLabel = new javax.swing.JLabel();
        verifyBtn = new javax.swing.JButton();
        currentLabel = new javax.swing.JLabel();
        logoutBtn1 = new javax.swing.JButton();
        inputToRoomNumber = new javax.swing.JFormattedTextField();
        backgroundMenu = new javax.swing.JLabel();
        employeePanel = new javax.swing.JPanel();
        employeeIDLabel = new javax.swing.JLabel();
        searchBtnEmployee = new javax.swing.JButton();
        employeeFirstNameLabel = new javax.swing.JLabel();
        employeeLastNameLabel = new javax.swing.JLabel();
        employeBirthDateLabel = new javax.swing.JLabel();
        ageLabel = new javax.swing.JLabel();
        payLabel = new javax.swing.JLabel();
        streetLabel = new javax.swing.JLabel();
        phoneNumberLabel = new javax.swing.JLabel();
        employeeFirstName = new javax.swing.JTextField();
        employeeID_Label = new javax.swing.JLabel();
        employeeLastName = new javax.swing.JTextField();
        employeeStreet = new javax.swing.JTextField();
        employeePhoneNumber = new javax.swing.JTextField();
        employeeID = new javax.swing.JTextField();
        genderLabel = new javax.swing.JLabel();
        imagePlaceholder = new javax.swing.JLabel();
        uploadBtn = new javax.swing.JButton();
        updateBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        raseLabel = new javax.swing.JLabel();
        raiseBtn = new javax.swing.JButton();
        percentLabel = new javax.swing.JLabel();
        addEmployeeBtn = new javax.swing.JButton();
        employeeGender = new javax.swing.JComboBox<>();
        employeeLevel = new javax.swing.JComboBox<>();
        logoutBtn = new javax.swing.JButton();
        logsBtn = new javax.swing.JLabel();
        employeeBirthDate = new javax.swing.JFormattedTextField();
        employeeAge = new javax.swing.JFormattedTextField();
        employeePay = new javax.swing.JFormattedTextField();
        inputRaise = new javax.swing.JFormattedTextField();
        inputEmployeeID = new javax.swing.JFormattedTextField();
        infoText = new javax.swing.JLabel();
        backgroundEmployees = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Hotel Manager");
        setPreferredSize(new java.awt.Dimension(900, 500));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        bookingPanel.setLayout(null);

        roomNumLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        roomNumLabel.setForeground(new java.awt.Color(255, 255, 255));
        roomNumLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        roomNumLabel.setText("Room number: ");
        bookingPanel.add(roomNumLabel);
        roomNumLabel.setBounds(40, 30, 100, 20);

        searchBtn.setBackground(new java.awt.Color(255, 255, 255));
        searchBtn.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        searchBtn.setForeground(new java.awt.Color(255, 255, 255));
        searchBtn.setText("Search");
        searchBtn.setToolTipText("Search guest info");
        searchBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        searchBtn.setOpaque(false);
        searchBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchBtnMouseExited(evt);
            }
        });
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });
        bookingPanel.add(searchBtn);
        searchBtn.setBounds(220, 30, 70, 25);

        lastNameLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lastNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        lastNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lastNameLabel.setText("Last Name :");
        bookingPanel.add(lastNameLabel);
        lastNameLabel.setBounds(40, 110, 80, 30);

        roomLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        roomLabel.setForeground(new java.awt.Color(255, 255, 255));
        roomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        roomLabel.setText("Room :");
        bookingPanel.add(roomLabel);
        roomLabel.setBounds(40, 150, 50, 20);

        firstNameLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        firstNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        firstNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        firstNameLabel.setText("First Name :");
        bookingPanel.add(firstNameLabel);
        firstNameLabel.setBounds(30, 80, 100, 20);

        checkInLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        checkInLabel.setForeground(new java.awt.Color(255, 255, 255));
        checkInLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        checkInLabel.setText("Check In :");
        bookingPanel.add(checkInLabel);
        checkInLabel.setBounds(20, 180, 110, 20);

        currentReceiptLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        currentReceiptLabel.setForeground(new java.awt.Color(255, 255, 255));
        currentReceiptLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentReceiptLabel.setText("Current Receipt: ");
        bookingPanel.add(currentReceiptLabel);
        currentReceiptLabel.setBounds(40, 220, 120, 20);

        receiptArea.setEditable(false);
        receiptArea.setColumns(20);
        receiptArea.setRows(8);
        receiptArea.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        receiptTextArea.setViewportView(receiptArea);

        bookingPanel.add(receiptTextArea);
        receiptTextArea.setBounds(40, 250, 260, 140);

        firstName.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        firstName.setForeground(new java.awt.Color(255, 255, 255));
        firstName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        bookingPanel.add(firstName);
        firstName.setBounds(150, 80, 100, 20);

        lastName.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lastName.setForeground(new java.awt.Color(255, 255, 255));
        lastName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        bookingPanel.add(lastName);
        lastName.setBounds(150, 110, 80, 30);

        room.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        room.setForeground(new java.awt.Color(255, 255, 255));
        room.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        bookingPanel.add(room);
        room.setBounds(150, 150, 50, 20);

        checkIn.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        checkIn.setForeground(new java.awt.Color(255, 255, 255));
        checkIn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        bookingPanel.add(checkIn);
        checkIn.setBounds(150, 180, 160, 20);

        totalCheckLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        totalCheckLabel.setForeground(new java.awt.Color(255, 255, 255));
        totalCheckLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalCheckLabel.setText("Total :");
        bookingPanel.add(totalCheckLabel);
        totalCheckLabel.setBounds(140, 400, 50, 20);

        totalLabelNum.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        totalLabelNum.setForeground(new java.awt.Color(255, 255, 255));
        totalLabelNum.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalLabelNum.setText("000");
        bookingPanel.add(totalLabelNum);
        totalLabelNum.setBounds(220, 400, 100, 20);

        bookingLabel.setFont(new java.awt.Font("Verdana", 1, 36)); // NOI18N
        bookingLabel.setForeground(new java.awt.Color(255, 255, 255));
        bookingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bookingLabel.setText("BOOKING");
        bookingPanel.add(bookingLabel);
        bookingLabel.setBounds(500, 40, 220, 40);

        roomTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Room number", "Floor", "Type", "Rooms", "Status", "Price"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        roomTable.setToolTipText("");
        roomTable.setGridColor(new java.awt.Color(0, 0, 0));
        roomTable.setOpaque(false);
        roomTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(roomTable);
        if (roomTable.getColumnModel().getColumnCount() > 0) {
            roomTable.getColumnModel().getColumn(0).setResizable(false);
            roomTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            roomTable.getColumnModel().getColumn(1).setResizable(false);
            roomTable.getColumnModel().getColumn(2).setResizable(false);
            roomTable.getColumnModel().getColumn(3).setResizable(false);
            roomTable.getColumnModel().getColumn(4).setResizable(false);
            roomTable.getColumnModel().getColumn(5).setResizable(false);
        }

        bookingPanel.add(jScrollPane1);
        jScrollPane1.setBounds(370, 110, 430, 220);

        roomListLabel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        roomListLabel.setForeground(new java.awt.Color(255, 255, 255));
        roomListLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        roomListLabel.setText("Room list: ");
        bookingPanel.add(roomListLabel);
        roomListLabel.setBounds(360, 80, 110, 20);

        firstNameCheckInLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        firstNameCheckInLabel.setForeground(new java.awt.Color(255, 255, 255));
        firstNameCheckInLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        firstNameCheckInLabel.setText("First Name :");
        bookingPanel.add(firstNameCheckInLabel);
        firstNameCheckInLabel.setBounds(410, 360, 100, 20);

        lastNameCheckInLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        lastNameCheckInLabel.setForeground(new java.awt.Color(255, 255, 255));
        lastNameCheckInLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lastNameCheckInLabel.setText("Last Name :");
        bookingPanel.add(lastNameCheckInLabel);
        lastNameCheckInLabel.setBounds(420, 400, 80, 30);

        firstNameCheckIn.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        firstNameCheckIn.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        firstNameCheckIn.setToolTipText("Enter first name");
        firstNameCheckIn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        bookingPanel.add(firstNameCheckIn);
        firstNameCheckIn.setBounds(530, 360, 130, 25);

        lastNameCheckIn.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lastNameCheckIn.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lastNameCheckIn.setToolTipText("Enter last name...");
        lastNameCheckIn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        bookingPanel.add(lastNameCheckIn);
        lastNameCheckIn.setBounds(530, 400, 130, 25);

        addGuestBtn.setBackground(new java.awt.Color(255, 255, 255));
        addGuestBtn.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        addGuestBtn.setForeground(new java.awt.Color(255, 255, 255));
        addGuestBtn.setText("ADD GUEST");
        addGuestBtn.setToolTipText("Add new guest");
        addGuestBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        addGuestBtn.setOpaque(false);
        addGuestBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addGuestBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addGuestBtnMouseExited(evt);
            }
        });
        addGuestBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGuestBtnActionPerformed(evt);
            }
        });
        bookingPanel.add(addGuestBtn);
        addGuestBtn.setBounds(690, 370, 90, 30);

        checkOutBtn.setBackground(new java.awt.Color(200, 0, 0));
        checkOutBtn.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        checkOutBtn.setForeground(new java.awt.Color(200, 0, 0));
        checkOutBtn.setText("CHECK OUT");
        checkOutBtn.setToolTipText("Check out the guest");
        checkOutBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 2, true));
        checkOutBtn.setOpaque(false);
        checkOutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                checkOutBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                checkOutBtnMouseExited(evt);
            }
        });
        checkOutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkOutBtnActionPerformed(evt);
            }
        });
        bookingPanel.add(checkOutBtn);
        checkOutBtn.setBounds(40, 400, 80, 25);

        logoutBtn2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kurtisisenad/hotelmanager/images/logout.png"))); // NOI18N
        logoutBtn2.setBorder(null);
        logoutBtn2.setContentAreaFilled(false);
        logoutBtn2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtn2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtn2MouseExited(evt);
            }
        });
        logoutBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutBtn2ActionPerformed(evt);
            }
        });
        bookingPanel.add(logoutBtn2);
        logoutBtn2.setBounds(810, 20, 60, 50);

        inputRoomNumber.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        inputRoomNumber.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        inputRoomNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inputRoomNumber.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        bookingPanel.add(inputRoomNumber);
        inputRoomNumber.setBounds(150, 30, 50, 25);

        backgroundBooking.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kurtisisenad/hotelmanager/images/background-log-in.jpeg"))); // NOI18N
        bookingPanel.add(backgroundBooking);
        backgroundBooking.setBounds(0, 0, 900, 500);

        hotelTab.addTab("Booking Management", bookingPanel);

        menuPanel.setLayout(null);

        itemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Price", "Available"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(itemTable);
        if (itemTable.getColumnModel().getColumnCount() > 0) {
            itemTable.getColumnModel().getColumn(0).setResizable(false);
            itemTable.getColumnModel().getColumn(1).setResizable(false);
            itemTable.getColumnModel().getColumn(2).setResizable(false);
        }

        menuPanel.add(jScrollPane2);
        jScrollPane2.setBounds(30, 60, 340, 360);

        itemsLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        itemsLabel.setForeground(new java.awt.Color(255, 255, 255));
        itemsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        itemsLabel.setText("Items");
        menuPanel.add(itemsLabel);
        itemsLabel.setBounds(10, 30, 100, 20);

        amount.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        amount.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        amount.setToolTipText("Choose amount");
        menuPanel.add(amount);
        amount.setBounds(390, 110, 50, 40);

        addItemBtn.setBackground(new java.awt.Color(0, 255, 0));
        addItemBtn.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        addItemBtn.setForeground(new java.awt.Color(0, 255, 0));
        addItemBtn.setText("ADD");
        addItemBtn.setToolTipText("Add item to order list");
        addItemBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        addItemBtn.setOpaque(false);
        addItemBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addItemBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addItemBtnMouseExited(evt);
            }
        });
        addItemBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addItemBtnActionPerformed(evt);
            }
        });
        menuPanel.add(addItemBtn);
        addItemBtn.setBounds(390, 170, 50, 40);

        receiptTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Amount", "Price"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane17.setViewportView(receiptTable);
        if (receiptTable.getColumnModel().getColumnCount() > 0) {
            receiptTable.getColumnModel().getColumn(0).setResizable(false);
            receiptTable.getColumnModel().getColumn(1).setResizable(false);
            receiptTable.getColumnModel().getColumn(2).setResizable(false);
        }

        menuPanel.add(jScrollPane17);
        jScrollPane17.setBounds(470, 60, 320, 250);

        totalReceiptLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        totalReceiptLabel.setForeground(new java.awt.Color(255, 255, 255));
        totalReceiptLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalReceiptLabel.setText("Total :");
        menuPanel.add(totalReceiptLabel);
        totalReceiptLabel.setBounds(670, 320, 50, 20);

        totalReceiptValueLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        totalReceiptValueLabel.setForeground(new java.awt.Color(255, 255, 255));
        totalReceiptValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalReceiptValueLabel.setText("000");
        menuPanel.add(totalReceiptValueLabel);
        totalReceiptValueLabel.setBounds(720, 320, 60, 20);

        roomToLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        roomToLabel.setForeground(new java.awt.Color(255, 255, 255));
        roomToLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        roomToLabel.setText("Room number: ");
        menuPanel.add(roomToLabel);
        roomToLabel.setBounds(500, 380, 110, 20);

        verifyBtn.setBackground(new java.awt.Color(0, 255, 0));
        verifyBtn.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        verifyBtn.setForeground(new java.awt.Color(0, 255, 0));
        verifyBtn.setText("Verify");
        verifyBtn.setToolTipText("Verify the order");
        verifyBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        verifyBtn.setOpaque(false);
        verifyBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                verifyBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                verifyBtnMouseExited(evt);
            }
        });
        verifyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyBtnActionPerformed(evt);
            }
        });
        menuPanel.add(verifyBtn);
        verifyBtn.setBounds(690, 370, 70, 40);

        currentLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        currentLabel.setForeground(new java.awt.Color(255, 255, 255));
        currentLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentLabel.setText("Items to verify: ");
        menuPanel.add(currentLabel);
        currentLabel.setBounds(470, 30, 130, 20);

        logoutBtn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kurtisisenad/hotelmanager/images/logout.png"))); // NOI18N
        logoutBtn1.setBorder(null);
        logoutBtn1.setContentAreaFilled(false);
        logoutBtn1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtn1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtn1MouseExited(evt);
            }
        });
        logoutBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutBtn1ActionPerformed(evt);
            }
        });
        menuPanel.add(logoutBtn1);
        logoutBtn1.setBounds(810, 20, 60, 50);

        inputToRoomNumber.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        inputToRoomNumber.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        inputToRoomNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inputToRoomNumber.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        inputToRoomNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputToRoomNumberActionPerformed(evt);
            }
        });
        menuPanel.add(inputToRoomNumber);
        inputToRoomNumber.setBounds(610, 380, 50, 25);

        backgroundMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kurtisisenad/hotelmanager/images/background-log-in.jpeg"))); // NOI18N
        menuPanel.add(backgroundMenu);
        backgroundMenu.setBounds(0, 0, 900, 500);

        hotelTab.addTab("Menu", menuPanel);

        employeePanel.setLayout(null);

        employeeIDLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        employeeIDLabel.setForeground(new java.awt.Color(255, 255, 255));
        employeeIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        employeeIDLabel.setText("Employee ID :");
        employeePanel.add(employeeIDLabel);
        employeeIDLabel.setBounds(50, 30, 100, 20);

        searchBtnEmployee.setBackground(new java.awt.Color(255, 255, 255));
        searchBtnEmployee.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        searchBtnEmployee.setForeground(new java.awt.Color(255, 255, 255));
        searchBtnEmployee.setText("Search");
        searchBtnEmployee.setToolTipText("Search for an employee");
        searchBtnEmployee.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        searchBtnEmployee.setOpaque(false);
        searchBtnEmployee.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchBtnEmployeeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchBtnEmployeeMouseExited(evt);
            }
        });
        searchBtnEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnEmployeeActionPerformed(evt);
            }
        });
        employeePanel.add(searchBtnEmployee);
        searchBtnEmployee.setBounds(220, 30, 70, 25);

        employeeFirstNameLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        employeeFirstNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        employeeFirstNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        employeeFirstNameLabel.setText("First name :");
        employeePanel.add(employeeFirstNameLabel);
        employeeFirstNameLabel.setBounds(110, 110, 100, 20);

        employeeLastNameLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        employeeLastNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        employeeLastNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        employeeLastNameLabel.setText("Last name :");
        employeePanel.add(employeeLastNameLabel);
        employeeLastNameLabel.setBounds(110, 150, 100, 20);

        employeBirthDateLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        employeBirthDateLabel.setForeground(new java.awt.Color(255, 255, 255));
        employeBirthDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        employeBirthDateLabel.setText("Date of birth :");
        employeePanel.add(employeBirthDateLabel);
        employeBirthDateLabel.setBounds(120, 190, 100, 20);

        ageLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        ageLabel.setForeground(new java.awt.Color(255, 255, 255));
        ageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ageLabel.setText("Age :");
        employeePanel.add(ageLabel);
        ageLabel.setBounds(90, 230, 100, 20);

        payLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        payLabel.setForeground(new java.awt.Color(255, 255, 255));
        payLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        payLabel.setText("Pay :");
        employeePanel.add(payLabel);
        payLabel.setBounds(90, 270, 100, 20);

        streetLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        streetLabel.setForeground(new java.awt.Color(255, 255, 255));
        streetLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        streetLabel.setText("Place of living :");
        employeePanel.add(streetLabel);
        streetLabel.setBounds(110, 310, 130, 20);

        phoneNumberLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        phoneNumberLabel.setForeground(new java.awt.Color(255, 255, 255));
        phoneNumberLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        phoneNumberLabel.setText("Phone number :");
        employeePanel.add(phoneNumberLabel);
        phoneNumberLabel.setBounds(110, 350, 130, 20);

        employeeFirstName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        employeeFirstName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        employeeFirstName.setToolTipText("Enter first name");
        employeeFirstName.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        employeePanel.add(employeeFirstName);
        employeeFirstName.setBounds(250, 110, 130, 25);

        employeeID_Label.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        employeeID_Label.setForeground(new java.awt.Color(255, 255, 255));
        employeeID_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        employeeID_Label.setText("ID :");
        employeePanel.add(employeeID_Label);
        employeeID_Label.setBounds(90, 70, 90, 20);

        employeeLastName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        employeeLastName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        employeeLastName.setToolTipText("Enter last name");
        employeeLastName.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        employeePanel.add(employeeLastName);
        employeeLastName.setBounds(250, 150, 130, 25);

        employeeStreet.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        employeeStreet.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        employeeStreet.setToolTipText("Enter place of living");
        employeeStreet.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        employeePanel.add(employeeStreet);
        employeeStreet.setBounds(250, 310, 130, 25);

        employeePhoneNumber.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        employeePhoneNumber.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        employeePhoneNumber.setToolTipText("Enter phone number");
        employeePhoneNumber.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        employeePanel.add(employeePhoneNumber);
        employeePhoneNumber.setBounds(250, 350, 130, 25);

        employeeID.setEditable(false);
        employeeID.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        employeeID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        employeeID.setToolTipText("");
        employeeID.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        employeePanel.add(employeeID);
        employeeID.setBounds(250, 70, 130, 25);

        genderLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        genderLabel.setForeground(new java.awt.Color(255, 255, 255));
        genderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        genderLabel.setText("Gender :");
        employeePanel.add(genderLabel);
        genderLabel.setBounds(120, 390, 60, 20);

        imagePlaceholder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kurtisisenad/hotelmanager/images/default-profile.jpg"))); // NOI18N
        imagePlaceholder.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)));
        employeePanel.add(imagePlaceholder);
        imagePlaceholder.setBounds(580, 30, 180, 200);

        uploadBtn.setBackground(new java.awt.Color(255, 255, 255));
        uploadBtn.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        uploadBtn.setForeground(new java.awt.Color(255, 255, 255));
        uploadBtn.setText("Upload");
        uploadBtn.setToolTipText("Select employee image");
        uploadBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        uploadBtn.setOpaque(false);
        uploadBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                uploadBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                uploadBtnMouseExited(evt);
            }
        });
        uploadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadBtnActionPerformed(evt);
            }
        });
        employeePanel.add(uploadBtn);
        uploadBtn.setBounds(580, 250, 70, 25);

        updateBtn.setBackground(new java.awt.Color(255, 255, 255));
        updateBtn.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        updateBtn.setForeground(new java.awt.Color(255, 255, 255));
        updateBtn.setText("Update");
        updateBtn.setToolTipText("Update employee info");
        updateBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        updateBtn.setOpaque(false);
        updateBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                updateBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                updateBtnMouseExited(evt);
            }
        });
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });
        employeePanel.add(updateBtn);
        updateBtn.setBounds(680, 250, 70, 25);

        deleteBtn.setBackground(new java.awt.Color(255, 0, 0));
        deleteBtn.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        deleteBtn.setForeground(new java.awt.Color(255, 0, 0));
        deleteBtn.setText("DELETE");
        deleteBtn.setToolTipText("Delete the employee");
        deleteBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 2, true));
        deleteBtn.setOpaque(false);
        deleteBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deleteBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deleteBtnMouseExited(evt);
            }
        });
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });
        employeePanel.add(deleteBtn);
        deleteBtn.setBounds(680, 340, 80, 30);

        raseLabel.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        raseLabel.setForeground(new java.awt.Color(255, 255, 255));
        raseLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        raseLabel.setText("Raise :");
        employeePanel.add(raseLabel);
        raseLabel.setBounds(560, 290, 100, 20);

        raiseBtn.setBackground(new java.awt.Color(255, 255, 255));
        raiseBtn.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        raiseBtn.setForeground(new java.awt.Color(255, 255, 255));
        raiseBtn.setText("Give raise");
        raiseBtn.setToolTipText("Give raise to the employee");
        raiseBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        raiseBtn.setOpaque(false);
        raiseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                raiseBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                raiseBtnMouseExited(evt);
            }
        });
        raiseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                raiseBtnActionPerformed(evt);
            }
        });
        employeePanel.add(raiseBtn);
        raiseBtn.setBounds(580, 340, 80, 30);

        percentLabel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        percentLabel.setForeground(new java.awt.Color(255, 255, 255));
        percentLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        percentLabel.setText("%");
        employeePanel.add(percentLabel);
        percentLabel.setBounds(690, 290, 80, 20);

        addEmployeeBtn.setBackground(new java.awt.Color(0, 255, 0));
        addEmployeeBtn.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        addEmployeeBtn.setForeground(new java.awt.Color(0, 255, 0));
        addEmployeeBtn.setText("ADD");
        addEmployeeBtn.setToolTipText("Add new employee");
        addEmployeeBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 255, 0), 2, true));
        addEmployeeBtn.setOpaque(false);
        addEmployeeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addEmployeeBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addEmployeeBtnMouseExited(evt);
            }
        });
        addEmployeeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEmployeeBtnActionPerformed(evt);
            }
        });
        employeePanel.add(addEmployeeBtn);
        addEmployeeBtn.setBounds(690, 400, 60, 30);

        employeeGender.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        employeeGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "male", "female" }));
        employeeGender.setToolTipText("Select a gender");
        employeePanel.add(employeeGender);
        employeeGender.setBounds(250, 390, 130, 25);

        employeeLevel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        employeeLevel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Regular", "Manager" }));
        employeeLevel.setToolTipText("Select a gender");
        employeePanel.add(employeeLevel);
        employeeLevel.setBounds(590, 400, 80, 30);

        logoutBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kurtisisenad/hotelmanager/images/logout.png"))); // NOI18N
        logoutBtn.setBorder(null);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtnMouseExited(evt);
            }
        });
        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutBtnActionPerformed(evt);
            }
        });
        employeePanel.add(logoutBtn);
        logoutBtn.setBounds(810, 20, 60, 50);

        logsBtn.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        logsBtn.setForeground(new java.awt.Color(255, 255, 255));
        logsBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logsBtn.setText("SEE LOGS");
        logsBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logsBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logsBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logsBtnMouseExited(evt);
            }
        });
        employeePanel.add(logsBtn);
        logsBtn.setBounds(310, 30, 100, 20);

        employeeBirthDate.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        employeeBirthDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("yyyy-MM-dd"))));
        employeeBirthDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        employeeBirthDate.setText("yyyy-MM-dd");
        employeeBirthDate.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        employeePanel.add(employeeBirthDate);
        employeeBirthDate.setBounds(250, 190, 130, 25);

        employeeAge.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        employeeAge.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        employeeAge.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        employeeAge.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        employeePanel.add(employeeAge);
        employeeAge.setBounds(250, 230, 130, 25);

        employeePay.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        employeePay.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0.###"))));
        employeePay.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        employeePay.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        employeePanel.add(employeePay);
        employeePay.setBounds(250, 270, 130, 25);

        inputRaise.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        inputRaise.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        inputRaise.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inputRaise.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        employeePanel.add(inputRaise);
        inputRaise.setBounds(640, 290, 70, 25);

        inputEmployeeID.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        inputEmployeeID.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        inputEmployeeID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inputEmployeeID.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        employeePanel.add(inputEmployeeID);
        inputEmployeeID.setBounds(160, 30, 50, 25);

        infoText.setFont(new java.awt.Font("Verdana", 1, 36)); // NOI18N
        infoText.setForeground(new java.awt.Color(255, 255, 255));
        infoText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        employeePanel.add(infoText);
        infoText.setBounds(-6, -6, 900, 480);

        backgroundEmployees.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/kurtisisenad/hotelmanager/images/background-log-in.jpeg"))); // NOI18N
        employeePanel.add(backgroundEmployees);
        backgroundEmployees.setBounds(0, 0, 900, 500);

        hotelTab.addTab("Employees Management", employeePanel);

        getContentPane().add(hotelTab);
        hotelTab.setBounds(0, 0, 890, 500);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void logoutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtnActionPerformed
        logOut();
    }//GEN-LAST:event_logoutBtnActionPerformed

    
    private void logoutBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtnMouseExited
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_logoutBtnMouseExited

    
    private void logoutBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtnMouseEntered
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_logoutBtnMouseEntered

    
    /**
     * Adds new employee to the database.
     * New employees log in credentials
     * are username: 'first_name-ID'
     * password: 'first_name last_name'
     * @param evt 
     */
    private void addEmployeeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEmployeeBtnActionPerformed
        // Getting informatu=ion about new employee
        String firstName = employeeFirstName.getText().trim();
        String lastName = employeeLastName.getText().trim();
        String birthDate = employeeBirthDate.getText().trim();
        String ageText = employeeAge.getText().trim();
        int age = 0;
        String payText = employeePay.getText().trim();
        float pay = 0;
        String street = employeeStreet.getText().trim();
        String phoneNumber = employeePhoneNumber.getText().trim();
        String gender = employeeGender.getSelectedItem().toString();
        byte[] image = imageFile;

        String levelText = employeeLevel.getSelectedItem().toString();
        int level = 1;

        if(levelText.equals("Manager")){
            level = 2;
        }

        int nextID = 0;
        String username = firstName.toLowerCase()+"-"
                +lastName.toLowerCase();
        String password = firstName.toLowerCase() + 
                " " + lastName.toLowerCase();

        if(!canBeAdded()){
            // Message: One of the fields is empty
            JOptionPane.showMessageDialog(null, "No field can be empty!",
                "Invalid input", JOptionPane.ERROR_MESSAGE);
        } else if(image == null){
            // Message: An image must be selected
            JOptionPane.showMessageDialog(null, "You need to select an image!",
                "Invalid input", JOptionPane.ERROR_MESSAGE);
        } else{
            if(!isInt(ageText) || !isFloat(payText)){
                JOptionPane.showMessageDialog(null, "Invalid input!",
                "Invalid input", JOptionPane.ERROR_MESSAGE);                
            } else{
                try{
                    // check if numeric input is empty
                    if(ageText.equals("") || payText.equals("")){
                        JOptionPane.showMessageDialog(null, "Invalid input!",
                        "Invalid input", JOptionPane.ERROR_MESSAGE);    
                        return;                        
                    }
                    age = Integer.parseInt(ageText);
                    pay = Float.parseFloat(payText);
                    
                    // setting up query for the employee
                    // insertion in the database
                    PreparedStatement stmt = con.prepareStatement("INSERT INTO"
                        + " EMPLOYEES (first_name, last_name, birth_date, "
                        + "age, street, phone_number, pay, gender, username,"
                        + " password, level, image) VALUES(?,?,?,?,?,?,?,?,?"
                        + ",?,?,?);",
                        Statement.RETURN_GENERATED_KEYS);

                    stmt.setString(1, firstName);
                    stmt.setString(2, lastName);
                    stmt.setString(3, birthDate);
                    stmt.setInt(4, age);
                    stmt.setString(5, street);
                    stmt.setString(6, phoneNumber);
                    stmt.setFloat(7, pay);
                    stmt.setString(8, gender);
                    stmt.setString(9, username);
                    stmt.setString(10, password);
                    stmt.setInt(11, level);
                    stmt.setBytes(12, image);

                    stmt.execute();
                    
                    ResultSet resSet = stmt.getGeneratedKeys();
                    if(resSet.next()){
                        nextID = resSet.getInt(1);
                    }
                    
                    stmt.close();

                    // Informing that the new employee has 
                    // been added to the database
                    JOptionPane.showMessageDialog(null, "New employee added "
                            + "ID="+nextID, "Add employee", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Show new employee ID
                    employeeID.setText(Integer.toString(nextID));

                    // Memorize the employee add log
                    String log = getLog(7, 0);
                    updateLogs(log);
                } catch(Exception ex){
                    ex.printStackTrace();
                }                    
            }

        }
    }//GEN-LAST:event_addEmployeeBtnActionPerformed

    
    private void addEmployeeBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addEmployeeBtnMouseExited
        addEmployeeBtn.setForeground(Color.green);
        addEmployeeBtn.setBorder(
            new javax.swing.border.LineBorder(Color.green, 1, true)
        );
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_addEmployeeBtnMouseExited

    
    private void addEmployeeBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addEmployeeBtnMouseEntered
        addEmployeeBtn.setForeground(Color.black);
        addEmployeeBtn.setBorder(
            new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_addEmployeeBtnMouseEntered

    
    /**
     * Applies raise to the selected employee
     * @param evt 
     */
    private void raiseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_raiseBtnActionPerformed
        String raiseText = inputRaise.getText().trim();
        
        if(raiseText.isEmpty()){
            // Message: Raise ammount field is empty
            JOptionPane.showMessageDialog(null, 
                    "Raise field can't be empty!",
                    "Invalid input", JOptionPane.ERROR_MESSAGE);
        } else{
            if(employeeID.getText().isEmpty()){
                // Message: No employee is selected
                JOptionPane.showMessageDialog(null, 
                        "No employee selected!",
                        "Invalid input", JOptionPane.ERROR_MESSAGE);
            } else{
                if(canEdit()){
                    if(isFloat(raiseText)){
                        
                        Float raise = Float.parseFloat(raiseText)/100;
                        int id = Integer.parseInt(employeeID.getText());

                        try{
                            // Raise confirmation
                            int choice = JOptionPane.showConfirmDialog(null,
                                "Are you sure you want to give raise to"
                                + " the employee ",
                                "Confirm raise",
                                JOptionPane.YES_NO_OPTION);
                            
                            if(choice==0){
                                Float finalPay = loadedPay*(1+raise);
                                
                                // preparing the statement
                                // for the pay update
                                PreparedStatement stmt = 
                                        con.prepareStatement("UPDATE"
                                        + " EMPLOYEES SET pay=? WHERE id=?");
                                stmt.setFloat(1, finalPay);
                                stmt.setInt(2, id);

                                stmt.executeUpdate();
                                
                                // updating the selected employee
                                // pay(GUI)
                                employeePay.setText(Float.toString(finalPay));
                                loadedPay = finalPay;
                                
                                //closing the statement
                                stmt.close();           
                                //Message: Update confirmed
                                JOptionPane.showMessageDialog(null, "Done",
                                    "",
                                    JOptionPane.INFORMATION_MESSAGE);
                                
                                // Memorize the update log
                                String log = getLog(5, id);
                                updateLogs(log);
                            }
                        } catch(Exception ex){
                            ex.printStackTrace();
                        }
                    } else{
                        // Message: Invalid raise
                        JOptionPane.showMessageDialog(null, 
                                "Raise has to be a number!",
                                "Invalid input", JOptionPane.ERROR_MESSAGE);
                    }
                } else{
                    // Message: No privileges to give specified
                    // employee a raise
                    JOptionPane.showMessageDialog(null, "You can't give a"
                        + " raise to that employee!",
                        "Invalid input", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }//GEN-LAST:event_raiseBtnActionPerformed

    private void raiseBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_raiseBtnMouseExited
        raiseBtn.setForeground(Color.white);
        raiseBtn.setBorder(
            new javax.swing.border.LineBorder(Color.white, 1, true)
        );
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_raiseBtnMouseExited

    private void raiseBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_raiseBtnMouseEntered
        raiseBtn.setForeground(Color.black);
        raiseBtn.setBorder(
            new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_raiseBtnMouseEntered

    
    /**
     * Deletes selected employee from
     * the database
     * @param evt 
     */
    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        if(!canDelete() || !canEdit()){
            // Message: That employee can't be deleted
            // by currently logged in employee
            JOptionPane.showMessageDialog(null, "You are not allowed to"
                + " delete that employee!", "Invalid input",
                JOptionPane.ERROR_MESSAGE);
        } else{
            String idText = employeeID.getText();
            if(idText.isEmpty()){
                // Message: No employee selected
                JOptionPane.showMessageDialog(null, "No employee selected",
                    "Invalid input", JOptionPane.ERROR_MESSAGE);
            } else{ 
                try{
                   // Message: Confirmation of the deletion
                    int choice = JOptionPane.showConfirmDialog(null, "Are you "
                        + "sure you want to update employee information?",
                        "Confirm update", JOptionPane.YES_NO_OPTION);
                    if(choice==0){
                        // Message: Deletion success
                        JOptionPane.showMessageDialog(null, "Employee deleted",
                            "Delete employee", JOptionPane.INFORMATION_MESSAGE);
                        
                        // preparing the statement for employee deletion
                        PreparedStatement stmt = con.prepareStatement("DELETE "
                                + "FROM EMPLOYEES WHERE id=?");
                        int id = Integer.parseInt(employeeID.getText());
                        stmt.setInt(1, id);

                        stmt.execute();
                        stmt.close();

                        // clearing current employee info
                        clearSearchedInfo();  
                        
                        // Memorize deletion log
                        String log = getLog(6, id);
                        updateLogs(log);
                    }
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void deleteBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteBtnMouseExited
        deleteBtn.setForeground(Color.red);
        deleteBtn.setBorder(
            new javax.swing.border.LineBorder(Color.red, 1, true)
        );
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_deleteBtnMouseExited

    private void deleteBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteBtnMouseEntered
        deleteBtn.setForeground(Color.black);
        deleteBtn.setBorder(
            new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_deleteBtnMouseEntered

    
    /**
     * Updates information about
     * currently searched employee
     * @param evt 
     */
    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        // getting the new information about employee
        String firstName = employeeFirstName.getText().trim();
        String lastName = employeeLastName.getText().trim();
        String birthDate = employeeBirthDate.getText().trim();
        String ageText = employeeAge.getText().trim();
        int age = (ageText.isEmpty())? 0:Integer.parseInt(ageText);
        String payText = employeePay.getText().trim();
        float pay = (payText.isEmpty())? 0:Float.parseFloat(payText);
        String street = employeeStreet.getText().trim();
        String phoneNumber = employeePhoneNumber.getText().trim();
        String gender = employeeGender.getSelectedItem().toString();
        byte[] image = imageFile;

        if(firstName.isEmpty() || lastName.isEmpty() || payText.isEmpty() ||
            street.isEmpty() || phoneNumber.isEmpty() || gender.isEmpty()
            || birthDate.isEmpty() || ageText.isEmpty()){
            // Message: Some field is empty
            JOptionPane.showMessageDialog(null, "No field can be empty!",
                "Invalid input", JOptionPane.ERROR_MESSAGE);
        } else if(image == null){
            // Message: Image is not selected
            JOptionPane.showMessageDialog(null, "You need to select an image!",
                "Invalid input", JOptionPane.ERROR_MESSAGE);
        } else{
            if(!canEdit()){
                // Message: Currently logged employee can't
                // perform wanted information update
                JOptionPane.showMessageDialog(null, "You are not allowed to"
                    + " edit this!", "Invalid input",
                    JOptionPane.ERROR_MESSAGE);
            } else{
                if(employeeID.getText().isEmpty()){
                    // Message: An employee is not selected
                    JOptionPane.showMessageDialog(null, "No employee selected!",
                        "Invalid input", JOptionPane.ERROR_MESSAGE);
                } else{
                    try{
                        // Message: Update confirmation
                        int choice = JOptionPane.showConfirmDialog(null, 
                                "Are you sure you want to update "
                                + "employee information?",
                                "Confirm update", JOptionPane.YES_NO_OPTION);
                        if(choice==0){
                            JOptionPane.showMessageDialog(null, "Employee "
                                + "information updated!", "",
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // preparing the statement for 
                            // information update
                            PreparedStatement stmt = con.prepareStatement(
                                    "UPDATE EMPLOYEES SET first_name=?, "
                                    + "last_name=?, pay=?, street=?, "
                                    + "phone_number=?, gender=?, "
                                    + "image=?, age=?, birth_date=? "
                                    + "WHERE id=?");
                            stmt.setString(1, firstName);
                            stmt.setString(2, lastName);
                            stmt.setFloat(3, pay);
                            stmt.setString(4, street);
                            stmt.setString(5, phoneNumber);
                            stmt.setString(6, gender);
                            stmt.setBytes(7, image);
                            stmt.setInt(8, age);
                            stmt.setString(9, birthDate);

                            int id = Integer.parseInt(employeeID.getText());
                            stmt.setInt(10, id);

                            stmt.execute();
                            stmt.close();         
                            
                            // Memorize the update log
                            String log = getLog(4, id);
                            updateLogs(log);
                        }
                    } catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }

    }//GEN-LAST:event_updateBtnActionPerformed

    private void updateBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateBtnMouseExited
        updateBtn.setForeground(Color.white);
        updateBtn.setBorder(
            new javax.swing.border.LineBorder(Color.white, 1, true)
        );
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_updateBtnMouseExited

    private void updateBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateBtnMouseEntered
        updateBtn.setForeground(Color.black);
        updateBtn.setBorder(
            new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_updateBtnMouseEntered

    
    /**
     * Uploads the selected image to the
     * employee profile
     * @param evt 
     */
    private void uploadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadBtnActionPerformed
        try{
            // opens a file choosing window
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(null);

            // getting the selected file
            File file = fileChooser.getSelectedFile();
            
            fileName = file.getAbsolutePath();
            
            // forming an image icon based on 
            // image placeholder size
            ImageIcon imageIcon = 
                    new ImageIcon(new ImageIcon(fileName).getImage().
                    getScaledInstance(imagePlaceholder.getWidth(),
                    imagePlaceholder.getHeight(), Image.SCALE_SMOOTH));
            
            // updating the image
            imagePlaceholder.setIcon(imageIcon);

            // setting up streams that memorize loaded
            // image as array of bytes, in a form
            // that will be later sent to the database
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream bOStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];

            for(int readNum; (readNum=inputStream.read(buff))!=-1;){
                bOStream.write(buff, 0, readNum);
            }
            imageFile = bOStream.toByteArray();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }//GEN-LAST:event_uploadBtnActionPerformed

    private void uploadBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_uploadBtnMouseExited
        uploadBtn.setForeground(Color.white);
        uploadBtn.setBorder(
            new javax.swing.border.LineBorder(Color.white, 1, true)
        );
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_uploadBtnMouseExited

    private void uploadBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_uploadBtnMouseEntered
        uploadBtn.setForeground(Color.black);
        uploadBtn.setBorder(
            new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_uploadBtnMouseEntered

    
    /**
     * Searches the database for the employee
     * with specified ID
     * @param evt 
     */
    private void searchBtnEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnEmployeeActionPerformed
        String idToSearch = inputEmployeeID.getText().trim();
        
        if(idToSearch.isEmpty()){
            // Message: ID field is empty
            JOptionPane.showMessageDialog(null, "ID field empty",
                "Invalid input", JOptionPane.ERROR_MESSAGE);
        } else{
            if(isInt(idToSearch)){
                int id = (idToSearch.isEmpty())? 
                        0:Integer.parseInt(idToSearch);
                if(id<=0){
                    JOptionPane.showMessageDialog(null, "Invalid employee ID",
                        "Invalid input", JOptionPane.ERROR_MESSAGE);   
                    return;
                }
                
                try{
                    // prparing the statement for employee search
                    Statement stmt = con.createStatement();
                    String query = "SELECT * FROM EMPLOYEES WHERE id="+id;
                    stmt.executeQuery(query);

                    ResultSet resSet = stmt.getResultSet();
                    
                    // getting the results, if they exist
                    if(resSet.next()){
                        // getting information about employee
                        String firstName = resSet.getString("first_name");
                        String lastName = resSet.getString("last_name");
                        String birthDate = resSet.getString("birth_date");
                        int age = resSet.getInt("age");
                        float pay = loadedPay = resSet.getFloat("pay");
                        String street = resSet.getString("street");
                        String phoneNumber = resSet.getString("phone_number");
                        String gender = resSet.getString("gender");
                        byte[] image = resSet.getBytes("image");

                        // memorizing the level of the 
                        // searched employee
                        loadedLevel = resSet.getInt("level");

                        // updating the GUI so that it shows
                        // information about searched employee
                        employeeID.setText(Integer.toString(id));
                        employeeFirstName.setText(firstName);
                        employeeLastName.setText(lastName);
                        employeeBirthDate.setText(birthDate.substring(0,10));
                        employeeAge.setText(Integer.toString(age));
                        employeePay.setText(Float.toString(pay));
                        employeeStreet.setText(street);
                        employeePhoneNumber.setText(phoneNumber);
                        employeeGender.setSelectedItem(gender);

                        // showing the image of the
                        // searched employee
                        ImageIcon imageIcon = 
                                new ImageIcon(new ImageIcon(image).getImage().
                                getScaledInstance(imagePlaceholder.getWidth(),
                                imagePlaceholder.getHeight(),Image.SCALE_SMOOTH));
                        imagePlaceholder.setIcon(imageIcon);
                        imageFile = image;
                    } else{
                        // Message: Searched employee doesn't exist
                        JOptionPane.showMessageDialog(null, "Employee with id="
                            +idToSearch+" doesn't exist.", "Invalid input",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                    stmt.close();
                } catch(Exception ex){
                    ex.printStackTrace();
                }                
            } else{
                // Message: Invalid employee ID
                JOptionPane.showMessageDialog(null, "Invalid employee id!",
                    "Invalid input", JOptionPane.INFORMATION_MESSAGE);
            }  
        }
    }//GEN-LAST:event_searchBtnEmployeeActionPerformed

    private void searchBtnEmployeeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBtnEmployeeMouseExited
        searchBtnEmployee.setForeground(Color.white);
        searchBtnEmployee.setBorder(
            new javax.swing.border.LineBorder(Color.white, 1, true)
        );
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_searchBtnEmployeeMouseExited

    private void searchBtnEmployeeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBtnEmployeeMouseEntered
        searchBtnEmployee.setForeground(Color.black);
        searchBtnEmployee.setBorder(
            new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_searchBtnEmployeeMouseEntered

    private void logoutBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtn1ActionPerformed
        logOut();
    }//GEN-LAST:event_logoutBtn1ActionPerformed

    private void logoutBtn1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtn1MouseExited
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_logoutBtn1MouseExited

    private void logoutBtn1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtn1MouseEntered
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_logoutBtn1MouseEntered

    
    /**
     * Confirms items order from a specific room
     * @param evt 
     */
    private void verifyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verifyBtnActionPerformed
        String roomText = inputToRoomNumber.getText().trim();
        if(roomText.isEmpty()){
            // Message: No room is selected
            JOptionPane.showMessageDialog(null, "No room selected!",
                "Invalid input", JOptionPane.ERROR_MESSAGE);
        } else{
            if(isInt(roomText)){
                int roomNumber= (roomText.isEmpty())? 
                        0:Integer.parseInt(roomText);
                
                if(roomNumber<=0){
                    JOptionPane.showMessageDialog(null, "Invalid room number!",
                        "Invalid input", JOptionPane.ERROR_MESSAGE);   
                    return;
                }
                if(!roomAvailable(roomNumber)){
                    updateReceipt(roomNumber);
                    
                    // Receipt update confirmation
                    JOptionPane.showMessageDialog(null, "Receipt for room "
                        + roomText + " updated",
                        "Receipt update", JOptionPane.INFORMATION_MESSAGE);
                    
                    // clearing the current receipt GUI
                    DefaultTableModel tableModel = 
                            (DefaultTableModel) receiptTable.getModel();
                    tableModel.setRowCount(0);
                    amount.setValue(0);
                    inputToRoomNumber.setText("");
                    
                    // memorize receipt update log
                    String log = getLog(3, Integer.parseInt(roomText));
                    updateLogs(log);
                } else{
                    // Message: Tried to update receipt for
                    // room that is not occupied
                    JOptionPane.showMessageDialog(null, "Room is not occupied!"
                        , "Invalid input", JOptionPane.ERROR_MESSAGE);
                }                
            } else{
                // Message: Invalid room number
                JOptionPane.showMessageDialog(null, "Invalid room number!"
                        , "Invalid input", JOptionPane.ERROR_MESSAGE);                
            }
        }
    }//GEN-LAST:event_verifyBtnActionPerformed

    private void verifyBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_verifyBtnMouseExited
        verifyBtn.setForeground(Color.green);
        verifyBtn.setBorder(
            new javax.swing.border.LineBorder(Color.green, 1, true)
        );
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_verifyBtnMouseExited

    private void verifyBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_verifyBtnMouseEntered
        verifyBtn.setForeground(Color.black);
        verifyBtn.setBorder(
            new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_verifyBtnMouseEntered

    
    /**
     * Adds selected item to the current receipt
     * @param evt 
     */
    private void addItemBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemBtnActionPerformed
        DefaultTableModel tableModel = 
                (DefaultTableModel) receiptTable.getModel();

        // getting the selected item
        int selectedRow = itemTable.getSelectedRow();

        // checks if any item is selected
        if(selectedRow>=0){
            String available = (String) itemTable.getValueAt(selectedRow, 2);
            boolean isAvailable = (available.equals("Yes"))? true:false;
            
            // checking if selected item is available
            if(isAvailable){
                // getting information
                // about desired item
                String desiredItem = (String) itemTable.
                getValueAt(selectedRow, 0);
                float desiredPrice = (float) itemTable.
                getValueAt(selectedRow, 1);
                int itemAmount = (int) amount.getValue();

                if(itemAmount>0){
                    // calculating the price of selected item
                    float addPrice = desiredPrice*itemAmount;

                    // adding the item to the current receipt

                    calculateTotalToAdd(desiredItem, itemAmount, addPrice);
                } else{
                    // Message: invalid item ammount
                    JOptionPane.showMessageDialog(null, 
                            "Invalid item ammount!", "Invalid input", 
                            JOptionPane.ERROR_MESSAGE);
                }

            } else{
                // Message: Item is not available
                JOptionPane.showMessageDialog(null, 
                        "That item is not available!",
                        "Invalid input", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_addItemBtnActionPerformed

    private void addItemBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addItemBtnMouseExited
        addItemBtn.setForeground(Color.green);
        addItemBtn.setBorder(
            new javax.swing.border.LineBorder(Color.green, 1, true)
        );
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_addItemBtnMouseExited

    private void addItemBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addItemBtnMouseEntered
        addItemBtn.setForeground(Color.black);
        addItemBtn.setBorder(
            new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_addItemBtnMouseEntered

    
    /**
     * Checks out guest from selected room
     * @param evt 
     */
    private void checkOutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkOutBtnActionPerformed
        String numberText = inputRoomNumber.getText().trim();
        if(room.getText().isEmpty()){
            // Message: Room is not selected
            JOptionPane.showMessageDialog(null, "No room selected!",
                "Invalid input", JOptionPane.ERROR_MESSAGE);
        } else{
            if(isInt(numberText)){
                // Check Out confirmation
                int choice= JOptionPane.showConfirmDialog(null,
                    "Do you want to proceed with checkout?",
                    "Checkout", JOptionPane.YES_NO_OPTION);

                if(choice==0){
                    int room = Integer.parseInt(numberText);
                    float amount = Float.parseFloat(totalLabelNum.getText());
                    String guestName = firstName.getText() +
                    " " + lastName.getText();
                    
                    // memorize chek in date
                    String checkInDate = checkIn.getText().trim();
                    // updating receipt display
                    showReceipt(room, checkInDate);
                    
                    // adding money to the cash register
                    addToCashRegister(room, guestName, amount);
                    // cleaning up
                    cleanUpAfterCheckOut(room);
                    
                    // memorize the check out log
                    String log = getLog(2, room);
                    updateLogs(log);
                }
            } else{
                // Message: Invalid room number
                JOptionPane.showMessageDialog(null, "Invalid room number!",
                    "Invalid input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_checkOutBtnActionPerformed

    private void checkOutBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkOutBtnMouseExited
        checkOutBtn.setForeground(Color.red);
        checkOutBtn.setBorder(
            new javax.swing.border.LineBorder(Color.red, 1, true)
        );
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_checkOutBtnMouseExited

    private void checkOutBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkOutBtnMouseEntered
        checkOutBtn.setForeground(Color.black);
        checkOutBtn.setBorder(
            new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_checkOutBtnMouseEntered

    
    /**
     * Adds new guest to the database
     * @param evt 
     */
    private void addGuestBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addGuestBtnActionPerformed
        DefaultTableModel tableModel = 
                (DefaultTableModel) roomTable.getModel();
        int selectedRow = roomTable.getSelectedRow();

        // checks if room is selected
        if(selectedRow>=0){
            int desiredRoom = (int) roomTable.getValueAt(selectedRow, 0);
            float price = (float) roomTable.getValueAt(selectedRow, 5);

            // getting the info about the guest
            String guestFirstName = firstNameCheckIn.getText().trim();
            String guestLastName = lastNameCheckIn.getText().trim();

            // getting the chek in date
            SimpleDateFormat dFormat = new
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dFormat.format(new Date());toString();

            if(guestFirstName.isEmpty() || guestLastName.isEmpty()){
                // Message: Guest information is missing
                JOptionPane.showMessageDialog(null, "Guest info missing!",
                    "Invalid input", JOptionPane.ERROR_MESSAGE);
            } else{
                try{
                    if(roomAvailable(desiredRoom)){
                        // preparing the statement for
                        // inserting gues to the database
                        PreparedStatement stmt = con.prepareStatement(
                                "INSERT INTO GUESTS " + "VALUES (?,?,?,?);");
                        stmt.setInt(1, desiredRoom);
                        stmt.setString(2, guestFirstName);
                        stmt.setString(3, guestLastName);
                        stmt.setString(4, currentDate);

                        stmt.execute();
                        stmt.clearParameters();
                        stmt.close();

                        // preparing the statement for updating the
                        // selected room accupation status
                        stmt = con.prepareStatement("UPDATE ACCOMMODATION SET "
                            + "occupied =1 WHERE room_number = ?");
                        stmt.setInt(1, desiredRoom);

                        stmt.execute();
                        stmt.close();

                        updateRoomTable();
                        saveInitialReceipt(desiredRoom, price);
                        
                        // memorize the guest add log
                        String log = getLog(1, desiredRoom);
                        updateLogs(log);
                        
                        // Message: Guest add confirmation
                        JOptionPane.showMessageDialog(null, "Guest successfully "
                                + "added", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else{
                        JOptionPane.showMessageDialog(null, "That room is occupied!"
                            ,"Invalid input",JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch(Exception ex){
                    ex.printStackTrace();
                }                
            }
        } else{
            // Message: Room is not selected
            JOptionPane.showMessageDialog(null, "No room selected!"
                ,"Invalid input",JOptionPane.ERROR_MESSAGE);            
        }
    }//GEN-LAST:event_addGuestBtnActionPerformed

    private void addGuestBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addGuestBtnMouseExited
        addGuestBtn.setForeground(Color.white);
        addGuestBtn.setBorder(
            new javax.swing.border.LineBorder(Color.white, 1, true)
        );
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_addGuestBtnMouseExited

    private void addGuestBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addGuestBtnMouseEntered
        addGuestBtn.setForeground(Color.black);
        addGuestBtn.setBorder(
            new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_addGuestBtnMouseEntered

    
    /**
     * Searches the database for information
     * about the guest in the specified room
     * @param evt 
     */
    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        String input = inputRoomNumber.getText().trim();
        if(input.isEmpty()){
            // Message: Room is not selected
            JOptionPane.showMessageDialog(null, "Room field is empty!",
                "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
        else{
            if(isInt(input)){
                int roomToSearch = Integer.parseInt(input);
                if(roomToSearch<=0){
                    JOptionPane.showMessageDialog(null, "Invalid room number!",
                        "Invalid input", JOptionPane.ERROR_MESSAGE);  
                    return;
                }
                try{
                    // prepares statement for database search
                    PreparedStatement stmt = con.prepareStatement(
                            "SELECT * FROM GUESTS WHERE room=?");
                    stmt.setInt(1,roomToSearch);
                    stmt.execute();

                    ResultSet resSet = stmt.getResultSet();
                    
                    // getting the results, if they exist
                    if(resSet.next()){
                        // getting the guest info
                        String guestFirstName = resSet.getString("first_name");
                        String guestLastName = resSet.getString("last_name");
                        int roomNumber = roomToSearch;
                        String checkInDate = resSet.getString("check_in");
                        System.out.println(checkInDate);

                        // updating the GUI with guest info
                        firstName.setText(guestFirstName);
                        lastName.setText(guestLastName);
                        room.setText(Integer.toString(roomNumber));
                        checkIn.setText(checkInDate);

                        showReceipt(roomNumber, checkInDate);
                    } else{
                        // Message: Specified room is not occupied
                        JOptionPane.showMessageDialog(null, 
                                "No guests in that room.",
                                "Invalid input", 
                                JOptionPane.INFORMATION_MESSAGE);
                        inputRoomNumber.setText("");
                    }

                    stmt.close();
                } catch(Exception ex){
                    ex.printStackTrace();
                }
            } else{
                // Message: Invalid room number
                JOptionPane.showMessageDialog(null, "Invalid room number",
                    "Invalid input", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_searchBtnActionPerformed

    private void searchBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBtnMouseExited
        searchBtn.setForeground(Color.white);
        searchBtn.setBorder(
            new javax.swing.border.LineBorder(Color.white, 1, true)
        );
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_searchBtnMouseExited

    private void searchBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBtnMouseEntered
        searchBtn.setForeground(Color.black);
        searchBtn.setBorder(
            new javax.swing.border.LineBorder(Color.black, 1, true)
        );
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_searchBtnMouseEntered

    private void logoutBtn2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtn2MouseEntered
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_logoutBtn2MouseEntered

    private void logoutBtn2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtn2MouseExited
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_logoutBtn2MouseExited

    private void logoutBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtn2ActionPerformed
        logOut();
    }//GEN-LAST:event_logoutBtn2ActionPerformed

    private void logsBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logsBtnMouseEntered
        logsBtn.setForeground(Color.black);
        setCursor(Cursor.HAND_CURSOR);
    }//GEN-LAST:event_logsBtnMouseEntered

    private void logsBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logsBtnMouseExited
        logsBtn.setForeground(Color.white);
        setCursor(Cursor.DEFAULT_CURSOR);
    }//GEN-LAST:event_logsBtnMouseExited

    private void logsBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logsBtnMouseClicked
        new LogsForm().setVisible(true);
    }//GEN-LAST:event_logsBtnMouseClicked

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        updateLogs(getLog(8, 0));
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        updateLogs(getLog(8, 0));
    }//GEN-LAST:event_formWindowClosing

    private void inputToRoomNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputToRoomNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputToRoomNumberActionPerformed

    
    public static void main(String args[]) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HotelManager().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEmployeeBtn;
    private javax.swing.JButton addGuestBtn;
    private javax.swing.JButton addItemBtn;
    private javax.swing.JLabel ageLabel;
    private javax.swing.JSpinner amount;
    private javax.swing.JLabel backgroundBooking;
    private javax.swing.JLabel backgroundEmployees;
    private javax.swing.JLabel backgroundMenu;
    private javax.swing.JLabel bookingLabel;
    private javax.swing.JPanel bookingPanel;
    private javax.swing.JLabel checkIn;
    private javax.swing.JLabel checkInLabel;
    private javax.swing.JButton checkOutBtn;
    private javax.swing.JLabel currentLabel;
    private javax.swing.JLabel currentReceiptLabel;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JLabel employeBirthDateLabel;
    private javax.swing.JFormattedTextField employeeAge;
    private javax.swing.JFormattedTextField employeeBirthDate;
    private javax.swing.JTextField employeeFirstName;
    private javax.swing.JLabel employeeFirstNameLabel;
    private javax.swing.JComboBox<String> employeeGender;
    private javax.swing.JTextField employeeID;
    private javax.swing.JLabel employeeIDLabel;
    private javax.swing.JLabel employeeID_Label;
    private javax.swing.JTextField employeeLastName;
    private javax.swing.JLabel employeeLastNameLabel;
    private javax.swing.JComboBox<String> employeeLevel;
    private javax.swing.JPanel employeePanel;
    private javax.swing.JFormattedTextField employeePay;
    private javax.swing.JTextField employeePhoneNumber;
    private javax.swing.JTextField employeeStreet;
    private javax.swing.JLabel firstName;
    private javax.swing.JTextField firstNameCheckIn;
    private javax.swing.JLabel firstNameCheckInLabel;
    private javax.swing.JLabel firstNameLabel;
    private javax.swing.JLabel genderLabel;
    private javax.swing.JTabbedPane hotelTab;
    private javax.swing.JLabel imagePlaceholder;
    private javax.swing.JLabel infoText;
    private javax.swing.JFormattedTextField inputEmployeeID;
    private javax.swing.JFormattedTextField inputRaise;
    private javax.swing.JFormattedTextField inputRoomNumber;
    private javax.swing.JFormattedTextField inputToRoomNumber;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel itemsLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lastName;
    private javax.swing.JTextField lastNameCheckIn;
    private javax.swing.JLabel lastNameCheckInLabel;
    private javax.swing.JLabel lastNameLabel;
    private javax.swing.JButton logoutBtn;
    private javax.swing.JButton logoutBtn1;
    private javax.swing.JButton logoutBtn2;
    private javax.swing.JLabel logsBtn;
    private javax.swing.JPanel menuPanel;
    private javax.swing.JLabel payLabel;
    private javax.swing.JLabel percentLabel;
    private javax.swing.JLabel phoneNumberLabel;
    private javax.swing.JButton raiseBtn;
    private javax.swing.JLabel raseLabel;
    private javax.swing.JTextArea receiptArea;
    private javax.swing.JTable receiptTable;
    private javax.swing.JScrollPane receiptTextArea;
    private javax.swing.JLabel room;
    private javax.swing.JLabel roomLabel;
    private javax.swing.JLabel roomListLabel;
    private javax.swing.JLabel roomNumLabel;
    private javax.swing.JTable roomTable;
    private javax.swing.JLabel roomToLabel;
    private javax.swing.JButton searchBtn;
    private javax.swing.JButton searchBtnEmployee;
    private javax.swing.JLabel streetLabel;
    private javax.swing.JLabel totalCheckLabel;
    private javax.swing.JLabel totalLabelNum;
    private javax.swing.JLabel totalReceiptLabel;
    private javax.swing.JLabel totalReceiptValueLabel;
    private javax.swing.JButton updateBtn;
    private javax.swing.JButton uploadBtn;
    private javax.swing.JButton verifyBtn;
    // End of variables declaration//GEN-END:variables
}