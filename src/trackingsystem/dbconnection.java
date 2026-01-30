
package trackingsystem;

import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class dbconnection {
    public static Connection connect(){
        Connection conn = null;
        try {
            if(conn == null){
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ttsdatabase", "root", "");
                Component rootPane = null;
            }
            return conn;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection Error", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }
}
