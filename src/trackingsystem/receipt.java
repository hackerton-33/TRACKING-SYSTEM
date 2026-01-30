/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package trackingsystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author Richmond
 */
public class receipt extends javax.swing.JFrame {
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    public receipt() {
        initComponents();
        conn = dbconnection.connect();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        printarea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("TRANSACTION RECEIPT");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        printarea.setColumns(20);
        printarea.setRows(5);
        jScrollPane1.setViewportView(printarea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(receipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(receipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(receipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(receipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new receipt().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea printarea;
    // End of variables declaration//GEN-END:variables
    public void printUsersForDate(Date selectedDate) {
    try {
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        String query = "SELECT username, pcno, time FROM userss WHERE date = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setDate(1, sqlDate);
        ResultSet rs = pstmt.executeQuery();

        // Clear previous text in JTextArea
        printarea.setText("");

        // Add header to the receipt
        printarea.append("\tReceipt for " + sqlDate + "\n\n");

        // Initialize summary variables
        int totalUsers = 0;
        int totalHours = 0;
        int totalMinutes = 0;

        // Add user details to the receipt
        while (rs.next()) {
            String username = rs.getString("username");
            String pcno = rs.getString("pcno");
            String time = rs.getString("time");
            printarea.append("\t---------------------------------\n");
            printarea.append("\tUsername: " + username + "\n");
            printarea.append("\tPC No: " + pcno + "\n");
            printarea.append("\tTime: " + time + "\n");
            printarea.append("\t---------------------------------\n\n");

            // Increment total users
            totalUsers++;

            // Extract numeric part of the time (e.g., "2HRS" -> 2)
            String numericPart = time.replaceAll("[^0-9]", "");
            if (!numericPart.isEmpty()) { // Check if numeric part is not empty
                int timeValue = Integer.parseInt(numericPart);
                if (time.contains("HRS") || time.contains("HR")) {
                    totalHours += timeValue;
                } else if (time.contains("MNS")) {
                    totalMinutes += timeValue;
                } else if (time.contains("5MNS")) {
                    totalMinutes += 5;
                }
            }
        }

        // Convert total minutes to hours and remaining minutes
        totalHours += totalMinutes / 60;
        totalMinutes = totalMinutes % 60;

        // Add summary information to the receipt
        printarea.append("\tTotal Users: " + totalUsers + "\n");
        printarea.append("\tTotal Time Allotted: " + totalHours + " HRS & " + totalMinutes + " MINS\n");

        rs.close();
        pstmt.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error occurred while retrieving users by date!");
    }
}
    public void calculateAndDisplayFee(Date selectedDate) {
    try {
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        String query = "SELECT time FROM userss WHERE date = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setDate(1, sqlDate);
        ResultSet rs = pstmt.executeQuery();

        // Initialize variables to store total time and total fee
        int totalTime = 0;
        int totalFee = 0;

        // Calculate total time and fee
        while (rs.next()) {
            String time = rs.getString("time");
            // Extract numeric part of the time (e.g., "2HRS" -> 2)
            String numericPart = time.replaceAll("[^0-9]", "");
            if (!numericPart.isEmpty()) {
                int hours = Integer.parseInt(numericPart);
                totalTime += hours;
                totalFee += calculateFee(hours);
            }
        }

        // Display receipt header
        printarea.append("\n\n\n\n\n\n              ------------------------------------------------\n");
        printarea.append("                         TRANSACTION RECEIPT\n");
        printarea.append("              ------------------------------------------------\n");
        printarea.append("              Date: " + selectedDate + "\n");
        printarea.append("              ------------------------------------------------\n");

        // Display individual fees and total fee
        printarea.append("              Total Time: " + totalTime + " HRS\n");
        printarea.append("              Total Income: PHP " + totalFee + ".00\n");

        // Display footer
        printarea.append("              ------------------------------------------------\n");

        rs.close();
        pstmt.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error occurred while calculating fee!");
    }
}

public int calculateFee(int hours) {
    int fee = 0;
    if (hours == 1) {
        fee = 15;
    } else if (hours == 2) {
        fee = 25;
    } else if (hours == 3) {
        fee = 35;
    } else if (hours == 4) {
        fee = 45;
    } else if (hours == 5) {
        fee = 55;
    } else if (hours == 6) {
        fee = 60;
    } else if (hours > 6) {
        fee = 60 + ((hours - 6) * 5); // Additional 5 pesos for each hour beyond 6 hours
    } else if (hours == 0) {
        fee = 5; // For 5 minutes
    }
    return fee;
}

}
