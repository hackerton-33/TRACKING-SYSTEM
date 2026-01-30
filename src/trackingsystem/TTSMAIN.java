
package trackingsystem;


import com.toedter.calendar.JTextFieldDateEditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

public class TTSMAIN extends javax.swing.JFrame {
    
    String day, month, mm, time, date, s, qq;
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    
    public TTSMAIN() {
    initComponents();
    startDateTimeUpdater();
    DATECHOOSER.setDate(new Date());
    JTextFieldDateEditor editor = (JTextFieldDateEditor) DATECHOOSER.getDateEditor();
    editor.setEditable(true); // Make the JDateChooser editable
    DATECHOOSER.setSelectableDateRange(new Date(), new Date());
    conn = dbconnection.connect();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        clear_currentuser();
    }));
    
    // Add PropertyChangeListener to the calendar to detect date selection changes
    calendar.addPropertyChangeListener(new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("calendar".equals(evt.getPropertyName())) {
                // Get the selected date from the calendar
                Date selectedDate = calendar.getDate();
                // Fetch and display users for the selected date
                fetchAndDisplayUsers(selectedDate);
            }
        }
    });
}    
    public void set_dashboard(String tableName) {
    try {
        String query = "SELECT COUNT(username) AS user_count FROM " + tableName;
        pst = conn.prepareStatement(query);
        rs = pst.executeQuery();

        if (rs.next()) {
            int userCount = rs.getInt("user_count");
            if (tableName.equals("userss")) {
                total_users.setText(String.valueOf(userCount));
            } else if (tableName.equals("currentuser")) {
                active_users.setText(String.valueOf(userCount));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public void set_pcdash() {
    int itemCount = pcnocb.getItemCount();
    pcavail.setText("" + itemCount);
    
}

    public void set_dashboardd() {
    try {
        // Initialize total time variables for hours and minutes
        int totalHours = 0;
        int totalMinutes = 0;

        // Prepare and execute query to fetch time data from the "userss" table
        pst = conn.prepareStatement("SELECT time FROM userss");
        rs = pst.executeQuery();

        // Iterate through the result set
        while (rs.next()) {
            String time = rs.getString("time");
            // Check if the time string is not empty
            if (time != null && !time.isEmpty()) {
                // Extract numeric part of the time (e.g., "2HRS" -> 2, "5MNS" -> 5)
                String numericPart = time.replaceAll("[^0-9]", "");
                if (!numericPart.isEmpty()) { // Check if numeric part is not empty
                    int timeValue = Integer.parseInt(numericPart);
                    if (time.contains("HRS") || time.contains("HR")) {
                        totalHours += timeValue;
                    } else if (time.contains("MNS")) {
                        totalMinutes += timeValue;
                    }
                }
            }
        }

        // Convert total minutes to hours and remaining minutes
        totalHours += totalMinutes / 60;
        totalMinutes = totalMinutes % 60;

        // Set the total time on the UI
        String totalTimeStr = totalHours + " HRS";
        if (totalMinutes > 0) {
            totalTimeStr += " & " + totalMinutes + " MINS";
        }
        total_time.setText(totalTimeStr);
    } catch (SQLException e) {
        System.out.println("SQL Exception: " + e.getMessage());
        e.printStackTrace();
    } catch (NumberFormatException e) {
        System.out.println("NumberFormatException: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        System.out.println("Exception: " + e.getMessage());
        e.printStackTrace();
    } finally {
        // Close resources
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


    public void set_dashboarddd() {
    try {
        // Initialize total daily time variables for hours and minutes
        int totalDailyHours = 0;
        int totalDailyMinutes = 0;

        // Get the current date
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

        // Prepare and execute query to fetch time data for the current date
        String query = "SELECT time FROM userss WHERE date = ?";
        pst = conn.prepareStatement(query);
        pst.setDate(1, currentDate);
        rs = pst.executeQuery();

        // Iterate through the result set
        while (rs.next()) {
            String time = rs.getString("time");
            // Check if the time string is not empty
            if (time != null && !time.isEmpty()) {
                // Extract numeric part of the time (e.g., "2HRS" -> 2, "5MNS" -> 5)
                String numericPart = time.replaceAll("[^0-9]", "");
                if (!numericPart.isEmpty()) { // Check if numeric part is not empty
                    int timeValue = Integer.parseInt(numericPart);
                    if (time.contains("HRS") || time.contains("HR")) {
                        totalDailyHours += timeValue;
                    } else if (time.contains("MNS")) {
                        totalDailyMinutes += timeValue;
                    }
                }
            }
        }

        // Convert total minutes to hours and remaining minutes
        totalDailyHours += totalDailyMinutes / 60;
        totalDailyMinutes = totalDailyMinutes % 60;

        // Set the total daily time on the UI
        String totalDailyTimeStr = totalDailyHours + " HRS";
        if (totalDailyMinutes > 0) {
            totalDailyTimeStr += " & " + totalDailyMinutes + " MINS";
        }
        daily_time.setText(totalDailyTimeStr);
    } catch (SQLException e) {
        System.out.println("SQL Exception: " + e.getMessage());
        e.printStackTrace();
    } catch (NumberFormatException e) {
        System.out.println("NumberFormatException: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        System.out.println("Exception: " + e.getMessage());
        e.printStackTrace();
    } finally {
        // Close resources
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


    
    public void set_users() {
    try {
        // Retrieve users from users table
        pst = conn.prepareStatement("SELECT username AS 'Username', pcno AS 'PC No.', time AS 'Time' FROM userss");
        rs = pst.executeQuery();
        
        // Set the model for user_table with the data retrieved from users table
        user_table.setModel(DbUtils.resultSetToTableModel(rs)); 
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error setting users: " + e.getMessage());
    }
}

// Method to fetch all users from currentuser table and display them in currentTB
    public void set_current_users() {
    try {
        // Retrieve users from currentuser table
        pst = conn.prepareStatement("SELECT username AS 'Username', pcno AS 'PC No.', time AS 'Time' FROM currentuser");
        rs = pst.executeQuery();
        
        // Set the model for currentTB with the data retrieved from currentuser table
        currentTB.setModel(DbUtils.resultSetToTableModel(rs));
        
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error setting current users: " + e.getMessage());
    }
}
    public void clear_currentuser() {
    try {
        // Execute SQL to delete all rows from the currentuser table
        pst = conn.prepareStatement("DELETE FROM currentuser");
        pst.executeUpdate();
        System.out.println("Current users cleared.");
        
        // After deletion, update the active users count
        set_dashboard("currentuser");
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error clearing current users: " + e.getMessage());
    }
}
    public void set_income_dashboard() {
    try {
        // Initialize total income variable
        int totalIncome = 0;

        // Prepare and execute query to fetch time data from the "userss" table
        pst = conn.prepareStatement("SELECT time FROM userss");
        rs = pst.executeQuery();

        // Iterate through the result set
        while (rs.next()) {
            String time = rs.getString("time");
            // Check if the time string is not empty
            if (!time.isEmpty()) {
                // Extract numeric part of the time (e.g., "2HRS" -> 2, "5MNS" -> 5)
                String numericPart = time.replaceAll("[^0-9]", "");
                if (!numericPart.isEmpty()) { // Check if numeric part is not empty
                    int timeValue = Integer.parseInt(numericPart);
                    int fee = 0;

                    // Calculate fee based on time
                    if (time.contains("HRS")) {
                        // Hours based fee
                        if (timeValue == 1) {
                            fee = 15;
                        } else if (timeValue == 2) {
                            fee = 25;
                        } else if (timeValue == 3) {
                            fee = 35;
                        } else if (timeValue == 4) {
                            fee = 45;
                        } else if (timeValue == 5) {
                            fee = 55;
                        } else if (timeValue >= 6) {
                            fee = 60;
                        }
                    } else if (time.contains("MNS")) {
                        // Minutes based fee
                        if (timeValue == 5) {
                            fee = 5;
                        } } else if (time.contains("HR")) {
                        // Minutes based fee
                        if (timeValue == 1) {
                            fee = 15;
                        }
                    }

                    // Add fee to total income
                    totalIncome += fee;
                }
            }
        }

        // Set the total income on the UI
        income.setText("PHP " + totalIncome + ".00\n");
    } catch (SQLException e) {
        System.out.println("SQL Exception: " + e.getMessage());
        e.printStackTrace();
    } catch (NumberFormatException e) {
        System.out.println("NumberFormatException: " + e.getMessage());
        e.printStackTrace();
    } catch (Exception e) {
        System.out.println("Exception: " + e.getMessage());
        e.printStackTrace();
    } finally {
        // Close resources
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel19 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        pop = new javax.swing.JPopupMenu();
        END = new javax.swing.JMenuItem();
        EDIT = new javax.swing.JMenuItem();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        dt = new javax.swing.JLabel();
        dt1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        DASH = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        total_users = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        active_users = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        daily_time = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        pcavail = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        wawa = new javax.swing.JLabel();
        total_time = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        wawa1 = new javax.swing.JLabel();
        income = new javax.swing.JLabel();
        ADD = new javax.swing.JPanel();
        nametf = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        timecb = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        pcnocb = new javax.swing.JComboBox<>();
        jButton7 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        currentTB = new javax.swing.JTable();
        jLabel24 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        user_table = new javax.swing.JTable();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        DATECHOOSER = new com.toedter.calendar.JDateChooser();
        USERS = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        dailyuser = new javax.swing.JTable();
        calendar = new com.toedter.calendar.JCalendar();
        print = new javax.swing.JButton();
        calculate = new javax.swing.JButton();
        ABOUT = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        OUT = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Barangay Record Management System");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(834, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        jLabel17.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("DASHBOARD");
        jLabel17.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel17MouseClicked(evt);
            }
        });

        END.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        END.setText("END USER");
        END.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ENDActionPerformed(evt);
            }
        });
        pop.add(END);

        EDIT.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        EDIT.setText("UPDATE");
        EDIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EDITActionPerformed(evt);
            }
        });
        pop.add(EDIT);

        jScrollPane2.setViewportView(jTextPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(0, 51, 102));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 60, 50));

        jLabel13.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 60, 50));

        jLabel14.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 60, 50));

        jLabel16.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 380, 60, 50));

        jLabel19.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 60, 50));

        jLabel20.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 330, 60, 50));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Purple_Pixel_Technology_Instagram_Story_20240520_000153_0000[1]-modified-128x128.png"))); // NOI18N
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 140, 140));

        jLabel21.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 60, 50));

        jLabel22.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 60, 50));

        dt.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        dt.setForeground(new java.awt.Color(255, 255, 255));
        dt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dt.setText("Time");
        jPanel3.add(dt, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 610, 219, -1));

        dt1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        dt1.setForeground(new java.awt.Color(255, 255, 255));
        dt1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dt1.setText("Date");
        jPanel3.add(dt1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 590, 219, -1));

        jButton1.setBackground(new java.awt.Color(0, 51, 102));
        jButton1.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("LOGOUT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 470, 170, 50));

        jButton2.setBackground(new java.awt.Color(0, 51, 102));
        jButton2.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("DASHBOARD");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, 170, 50));

        jButton3.setBackground(new java.awt.Color(0, 51, 102));
        jButton3.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("ADD USERS");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, 170, 50));

        jButton4.setBackground(new java.awt.Color(0, 51, 102));
        jButton4.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("TRACKING");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, 170, 50));

        jButton6.setBackground(new java.awt.Color(0, 51, 102));
        jButton6.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("ABOUT");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 400, 170, 50));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, -1, 210, 630));

        jPanel4.setBackground(new java.awt.Color(0, 51, 102));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("DAP GAMING TRANSACTION TRACKING SYSTEM");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(325, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(150, 150, 150))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(30, 30, 30))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 1, 1190, 100));

        DASH.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(0, 51, 102));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image-96x96_4.png"))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("TOTAL USERS");

        total_users.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        total_users.setForeground(new java.awt.Color(255, 255, 255));
        total_users.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        total_users.setText("0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(total_users, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(53, 53, 53))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(total_users, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        jPanel10.setBackground(new java.awt.Color(0, 51, 102));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image-96x96.png"))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("ACTIVE USERS");

        active_users.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        active_users.setForeground(new java.awt.Color(255, 255, 255));
        active_users.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        active_users.setText("0");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jLabel8))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(active_users, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(active_users, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addContainerGap())
        );

        jPanel11.setBackground(new java.awt.Color(0, 51, 102));

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image-96x96_1.png"))); // NOI18N

        jLabel26.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("DAILY TIME CONS.");

        daily_time.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        daily_time.setForeground(new java.awt.Color(255, 255, 255));
        daily_time.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        daily_time.setText("0");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(daily_time, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel25)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(daily_time, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addContainerGap())
        );

        jPanel12.setBackground(new java.awt.Color(0, 51, 102));

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image-96x96_2.png"))); // NOI18N

        jLabel29.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("PC AVAILABLE");

        pcavail.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        pcavail.setForeground(new java.awt.Color(255, 255, 255));
        pcavail.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pcavail.setText("0");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pcavail, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel28)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pcavail, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addContainerGap())
        );

        jPanel13.setBackground(new java.awt.Color(0, 51, 102));

        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image-96x96_5.png"))); // NOI18N

        wawa.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        wawa.setForeground(new java.awt.Color(255, 255, 255));
        wawa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        wawa.setText("TOTAL TIME CONS.");

        total_time.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        total_time.setForeground(new java.awt.Color(255, 255, 255));
        total_time.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        total_time.setText("0");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel31))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(wawa)
                            .addComponent(total_time, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wawa, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(total_time, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addContainerGap())
        );

        jButton10.setBackground(new java.awt.Color(0, 51, 102));
        jButton10.setFont(new java.awt.Font("Arial Black", 1, 11)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("REFRESH");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jPanel14.setBackground(new java.awt.Color(0, 51, 102));

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image-96x96_3.png"))); // NOI18N

        wawa1.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        wawa1.setForeground(new java.awt.Color(255, 255, 255));
        wawa1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        wawa1.setText("TOTAL INCOME");

        income.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        income.setForeground(new java.awt.Color(255, 255, 255));
        income.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        income.setText("0");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(income, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                .addComponent(wawa1)
                                .addGap(13, 13, 13))))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel32)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wawa1, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(income, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32)
                .addContainerGap())
        );

        javax.swing.GroupLayout DASHLayout = new javax.swing.GroupLayout(DASH);
        DASH.setLayout(DASHLayout);
        DASHLayout.setHorizontalGroup(
            DASHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DASHLayout.createSequentialGroup()
                .addContainerGap(842, Short.MAX_VALUE)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(DASHLayout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addGroup(DASHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(101, 101, 101)
                .addGroup(DASHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(DASHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60))
        );
        DASHLayout.setVerticalGroup(
            DASHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DASHLayout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addGroup(DASHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addGroup(DASHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabs.addTab("dashboard", DASH);

        ADD.setBackground(new java.awt.Color(255, 255, 255));

        nametf.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "NAME:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("   TIME");
        jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel6.setMaximumSize(new java.awt.Dimension(50, 17));
        jLabel6.setMinimumSize(new java.awt.Dimension(50, 17));

        timecb.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        timecb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5MNS", "1HR", "2HRS", "3HRS", "4HRS", "5HRS" }));
        timecb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timecbActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel11.setText("   PC NO.");
        jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel11.setPreferredSize(new java.awt.Dimension(38, 16));

        pcnocb.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        pcnocb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PC 01", "PC 02", "PC 03", "PC 04", "PC 05", "PC 06" }));

        jButton7.setBackground(new java.awt.Color(0, 51, 102));
        jButton7.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("ADD");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Custom-Icon-Design-Pretty-Office-2-Contact.48.png"))); // NOI18N

        currentTB.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        currentTB.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NAME", "TIME", "PC No."
            }
        ));
        currentTB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                currentTBMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                currentTBMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(currentTB);

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Oxygen-Icons.org-Oxygen-Apps-preferences-system-time.48-40x40.png"))); // NOI18N

        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image-96x96-43x43.png"))); // NOI18N

        user_table.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        user_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NAME", "TIME", "PC No."
            }
        ));
        jScrollPane9.setViewportView(user_table);

        jLabel39.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("CURRENT USERS");

        jLabel40.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("ALL USERS");

        jButton8.setBackground(new java.awt.Color(0, 51, 102));
        jButton8.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("REFRESH");
        jButton8.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image-43x43.png"))); // NOI18N

        jLabel15.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel15.setText("   DATE");
        jLabel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel15.setPreferredSize(new java.awt.Dimension(38, 16));

        javax.swing.GroupLayout ADDLayout = new javax.swing.GroupLayout(ADD);
        ADD.setLayout(ADDLayout);
        ADDLayout.setHorizontalGroup(
            ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ADDLayout.createSequentialGroup()
                .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ADDLayout.createSequentialGroup()
                        .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ADDLayout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(ADDLayout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addGap(10, 10, 10))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ADDLayout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(ADDLayout.createSequentialGroup()
                                                .addComponent(jLabel38)
                                                .addGap(5, 5, 5))
                                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ADDLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ADDLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(timecb, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(nametf)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ADDLayout.createSequentialGroup()
                                .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pcnocb, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(DATECHOOSER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(49, 49, 49))
                    .addGroup(ADDLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)))
                .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ADDLayout.createSequentialGroup()
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(185, 185, 185))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ADDLayout.createSequentialGroup()
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(138, 138, 138))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ADDLayout.createSequentialGroup()
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)))
                .addGap(43, 43, 43))
        );
        ADDLayout.setVerticalGroup(
            ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ADDLayout.createSequentialGroup()
                .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ADDLayout.createSequentialGroup()
                        .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ADDLayout.createSequentialGroup()
                                .addGap(198, 198, 198)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ADDLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(nametf, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                            .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(timecb, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(ADDLayout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ADDLayout.createSequentialGroup()
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ADDLayout.createSequentialGroup()
                        .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pcnocb))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41)
                            .addGroup(ADDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(DATECHOOSER, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab("add users", ADD);

        USERS.setBackground(new java.awt.Color(255, 255, 255));

        dailyuser.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        dailyuser.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Name", "Pc No.", "Time", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(dailyuser);

        print.setBackground(new java.awt.Color(0, 51, 102));
        print.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        print.setForeground(new java.awt.Color(255, 255, 255));
        print.setText("Print Transaction");
        print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printActionPerformed(evt);
            }
        });

        calculate.setBackground(new java.awt.Color(0, 51, 102));
        calculate.setFont(new java.awt.Font("Arial Black", 1, 12)); // NOI18N
        calculate.setForeground(new java.awt.Color(255, 255, 255));
        calculate.setText("Compute Transaction");
        calculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout USERSLayout = new javax.swing.GroupLayout(USERS);
        USERS.setLayout(USERSLayout);
        USERSLayout.setHorizontalGroup(
            USERSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, USERSLayout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addComponent(calendar, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(USERSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, USERSLayout.createSequentialGroup()
                        .addComponent(calculate)
                        .addGap(40, 40, 40)
                        .addComponent(print, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(112, 112, 112))
                    .addGroup(USERSLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(74, 74, 74))))
        );
        USERSLayout.setVerticalGroup(
            USERSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, USERSLayout.createSequentialGroup()
                .addContainerGap(114, Short.MAX_VALUE)
                .addGroup(USERSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, USERSLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, USERSLayout.createSequentialGroup()
                        .addComponent(calendar, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(140, 140, 140)))
                .addGroup(USERSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(calculate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(print, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33))
        );

        tabs.addTab("users", USERS);

        ABOUT.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Blue_professional_business_presentation__20240520_012610_0000[1] (3).png"))); // NOI18N

        javax.swing.GroupLayout ABOUTLayout = new javax.swing.GroupLayout(ABOUT);
        ABOUT.setLayout(ABOUTLayout);
        ABOUTLayout.setHorizontalGroup(
            ABOUTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ABOUTLayout.createSequentialGroup()
                .addContainerGap(42, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addGap(38, 38, 38))
        );
        ABOUTLayout.setVerticalGroup(
            ABOUTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ABOUTLayout.createSequentialGroup()
                .addContainerGap(83, Short.MAX_VALUE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        tabs.addTab("about", ABOUT);

        OUT.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout OUTLayout = new javax.swing.GroupLayout(OUT);
        OUT.setLayout(OUTLayout);
        OUTLayout.setHorizontalGroup(
            OUTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 975, Short.MAX_VALUE)
        );
        OUTLayout.setVerticalGroup(
            OUTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 602, Short.MAX_VALUE)
        );

        tabs.addTab("LOGOUT", OUT);

        jPanel1.add(tabs, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 0, 980, 630));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
        
    }//GEN-LAST:event_jLabel17MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        tabs.setSelectedIndex(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        tabs.setSelectedIndex(1);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        tabs.setSelectedIndex(2);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        String a = nametf.getText();
    String c = (String) pcnocb.getSelectedItem();
    String b = (String) timecb.getSelectedItem();
    java.util.Date selectedDate = DATECHOOSER.getDate();

    // Check if any field is empty
    if (a.isEmpty() || c == null || b == null || selectedDate == null) {
        JOptionPane.showMessageDialog(null, "Please fill in all fields!");
    } else {
        try {
            java.sql.Date sqlSelectedDate = new java.sql.Date(selectedDate.getTime());

            pst = conn.prepareStatement("INSERT INTO currentuser (username, pcno, time, date) VALUES (?, ?, ?, ?)");
            pst.setString(1, a);
            pst.setString(2, c);
            pst.setString(3, b);
            pst.setDate(4, sqlSelectedDate);
            pst.executeUpdate();

            pst = conn.prepareStatement("INSERT INTO userss (username, pcno, time, date) VALUES (?, ?, ?, ?)");
            pst.setString(1, a);
            pst.setString(2, c);
            pst.setString(3, b);
            pst.setDate(4, sqlSelectedDate);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Account Created!");

            nametf.setText("");
            pcnocb.setSelectedIndex(0);
            timecb.setSelectedIndex(0);
            //DATECHOOSER.setDate(null);

            // Update both tables in the UI
            set_users();
            set_dashboard("userss");
            set_dashboard("currentuser");
            set_dashboardd();
            set_dashboarddd();
            set_current_users();
            set_pcdash();
            set_income_dashboard();
            pcnocb.removeItem(c);
            scheduleNotification(a, c, b);

            // Retrieve and display users with the selected date in the table
            retrieveUsersByDate(sqlSelectedDate);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while creating account!");
        }
    }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void currentTBMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_currentTBMouseReleased
        if (evt.isPopupTrigger()) { 
            pop.show(currentTB, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_currentTBMouseReleased

    private void ENDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ENDActionPerformed
         int selectedRow = currentTB.getSelectedRow();
    // Check if any row is selected
    if (selectedRow != -1) {
        // Get the data from the selected row
        String uname = currentTB.getValueAt(selectedRow, 0).toString();
        String pcNo = currentTB.getValueAt(selectedRow, 1).toString(); // Assuming PC number is in column index 1
        
        // Remove the selected row from the table
        DefaultTableModel model = (DefaultTableModel) currentTB.getModel();
        model.removeRow(selectedRow);
        
        // Call a method to delete the record from the database
        deleteData(uname, pcNo);
        
        // Add the released PC number back to the dropdown
        pcnocb.addItem(pcNo);
    } else {
        JOptionPane.showMessageDialog(null, "No row selected. Please select a row to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_ENDActionPerformed

    private void currentTBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_currentTBMousePressed
        
    }//GEN-LAST:event_currentTBMousePressed

    private void timecbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timecbActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_timecbActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        refreshSystem();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void EDITActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EDITActionPerformed
        updateData();
    }//GEN-LAST:event_EDITActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        refreshSystem();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void calendarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calendarMouseClicked
        
    }//GEN-LAST:event_calendarMouseClicked

    private void printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printActionPerformed
        try {
        // Get the selected date from the JDateChooser
        Date selectedDate = calendar.getDate();
        
        // Check if a date is selected
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(null, "Please select a date before printing the receipt.");
            return;
        }
        
        // Create an instance of the receipt frame
        receipt receiptFrame = new receipt();
        
        // Call the method to print users for the selected date
        receiptFrame.printUsersForDate(selectedDate);
        
        // Set the visibility of the receipt frame to true
        receiptFrame.setVisible(true);
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error occurred while printing receipt: " + ex.getMessage());
    }
    }//GEN-LAST:event_printActionPerformed

    private void calculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateActionPerformed
        try {
        // Get the selected date from the JDateChooser
        Date selectedDate = calendar.getDate();
        
        // Check if a date is selected
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(null, "Please select a date before printing the receipt.");
            return;
        }
        
        // Create an instance of the receipt frame
        receipt receiptFrame = new receipt();
        
        // Call the method to print users for the selected date
        receiptFrame.calculateAndDisplayFee(selectedDate);  
        
        // Set the visibility of the receipt frame to true
        receiptFrame.setVisible(true);
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error occurred while printing receipt: " + ex.getMessage());
    }
    }//GEN-LAST:event_calculateActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
                                                
    // Show a confirmation dialog
    int response = JOptionPane.showConfirmDialog(null, "Do you want to log out?", "Log Out",
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    
    // Check if the user chose "Yes"
    if (response == JOptionPane.YES_OPTION) {
        // Close the current window
        this.dispose();
        
        // Create and show the login screen
        LOGIN login = new LOGIN();
        login.setVisible(true);
    }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        tabs.setSelectedIndex(3);
    }//GEN-LAST:event_jButton6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TTSMAIN().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ABOUT;
    private javax.swing.JPanel ADD;
    private javax.swing.JPanel DASH;
    private com.toedter.calendar.JDateChooser DATECHOOSER;
    private javax.swing.JMenuItem EDIT;
    private javax.swing.JMenuItem END;
    private javax.swing.JPanel OUT;
    private javax.swing.JPanel USERS;
    private javax.swing.JLabel active_users;
    private javax.swing.JButton calculate;
    private com.toedter.calendar.JCalendar calendar;
    private javax.swing.JTable currentTB;
    private javax.swing.JLabel daily_time;
    private javax.swing.JTable dailyuser;
    private javax.swing.JLabel dt;
    private javax.swing.JLabel dt1;
    private javax.swing.JLabel income;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextField nametf;
    private javax.swing.JLabel pcavail;
    private javax.swing.JComboBox<String> pcnocb;
    private javax.swing.JPopupMenu pop;
    private javax.swing.JButton print;
    public static javax.swing.JTabbedPane tabs;
    private javax.swing.JComboBox<String> timecb;
    private javax.swing.JLabel total_time;
    private javax.swing.JLabel total_users;
    private javax.swing.JTable user_table;
    private javax.swing.JLabel wawa;
    private javax.swing.JLabel wawa1;
    // End of variables declaration//GEN-END:variables
    private void deleteData(String uname, String pcNo) {
    if (conn != null) {
        try {
            // Prepare the SQL statement to delete the user record
            String deleteSql = "DELETE FROM currentuser WHERE username = ? AND pcno = ?";
            System.out.println("SQL Query: " + deleteSql); // Print the SQL query
            System.out.println("Deleting record for user: " + uname + " on " + pcNo); // Print user details

            PreparedStatement deletePst = conn.prepareStatement(deleteSql);
            deletePst.setString(1, uname);
            deletePst.setString(2, pcNo);

            // Execute the delete statement
            int rowsAffected = deletePst.executeUpdate();

            // Check if the deletion was successful
            if (rowsAffected > 0) {
                String message = "Record deleted for user " + uname + " on " + pcNo;
                JOptionPane.showMessageDialog(null, message, "DELETE USER", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String message = "Failed to delete record for user " + uname + " on " + pcNo;
                JOptionPane.showMessageDialog(null, message, "FAILED TO DELETE", JOptionPane.ERROR_MESSAGE);
            }

            // Close the statement
            loadAvailablePCNumbers();
            deletePst.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting record: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(null, "Database connection is not available.", "Connection Error", JOptionPane.ERROR_MESSAGE);
    }
}
    private void loadAvailablePCNumbers() {
    // Check if the JComboBox is empty
    if (pcnocb.getItemCount() == 0) {
        try {
            // Establish a database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ttsdatabase", "root", "");

            // Create a statement
            Statement statement = conn.createStatement();

            // Execute a query to retrieve PC numbers chosen by users
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT pcno FROM currentuser");

            // Create a HashSet to store chosen PC numbers
            Set<String> chosenPCNumbers = new HashSet<>();
            while (resultSet.next()) {
                chosenPCNumbers.add(resultSet.getString("pcno"));
            }

            // Define the range of PC numbers
            int startNumber = 1;
            int endNumber = 6;

            // Generate and add only the available PC numbers to the JComboBox
            for (int i = startNumber; i <= endNumber; i++) {
                String pcNumber = "PC" + String.format("%02d", i);
                if (!chosenPCNumbers.contains(pcNumber)) {
                    pcnocb.addItem(pcNumber);
                }
            }

            // Close the resources
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading available PC numbers: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    private void loadTableData() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ttsdatabase", "root", "");
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM currentuser");
            DefaultTableModel model = (DefaultTableModel) currentTB.getModel();
            model.setRowCount(0);
            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getString("username"),
                    resultSet.getString("pcno"),
                    resultSet.getString("time")
                };
                model.addRow(row);
            }
            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading table data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateData() {
    int selectedRow = currentTB.getSelectedRow();
    if (selectedRow != -1) {
        String uname = currentTB.getValueAt(selectedRow, 0).toString();
        String currentTime = currentTB.getValueAt(selectedRow, 2).toString();
        String newTime = JOptionPane.showInputDialog(this, "Enter New Time for user " + uname + ":", currentTime);
        if (newTime != null) {
            Connection conn = null;
            PreparedStatement updatePst = null;
            try {
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ttsdatabase", "root", "");
                conn.setAutoCommit(false); // Start a transaction

                // Update currentuser table
                String updateSql = "UPDATE currentuser SET time = ? WHERE username = ?";
                updatePst = conn.prepareStatement(updateSql);
                updatePst.setString(1, newTime);
                updatePst.setString(2, uname);
                int rowsAffected = updatePst.executeUpdate();

                // Update userss table
                String updateUserTableSql = "UPDATE userss SET time = ? WHERE username = ?";
                PreparedStatement updateUserTablePst = conn.prepareStatement(updateUserTableSql);
                updateUserTablePst.setString(1, newTime);
                updateUserTablePst.setString(2, uname);
                int rowsAffectedUserTable = updateUserTablePst.executeUpdate();

                // Commit the transaction if both updates were successful
                if (rowsAffected > 0 && rowsAffectedUserTable > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Time updated successfully for user " + uname);
                    loadTableData(); // Refresh the table
                } else {
                    conn.rollback(); // Rollback the transaction if any update fails
                    JOptionPane.showMessageDialog(this, "Failed to update time for user " + uname, "Update Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                // Rollback the transaction and handle the exception
                try {
                    if (conn != null) {
                        conn.rollback();
                    }
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating time: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Close resources and restore auto-commit mode
                try {
                    if (updatePst != null) {
                        updatePst.close();
                    }
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error closing resources: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a user to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
    }
}
    

    private void retrieveUsersByDate(java.sql.Date selectedDate) {
    try {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Username");
        tableModel.addColumn("Date");

        String query = "SELECT username, date FROM userss WHERE date = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setDate(1, selectedDate);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String username = rs.getString("username");
            String date = rs.getString("date");
            tableModel.addRow(new Object[]{username, date});
        }

        rs.close();
        pstmt.close();
        
        // Set the table model to display the retrieved users
        dailyuser.setModel(tableModel);
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error occurred while retrieving users by date!");
    }
}
    private void refreshSystem() {
    set_users();
    set_dashboard("currentuser");
    set_current_users();
    set_dashboarddd();
    set_dashboardd();
    set_pcdash();
    set_income_dashboard();
    
}
    

    private void fetchAndDisplayUsers(Date selectedDate) {
    try {
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Username");
        tableModel.addColumn("PC No");
        tableModel.addColumn("Time");
        tableModel.addColumn("Date");
        String query = "SELECT username, pcno, time, date FROM userss WHERE date = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setDate(1, sqlDate);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            String username = rs.getString("username");
            String pcno = rs.getString("pcno");
            String time = rs.getString("time");
            String date = rs.getString("date");
            tableModel.addRow(new Object[]{username, pcno, time, date});
        }
        rs.close();
        pstmt.close();
        dailyuser.setModel(tableModel);
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error occurred while retrieving users by date!");
    }
}
    private void startDateTimeUpdater() {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Get current date and time
                Date now = new Date();

                // Format date and time
                String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(now);
                String timeStr = new SimpleDateFormat("HH:mm:ss").format(now);

                // Update the labels
                SwingUtilities.invokeLater(() -> {
                    dt1.setText("Date: " + dateStr);
                    dt.setText("Time: " + timeStr);
                });
            }
        }, 0, 1000); // Update every second
    }
    private void scheduleNotification(String username, String pcno, String duration) {
        int durationInMinutes = 0;

        // Parse the duration string
        if (duration.contains("HRS") || duration.contains("HR")) {
            durationInMinutes = Integer.parseInt(duration.replaceAll("[^0-9]", "")) * 60;
        } else if (duration.contains("MNS")) {
            durationInMinutes = Integer.parseInt(duration.replaceAll("[^0-9]", ""));
        }

        // Calculate the end time and the notification times
        long durationInMillis = durationInMinutes * 60000L;
        long notificationBeforeEndDelay = durationInMillis - (1 * 60000L); // 5 minutes before end
        long notificationEndDelay = durationInMillis; // End time

        // If the duration is exactly 5 minutes, set a short delay for demonstration
        if (durationInMinutes == 1) {
            notificationBeforeEndDelay = 3000L; // 3 seconds for testing
        }

        // Schedule the 5 minutes remaining notification
        Timer timerBeforeEnd = new Timer();
        timerBeforeEnd.schedule(new TimerTask() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, username + ", you only have 5 minutes remaining on " + pcno, "Time Warning", JOptionPane.WARNING_MESSAGE);
            }
        }, notificationBeforeEndDelay);

        // Schedule the end time notification with an option to extend
        Timer timerEnd = new Timer();
        timerEnd.schedule(new TimerTask() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, username + ", your time on " + pcno + " has ended!", "Time Ended", JOptionPane.INFORMATION_MESSAGE);

                // Schedule the extend time prompt after 3 seconds
                Timer extendTimer = new Timer();
                extendTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        int option = JOptionPane.showConfirmDialog(null, "Would you like to extend your time?", "Extend Time", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            // Code to extend the user's time
                            extendUserTime(username, pcno);
                        }
                    }
                }, 3000); // 3 seconds delay
            }
        }, notificationEndDelay);
    }
    private void extendUserTime(String username, String pcno) {
    String[] options = {"5MNS", "1HR", "2HRS", "3HRS", "4HRS", "5HRS"};
    String newTime = (String) JOptionPane.showInputDialog(null, "Select additional time for " + username + " on " + pcno, "Extend Time", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

    if (newTime != null) {
        try {
            // Update the user's time in the databases
            pst = conn.prepareStatement("UPDATE currentuser SET time = ? WHERE username = ? AND pcno = ?");
            pst.setString(1, newTime);
            pst.setString(2, username);
            pst.setString(3, pcno);
            pst.executeUpdate();

            // Update userss database as well
            pst = conn.prepareStatement("UPDATE userss SET time = ? WHERE username = ? AND pcno = ?");
            pst.setString(1, newTime);
            pst.setString(2, username);
            pst.setString(3, pcno);
            pst.executeUpdate();

            // Schedule new notifications based on the extended time
            scheduleNotification(username, pcno, newTime);

            JOptionPane.showMessageDialog(null, "Time extended for " + username + " on " + pcno + " to " + newTime);

            // Update the dashboard and other UI components if necessary
            set_dashboard("currentuser");
            set_current_users();
            set_pcdash();
            set_income_dashboard();

            // Update the daily and total time dashboards
            set_dashboardd();
            set_dashboarddd();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred while extending time!");
        }
    }
}
}
